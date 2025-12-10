const { FieldValue, GeoPoint } = require("firebase-admin/firestore");
const { onRequest } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.firestore();
const messaging = admin.messaging();

exports.sendEmergency = onRequest(async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).json({ message: "Only POST allowed" });
  }

  // 1. İsteği Al
  const { latitude, longitude, senderId } = req.body || {};

  if (!senderId) {
    return res.status(400).json({ message: "senderId zorunludur." });
  }

  logger.info("Acil durum süreci başladı:", { senderId });

  try {
    // -----------------------------------------------------------------------
    // ADIM 1: Gönderen (Sender) Profilini Çek
    // (UserDto yapısına göre: emergencyMessage, name alanları lazım) [cite: 106]
    // -----------------------------------------------------------------------
    const senderDoc = await db.collection("profiles").doc(senderId).get();

    if (!senderDoc.exists) {
      return res.status(404).json({ message: "Gönderen kullanıcı bulunamadı." });
    }

    const senderData = senderDoc.data();
    // UserDto alanları [cite: 106]
    const defaultEmergencyMsg = senderData.emergencyMessage; 
    const senderName = senderData.name || "Birisi";
    
    // -----------------------------------------------------------------------
    // ADIM 2: Contact Tablosundan Filtreleme Yap
    // ContactDto yapısına göre filtreler: [cite: 112, 114, 116]
    // - addingId == senderId (Kişiyi ekleyen benim)
    // - isConfirmed == true
    // - isTop == true
    // - isActiveUser == true
    // -----------------------------------------------------------------------
    
    const contactsQuery = await db.collection("contacts")
      .where("addingId", "==", senderId)       // Ekleyen kişi benim
      .where("isConfirmed", "==", true)        // [cite: 116]
      .where("isTop", "==", true)              // [cite: 116]
      .where("isActiveUser", "==", true)       // [cite: 114]
      .get();

    if (contactsQuery.empty) {
      logger.info("Kriterlere uyan (Onaylı, Favori, Aktif) kişi bulunamadı.");
      return res.status(200).json({ successCount: 0, failureCount: 0, message: "Uygun kişi yok" });
    }

    // -----------------------------------------------------------------------
    // ADIM 3: Her Kişi İçin Özel Veri Hazırla ve Token Bul
    // -----------------------------------------------------------------------
    
    const messagesToSend = [];
    const historyLogs = []; // "Daha sonra görebilmesi için" kayıt listesi

    // Asenkron işlemleri paralel yönetmek için Promise dizisi
    const promises = contactsQuery.docs.map(async (contactDoc) => {
      const contactData = contactDoc.data();
      const receiverId = contactData.addedId; // Bildirimin gideceği kişi [cite: 113]

      if (!receiverId) return; // Hedef ID yoksa atla

      // Alıcının (Receiver) profilini çek -> Token lazım [cite: 106]
      const receiverDoc = await db.collection("profiles").doc(receiverId).get();
      
      if (!receiverDoc.exists) return;

      const receiverData = receiverDoc.data();
      const token = receiverData.fcmToken; // [cite: 106]

      if (!token) {
        logger.warn(`Token bulunamadı: ${receiverId}`);
        return;
      }

      // --- KURAL: Mesaj İçeriği Belirleme ---
      // 1. Contact.specialMessage [cite: 114]
      // 2. User.emergencyMessage [cite: 106]
      // 3. Sabit: "yardım edin"
      let finalMessage = "yardım edin";
      
      if (contactData.specialMessage) {
        finalMessage = contactData.specialMessage;
      } else if (defaultEmergencyMsg) {
        finalMessage = defaultEmergencyMsg;
      }

      // --- KURAL: Konum Verisi (isLocationSend) ---
      // Contact.isLocationSend 
      const includeLocation = contactData.isLocationSend === true;
      const payloadLat = includeLocation ? String(latitude) : "";
      const payloadLong = includeLocation ? String(longitude) : "";

      // Bildirim Mesajı Oluştur
      const messagePayload = {
        token: token,
        notification: {
          title: "Acil Durum Mesajı", // İstenen sabit başlık
          body: finalMessage,
        },
        data: {
          type: "EMERGENCY",
          senderId: senderId,
          senderName: senderName,
          latitude: payloadLat,
          longitude: payloadLong,
          sentTime: String(Date.now()),
          hasLocation: includeLocation ? "true" : "false"
        },
        android: {
          priority: "high",
          notification: {
             priority: "max",
             channelId: "emergency_channel",
             defaultSound: true
          }
        }
      };

      messagesToSend.push(messagePayload);

      // --- Loglama (Daha sonra görmek için) ---
      historyLogs.push({
        senderId: senderId,
        receiverId: receiverId,
        receiverName: contactData.name || receiverData.name,
        senderName: senderName,
        messageContent: finalMessage,
        error: null, // Hata varsa buraya string olarak gelir
          // Konum Detayları
        hasLocation: includeLocation, // UI'da pin ikonu göstermek için hızlı flag
        location: includeLocation
             ? new GeoPoint(latitude, longitude) // <-- admin.firestore kısmını sildik
             : null,
        timestamp: FieldValue.serverTimestamp(),
        status: "attempted"
      });
    });

    // Tüm veri hazırlama işlemlerini bekle
    await Promise.all(promises);

    if (messagesToSend.length === 0) {
      return res.status(200).json({ successCount: 0, failureCount: 0, message: "Tokenlı kullanıcı bulunamadı" });
    }

    // -----------------------------------------------------------------------
    // ADIM 4: Hepsini Gönder (sendEach)
    // -----------------------------------------------------------------------
    const batchResponse = await messaging.sendEach(messagesToSend);

    // -----------------------------------------------------------------------
    // ADIM 5: Geçmişi Kaydet (History Logs)
    // "işlem bittiğinde bunu daha sonra görebilmesi gerek" maddesi için
    // -----------------------------------------------------------------------
    const batchWrite = db.batch();
    const historyCollection = db.collection("emergency_history");

    historyLogs.forEach((log, index) => {
      // Gönderim sonucunu loga ekle
      const response = batchResponse.responses[index];
      log.success = response.success;
      if (!response.success) {
        log.error = JSON.stringify(response.error);
        log.status = "failed";
      } else {
        log.status = "sent";
      }

      const newDocRef = historyCollection.doc();
      batchWrite.set(newDocRef, log);
    });

    await batchWrite.commit();

    // Yanıt
    return res.status(200).json({
      successCount: batchResponse.successCount,
      failureCount: batchResponse.failureCount,
      details: "İşlem tamamlandı ve history kaydedildi."
    });

  } catch (error) {
    logger.error("SendEmergency hatası:", error);
    return res.status(500).json({ error: error.message });
  }
});