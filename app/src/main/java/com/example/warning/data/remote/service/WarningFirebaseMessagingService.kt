package com.example.warning.data.remote.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.warning.MainActivity
import com.example.warning.R // R sınıfının doğru import edildiğinden emin ol
import com.example.warning.domain.usecase.UpdateFCMTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class WarningFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var updateFCMTokenUseCase: UpdateFCMTokenUseCase

    private val TAG = "WarningFCM"

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    // Uygulama AÇIKKEN (Foreground) mesaj gelirse burası çalışır
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Mesaj alındı: ${remoteMessage.from}")

        // 1. Data payload kontrolü (Bizim latitude, longitude burada)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data Payload: ${remoteMessage.data}")
            // Buradaki veriyi alıp işlem yapabilirsin (Örn: Haritayı aç)
        }

        // 2. Notification payload kontrolü (Başlık ve Gövde)
        // Backend'den hem 'notification' hem 'data' gönderdiğimiz için burası dolu gelir.
        remoteMessage.notification?.let {
            Log.d(TAG, "Bildirim Başlığı: ${it.title}, Gövde: ${it.body}")
            // Uygulama açıkken bildirimi elle oluşturup gösteriyoruz
            sendNotification(it.title, it.body)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // PendingIntent güvenliği için FLAG_IMMUTABLE (Android 12+)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "emergency_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Kendi ikonunu koyabilirsin (R.drawable.ic_warning gibi)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_MAX) // En yüksek öncelik (Heads-up)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android Oreo (API 26) ve üzeri için kanal oluşturmak şarttır
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Acil Durum Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH // Yüksek önem
            )
            channel.description = "Acil durum mesajlarını gösterir"
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirimi göster (ID rastgele olsun ki üst üste binmesin)
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }

    private fun sendRegistrationToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateFCMTokenUseCase.execute(token)
            } catch (e: Exception) {
                Log.e(TAG, "Token update error", e)
            }
        }
    }
}