package com.example.warning.data.remote

import com.example.warning.data.remote.service.BackendApi
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java
import dagger.Module
import okhttp3.OkHttpClient
import javax.inject.Singleton
import dagger.Provides

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    private const val BASE_URL = "https://example.com/api/" // <<< BURASI ÖNEMLİ
    val backendApi: BackendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)               // <--- Bu URL'e gidecek
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)  // <--- Interface ile bağlanıyor
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideBackendApi(retrofit: Retrofit): BackendApi =
        retrofit.create(BackendApi::class.java)
}