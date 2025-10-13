package com.example.warning.data.remote.service

import com.example.warning.data.remote.Dto.EmergencyMessageRequestDto
import com.example.warning.data.remote.Dto.EmergencyMessageResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyMessageService @Inject constructor() {
    
    companion object {
        // Firebase Cloud Function URL'inizi buraya yazın
        private const val CLOUD_FUNCTION_URL = "https://us-central1-warning-5d457.cloudfunctions.net/sendEmergencyMessage"
    }
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    suspend fun sendEmergencyMessage(request: EmergencyMessageRequestDto): EmergencyMessageResponseDto {
        return withContext(Dispatchers.IO) {
            try {
                // Request body oluştur
                val requestBody = JSONObject().apply {
                    put("senderPhoneNumber", request.senderPhoneNumber)
                }.toString()
                
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = requestBody.toRequestBody(mediaType)
                
                // HTTP request oluştur
                val httpRequest = Request.Builder()
                    .url(CLOUD_FUNCTION_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()
                
                // Request gönder
                val response = httpClient.newCall(httpRequest).execute()
                val responseBody = response.body?.string() ?: ""
                
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBody)
                    EmergencyMessageResponseDto(
                        success = jsonResponse.getBoolean("success"),
                        message = jsonResponse.optString("message", ""),
                        sentCount = jsonResponse.optInt("sentCount", 0),
                        failedCount = jsonResponse.optInt("failedCount", 0),
                        details = parseDetails(jsonResponse.optJSONArray("details"))
                    )
                } else {
                    throw Exception("HTTP ${response.code}: $responseBody")
                }
                
            } catch (e: Exception) {
                throw Exception("Acil durum mesajı gönderilemedi: ${e.message}", e)
            }
        }
    }
    
    private fun parseDetails(detailsArray: org.json.JSONArray?): List<com.example.warning.data.remote.Dto.MessageDetailDto>? {
        if (detailsArray == null) return null
        
        val details = mutableListOf<com.example.warning.data.remote.Dto.MessageDetailDto>()
        for (i in 0 until detailsArray.length()) {
            val detail = detailsArray.getJSONObject(i)
            details.add(
                com.example.warning.data.remote.Dto.MessageDetailDto(
                    contactPhone = detail.optString("contactPhone", ""),
                    contactName = detail.optString("contactName", ""),
                    success = detail.optBoolean("success", false),
                    messageId = detail.optString("messageId", null),
                    usedSpecialMessage = detail.optBoolean("usedSpecialMessage", false),
                    error = detail.optString("error", null)
                )
            )
        }
        return details
    }
}
