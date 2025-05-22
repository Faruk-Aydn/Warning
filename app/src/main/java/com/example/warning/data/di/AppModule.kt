package com.example.warning.data.di

import android.content.Context
import androidx.room.Room
import com.example.warning.data.local.AppDatabase
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.repository.ProfileRepositoryImpl
import com.example.warning.domain.repository.ProfileRepository
import com.example.warning.domain.usecase.ProfileUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "profile_database"
        ).build()
    }

    @Provides
    fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()

    @Provides
    fun provideContactDao(db: AppDatabase): ContactDao = db.contactDao()

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileDao: ProfileDao,
        contactDao: ContactDao
    ): ProfileRepository {
        return ProfileRepositoryImpl(profileDao, contactDao)
    }

    @Provides
    @Singleton
    fun provideProfileUseCases(repository: ProfileRepository): ProfileUseCases {
        return ProfileUseCases(repository)
    }
}