package com.example.warning.data.repository

import android.util.Log
import com.example.warning.data.remote.service.BackendApi
import com.example.warning.data.remote.service.SendProfileIdRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

data class EmergencyResponse(
    val successCount: Int,
    val failureCount: Int,
    val errors: List<String> = emptyList()
)
// Repository Katmanı: HTTP çağrısını yapar ve sonucu döner. Loglama burada yapılır.
class FcmRepository(
    private val api: BackendApi
) {

    // Backend'e ID gönderme fonksiyonu
    suspend fun sendProfileIdToBackend(profileId: String): Result<EmergencyResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProfileRepository", "Backend'e ID gönderiliyor: $profileId")

                val response = api.sendProfileId(SendProfileIdRequest(profileId))

                Log.d("ProfileRepository", "ID gönderme sonucu: $response")
                Result.success(response)
            } catch (e: IOException) {
                Log.e("ProfileRepository", "Network hatası: ${e.message}")
                Result.failure(Exception("Bağlantı hatası."))
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Beklenmeyen hata: ${e.message}")
                Result.failure(Exception("Bir hata oluştu."))
            }
        }
    }
}