package com.example.warning.data.remote.service

import com.example.warning.data.remote.Dto.EmergencyMessageRequestDto
import com.example.warning.data.remote.Dto.EmergencyMessageResponseDto
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyMessageService @Inject constructor(
    private val functions: FirebaseFunctions
) {
    suspend fun sendEmergencyMessage(
        senderId: String,
        receiverId: String,
        message: String
    ): Result<Map<String, Any>> {
        return try {
            // Function parametreleri
            val data = hashMapOf(
                "senderId" to senderId,
                "receiverId" to receiverId,
                "message" to message
            )

            // Firebase Cloud Function çağrısı
            val response = functions
                .getHttpsCallable("sendEmergencyMessage")
                .call(data)
                .await() // suspend ile bekle

            // Function sonucunu al
            val result = response.data as? Map<String, Any>
                ?: emptyMap()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
