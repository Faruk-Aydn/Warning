// warning-backend/index.js

// v2 HTTPS fonksiyonu ve logger importları
const { onRequest } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Android tarafından çağrılacak basit HTTP fonksiyon.
// Şimdilik sadece gelen datayı logluyor ve sahte bir cevap döndürüyor.
exports.sendEmergency = onRequest((req, res) => {
  // Sadece POST istek kabul ediyoruz
  if (req.method !== "POST") {
    return res.status(405).json({ message: "Only POST allowed" });
  }

  const { latitude, longitude, senderId } = req.body || {};

  logger.info("Emergency request received", {
    latitude,
    longitude,
    senderId,
  });

  // Şimdilik sahte sonuç
  const response = {
    successCount: 3,
    failureCount: 1,
  };

  return res.status(200).json(response);
});
