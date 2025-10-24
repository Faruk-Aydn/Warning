const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendEmergencyMessage = functions.https.onCall(async (data, context) => {
    const { senderId, receiverId, message, title } = data;

    console.log("LOG: Alıcı ID:", receiverId, "Çekilen FCM Token:", senderId, "message: ", message, "tittle", title); // ❗ Eklenecek (Kritik)
        
    if (!senderId || !receiverId || !message || !title) {
            console.log("LOG: Alıcı ID:", receiverId, "Çekilen FCM Token:", senderId, "message: ", message, "tittle", title); // ❗ Eklenecek (Kritik)
        throw new functions.https.HttpsError(
            "invalid-argument",
            "Gerekli alanlar eksik"
        );
    }

    try {
        // 1. Alıcının FCM token'ını Firestore'dan çek
        const receiverDoc = await admin.firestore().collection("profiles").doc(receiverId).get();
        
        if (!receiverDoc.exists) {
            console.error("HATA: Alıcı profili Firestore'da bulunamadı", receiverId); // ❗ Eklenecek
            throw new functions.https.HttpsError("not-found", "Alıcı bulunamadı");
        }

        const receiverData = receiverDoc.data();
        const fcmToken = receiverData.fcmToken;
        

        console.log("LOG: Alıcı ID:", receiverId, "Çekilen FCM Token:", fcmToken); // ❗ Eklenecek (Kritik)
        
        if (!fcmToken) {
            console.error("HATA: Alıcının FCM token'ı boş veya null", receiverId); // ❗ Eklenecek
            throw new functions.https.HttpsError("failed-precondition", "Alıcının FCM token'ı yok");
        }

        // 2. Bildirimi oluştur
        const payload = {
            notification: {
                title: title,
                body: message,
            },
            data: {
                senderId: senderId,
                receiverId: receiverId,
            },
        };

        // 3. Bildirimi gönder
        const response = await admin.messaging().sendToDevice(fcmToken, payload);

        console.log("LOG: Bildirim Başarılı. Sonuç:", response); // ❗ Eklenecek
        
        console.log("Bildirim sonucu:", response);
        return { success: true, result: response };
    } catch (error) {
        console.error("Hata:", error);
        throw new functions.https.HttpsError("internal", error.message);
    }
});
