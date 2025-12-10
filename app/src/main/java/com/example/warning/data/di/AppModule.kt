package com.example.warning.data.di

import android.content.Context
import androidx.room.Room
import com.example.warning.data.local.AppDatabase
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.EmergencyHistoryDao
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.local.dao.ProfileDao

import com.example.warning.data.remote.api.EmergencyApi
import com.example.warning.data.repository.EmergencyHistoryRepositoryImpl
import com.example.warning.data.repository.EmergencyRepositoryImpl
import com.example.warning.data.repository.ProfileRepositoryImpl
import com.example.warning.domain.repository.EmergencyHistoryRepository
import com.example.warning.domain.repository.EmergencyRepository

import com.example.warning.domain.repository.FirebaseRepository
import com.example.warning.domain.repository.ProfileRepository
import com.example.warning.domain.usecase.ProfileUseCases
import com.example.warning.domain.usecase.SendEmergencyMessageUseCase
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideEmergencyHistoryDao(db: AppDatabase): EmergencyHistoryDao = db.emergencyHistoryDao()

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context
    ): AppDatabase {
        return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "profile_database"
            ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()

    @Provides
    fun provideContactDao(db: AppDatabase): ContactDao = db.contactDao()

    @Provides
    fun provideLinkedDao(db: AppDatabase): LinkedDao = db.linkedDao()


    @Provides
    @Singleton
    fun provideProfileRepository(
        profileDao: ProfileDao,
        contactDao: ContactDao,
        linkedDao: LinkedDao
    ): ProfileRepository {
        return ProfileRepositoryImpl(
            profileDao,
            linkedDao,
            contactDao
        )
    }

    @Provides
    @Singleton
    fun provideProfileUseCases(repository: ProfileRepository, firebaseRepo: FirebaseRepository): ProfileUseCases {
        return ProfileUseCases(repository, firebaseRepo)
    }


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            // TODO: Backend hazır olduğunda baseUrl'i kendi API adresinle değiştir
            .baseUrl("http://10.0.2.2:5001/warning-5d457/us-central1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // 60 saniye yaptık
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }
    @Provides
    @Singleton
    fun provideEmergencyRepository(
        emergencyApi: EmergencyApi
    ): EmergencyRepository {
        return EmergencyRepositoryImpl(emergencyApi)
    }

    @Provides
    @Singleton
    fun provideEmergencyApi(retrofit: Retrofit): EmergencyApi {
        return retrofit.create(EmergencyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEmergencyHistoryRepository(
        emergencyHistoryDao: EmergencyHistoryDao
    ): EmergencyHistoryRepository {
        return EmergencyHistoryRepositoryImpl(emergencyHistoryDao)
    }
}