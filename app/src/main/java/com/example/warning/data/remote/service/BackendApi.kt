package com.example.warning.data.remote.service

import com.example.warning.data.repository.EmergencyResponse
import retrofit2.http.Body
import retrofit2.http.POST

data class SendProfileIdRequest(
    val profileId: String
)
interface BackendApi {

    @POST("sendProfileId")
    suspend fun sendProfileId(
        @Body request: SendProfileIdRequest
    ): EmergencyResponse
}