package com.example.warning.data.di

import android.content.Context
import androidx.room.Room
import com.example.warning.data.local.AppDatabase
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.repository.ProfileRepositoryImpl
import com.example.warning.data.repository.EmergencyMessageRepositoryImpl
import com.example.warning.domain.repository.FirebaseRepository
import com.example.warning.domain.repository.ProfileRepository
import com.example.warning.domain.repository.EmergencyMessageRepository
import com.example.warning.domain.usecase.ProfileUseCases
import com.example.warning.domain.usecase.SendEmergencyMessageUseCase
import com.example.warning.data.remote.service.EmergencyMessageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideEmergencyMessageService(): EmergencyMessageService {
        return EmergencyMessageService()
    }

    @Provides
    @Singleton
    fun provideEmergencyMessageRepository(
        emergencyMessageService: EmergencyMessageService,
        profileRepository: ProfileRepository
    ): EmergencyMessageRepository {
        return EmergencyMessageRepositoryImpl(emergencyMessageService, profileRepository)
    }

    @Provides
    @Singleton
    fun provideSendEmergencyMessageUseCase(
        emergencyMessageRepository: EmergencyMessageRepository
    ): SendEmergencyMessageUseCase {
        return SendEmergencyMessageUseCase(emergencyMessageRepository)
    }

}