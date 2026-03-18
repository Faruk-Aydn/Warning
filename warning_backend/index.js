const {onRequest} = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const {Timestamp} = require("firebase-admin/firestore");

if (admin.apps.length === 0) {
  admin.initializeApp();
}

const db = admin.firestore();
const fcm = admin.messaging();

exports.sendEmergency = onRequest({cors: true}, async (req, res) => {
  const {latitude, longitude, senderId} = req.body;
  if (!senderId) {
    return res.status(400).send({error: "senderId eksik!"});
  }

  try {
    const sDoc = await db.collection("profiles").doc(senderId).get();
    const sData = sDoc.data();
    const sName = (sDoc.exists && sData) ? (sData.name || "İsimsiz") :
      "Bilinmeyen";

    const contactsSnapshot = await db.collection("contacts")
        .where("addingId", "==", senderId)
        .where("isActiveUser", "==", true)
        .where("isConfirmed", "==", true)
        .get();

    if (contactsSnapshot.empty) {
      return res.status(200).send({successCount: 0, failureCount: 0});
    }

    const batch = db.batch();
    const tokens = [];

    for (const doc of contactsSnapshot.docs) {
      const contact = doc.data();
      const msg = contact.specialMessage || "Yardım edin!";
      const hasLoc = contact.isLocationSend === true && latitude && longitude;
      const finalLoc = hasLoc ? {
        lat: latitude, lng: longitude} : {lat: 0, lng: 0};

      const hRef = db.collection("emergencyHistory").doc();
      batch.set(hRef, {
        senderUid: senderId,
        senderName: sName,
        receiverUid: contact.addedId || "N/A",
        receiverPhone: contact.phone || "Bilinmiyor",
        message: msg,
        location: finalLoc,
        timestamp: Timestamp.now(),
        status: "sent",
      });

      if (contact.addedId) {
        const rDoc = await db.collection("profiles").doc(contact.addedId).get();
        if (rDoc.exists && rDoc.data().fcmToken) {
          tokens.push(rDoc.data().fcmToken);
        }
      }
    }
    await batch.commit();

    if (tokens.length > 0) {
      const payload = {
        tokens: tokens,
        notification: {
          title: "ACİL DURUM UYARISI!",
          body: `${sName} yardıma ihtiyaç duyuyor!`,
        },
        data: {
          latitude: String(latitude || 0),
          longitude: String(longitude || 0),
          senderName: sName,
          type: "EMERGENCY",
        },
      };
      await fcm.sendEachForMulticast(payload);
    }
    return res.status(200).send({successCount: tokens.length, failureCount: 0});
  } catch (error) {
    return res.status(500).send({error: error.message});
  }
});
