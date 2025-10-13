const {setGlobalOptions} = require("firebase-functions");
const {onRequest} = require("firebase-functions/https");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");
const cors = require("cors")({origin: true});

// Initialize Firebase Admin SDK
admin.initializeApp();

// Set global options for cost control
setGlobalOptions({maxInstances: 10});

/**
 * Emergency message sending function
 * Handles sending emergency messages to confirmed top contacts
 */
exports.sendEmergencyMessage = onRequest({cors: true}, async (req, res) => {
  // Enable CORS for all requests
  cors(req, res, async () => {
    try {
      // Validate request method
      if (req.method !== "POST") {
        logger.warn("Invalid request method", {method: req.method});
        return res.status(405).json({
          success: false,
          error: "Method not allowed. Use POST.",
        });
      }

      // Extract sender phone number from request body
      const {senderPhoneNumber} = req.body;

      if (!senderPhoneNumber) {
        logger.warn("Missing sender phone number");
        return res.status(400).json({
          success: false,
          error: "Sender phone number is required",
        });
      }

      logger.info("Processing emergency message request", {
        senderPhoneNumber,
      });

      // Get sender profile information
      const senderProfile = await getSenderProfile(senderPhoneNumber);
      if (!senderProfile) {
        logger.warn("Sender profile not found", {senderPhoneNumber});
        return res.status(404).json({
          success: false,
          error: "Sender profile not found",
        });
      }

      // Get eligible contacts (confirmed and top)
      const eligibleContacts = await getEligibleContacts(senderPhoneNumber);
      if (eligibleContacts.length === 0) {
        logger.info("No eligible contacts found", {senderPhoneNumber});
        return res.status(200).json({
          success: true,
          message: "No eligible contacts found",
          sentCount: 0,
        });
      }

      // Send FCM messages to each eligible contact
      const results = await sendFCMessages(eligibleContacts, senderProfile);

      // Log results
      const successCount = results.filter((r) => r.success).length;
      const failureCount = results.filter((r) => !r.success).length;

      logger.info("Emergency message sending completed", {
        senderPhoneNumber,
        totalContacts: eligibleContacts.length,
        successCount,
        failureCount,
      });

      return res.status(200).json({
        success: true,
        message: "Emergency messages processed",
        sentCount: successCount,
        failedCount: failureCount,
        details: results,
      });
    } catch (error) {
      logger.error("Error in sendEmergencyMessage", {
        error: error.message,
        stack: error.stack,
      });
      return res.status(500).json({
        success: false,
        error: "Internal server error",
      });
    }
  });
});

/**
 * Get sender profile information from Firestore
 * @param {string} phoneNumber - The phone number to search for
 */
async function getSenderProfile(phoneNumber) {
  try {
    const profileDoc = await admin
        .firestore()
        .collection("profiles")
        .where("phoneNumber", "==", phoneNumber)
        .limit(1)
        .get();

    if (profileDoc.empty) {
      return null;
    }

    const profileData = profileDoc.docs[0].data();
    logger.info("Sender profile retrieved", {
      phoneNumber,
      name: profileData.name,
      hasEmergencyMessage: !!profileData.emergencyMessage,
    });

    return {
      id: profileDoc.docs[0].id,
      ...profileData,
    };
  } catch (error) {
    logger.error("Error getting sender profile", {
      phoneNumber,
      error: error.message,
    });
    throw error;
  }
}

/**
 * Get eligible contacts (confirmed and top) from Firestore
 * @param {string} senderPhoneNumber - The sender's phone number
 */
async function getEligibleContacts(senderPhoneNumber) {
  try {
    const contactsQuery = await admin
        .firestore()
        .collection("contacts")
        .where("ownerPhone", "==", senderPhoneNumber)
        .where("isConfirmed", "==", true)
        .where("isTop", "==", true)
        .get();

    const contacts = [];
    contactsQuery.forEach((doc) => {
      const contactData = doc.data();
      contacts.push({
        id: doc.id,
        ...contactData,
      });
    });

    logger.info("Eligible contacts retrieved", {
      senderPhoneNumber,
      contactCount: contacts.length,
    });

    return contacts;
  } catch (error) {
    logger.error("Error getting eligible contacts", {
      senderPhoneNumber,
      error: error.message,
    });
    throw error;
  }
}

/**
 * Send FCM messages to all eligible contacts
 * @param {Array} contacts - Array of contact objects
 * @param {Object} senderProfile - The sender's profile information
 */
async function sendFCMessages(contacts, senderProfile) {
  const results = [];

  for (const contact of contacts) {
    try {
      // Determine message content
      const messageContent = contact.specialMessage ||
                            senderProfile.emergencyMessage ||
                            "Emergency message from " + senderProfile.name;

      // Get FCM token for this contact
      const fcmToken = await getFCMToken(contact.phone);
      if (!fcmToken) {
        logger.warn("No FCM token found for contact", {
          contactPhone: contact.phone,
          contactName: contact.name,
        });
        results.push({
          contactPhone: contact.phone,
          contactName: contact.name,
          success: false,
          error: "No FCM token found",
        });
        continue;
      }

      // Prepare FCM message
      const message = {
        token: fcmToken,
        notification: {
          title: "Emergency Alert",
          body: messageContent,
        },
        data: {
          senderName: senderProfile.name || "Unknown",
          senderPhone: senderProfile.phoneNumber,
          messageType: "emergency",
          timestamp: new Date().toISOString(),
        },
        android: {
          priority: "high",
          notification: {
            sound: "default",
            priority: "high",
            defaultSound: true,
          },
        },
        apns: {
          payload: {
            aps: {
              sound: "default",
              badge: 1,
            },
          },
        },
      };

      // Send FCM message
      const response = await admin.messaging().send(message);
      logger.info("FCM message sent successfully", {
        contactPhone: contact.phone,
        contactName: contact.name,
        messageId: response,
        usedSpecialMessage: !!contact.specialMessage,
      });

      results.push({
        contactPhone: contact.phone,
        contactName: contact.name,
        success: true,
        messageId: response,
        usedSpecialMessage: !!contact.specialMessage,
      });
    } catch (error) {
      logger.error("Error sending FCM message", {
        contactPhone: contact.phone,
        contactName: contact.name,
        error: error.message,
      });

      results.push({
        contactPhone: contact.phone,
        contactName: contact.name,
        success: false,
        error: error.message,
      });
    }
  }

  return results;
}

/**
 * Get FCM token for a contact phone number
 * @param {string} phoneNumber - The phone number to get token for
 */
async function getFCMToken(phoneNumber) {
  try {
    const tokenDoc = await admin
        .firestore()
        .collection("fcmTokens")
        .where("phoneNumber", "==", phoneNumber)
        .limit(1)
        .get();

    if (tokenDoc.empty) {
      return null;
    }

    const tokenData = tokenDoc.docs[0].data();
    return tokenData.token;
  } catch (error) {
    logger.error("Error getting FCM token", {
      phoneNumber,
      error: error.message,
    });
    return null;
  }
}

/**
 * FCM token registration endpoint
 * Android uygulaması bu endpoint'i kullanarak FCM token'ını kaydedecek
 */
exports.registerFCMToken = onRequest({cors: true}, async (req, res) => {
  cors(req, res, async () => {
    try {
      if (req.method !== "POST") {
        return res.status(405).json({
          success: false,
          error: "Method not allowed. Use POST.",
        });
      }

      const {phoneNumber, token, deviceInfo} = req.body;

      if (!phoneNumber || !token) {
        return res.status(400).json({
          success: false,
          error: "Phone number and token are required",
        });
      }

      // FCM token'ı Firestore'a kaydet
      const tokenData = {
        phoneNumber,
        token,
        deviceInfo: deviceInfo || "Unknown Device",
        lastUpdated: admin.firestore.FieldValue.serverTimestamp(),
      };

      // Mevcut token'ı güncelle veya yeni oluştur
      const existingTokenQuery = await admin
          .firestore()
          .collection("fcmTokens")
          .where("phoneNumber", "==", phoneNumber)
          .limit(1)
          .get();

      if (!existingTokenQuery.empty) {
        // Mevcut token'ı güncelle
        const docId = existingTokenQuery.docs[0].id;
        await admin
            .firestore()
            .collection("fcmTokens")
            .document(docId)
            .update(tokenData);
      } else {
        // Yeni token oluştur
        await admin
            .firestore()
            .collection("fcmTokens")
            .add(tokenData);
      }

      logger.info("FCM token registered successfully", {
        phoneNumber,
        deviceInfo: tokenData.deviceInfo,
      });

      return res.status(200).json({
        success: true,
        message: "FCM token registered successfully",
      });
    } catch (error) {
      logger.error("Error registering FCM token", {
        error: error.message,
        stack: error.stack,
      });
      return res.status(500).json({
        success: false,
        error: "Internal server error",
      });
    }
  });
});
