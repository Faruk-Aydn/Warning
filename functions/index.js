const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendEmergencyMessage = functions.https.onCall(async (data, context) => {
    const { senderId, receiverId, message, title } = data;

    if (!senderId || !receiverId || !message) {
        throw new functions.https.HttpsError(
            "invalid-argument",
            "Gerekli alanlar eksik"
        );
    }

    try {
        // 1. Alıcının FCM token'ını Firestore'dan çek
        const receiverDoc = await admin.firestore().collection("profiles").doc(receiverId).get();

        if (!receiverDoc.exists) {
            throw new functions.https.HttpsError("not-found", "Alıcı bulunamadı");
        }

        const receiverData = receiverDoc.data();
        const fcmToken = receiverData.fcmToken;

        if (!fcmToken) {
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

        console.log("Bildirim sonucu:", response);
        return { success: true, result: response };
    } catch (error) {
        console.error("Hata:", error);
        throw new functions.https.HttpsError("internal", error.message);
    }
});
