package com.example.warning.data.remote

import com.example.warning.data.remote.service.BackendApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object NetworkModule {
    private const val BASE_URL = "https://example.com/api/" // <<< BURASI ÖNEMLİ
    val backendApi: BackendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)               // <--- Bu URL'e gidecek
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)  // <--- Interface ile bağlanıyor
    }
}