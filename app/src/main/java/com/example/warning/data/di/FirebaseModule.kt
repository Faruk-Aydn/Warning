package com.example.warning.data.di

import android.util.Log
import com.example.warning.BuildConfig
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.EmergencyHistoryDao
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.remote.service.FirestoreService
import com.example.warning.data.remote.listener.ContactRealtimeSyncManager
import com.example.warning.data.remote.listener.EmergencyHistorySyncManager
import com.example.warning.data.remote.listener.LinkedRealtimeSyncManager
import com.example.warning.data.remote.listener.UserRealtimeSyncManager
import com.example.warning.data.repository.FirebaseRepositoryImpl
import com.example.warning.domain.repository.FirebaseRepository

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

@Provides
@Singleton
fun provideFirebaseAuth(): FirebaseAuth {
    return FirebaseAuth.getInstance().apply {
        if (BuildConfig.DEBUG) {
            try {
                useEmulator("10.0.2.2", 9099)
                Log.d("FirebaseModule", "Auth emulator connected")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            if (BuildConfig.DEBUG) {
                try {
                    useEmulator("10.0.2.2", 8080)

                    // Firestore ayarlarını builder ile yap
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(false) // offline persistence kapalı
                        .build()
                    this.firestoreSettings = settings

                    Log.d("FirebaseModule", "Firestore emulator connected")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }



    @Provides
    @Singleton
    fun provideUserSyncManager(
        firestore: FirebaseFirestore,
        profileDao: ProfileDao
    ): UserRealtimeSyncManager {
        return UserRealtimeSyncManager(firestore, profileDao)
    }

    @Provides
    @Singleton
    fun provideLinkedSyncManager(
        firestore: FirebaseFirestore,
        linkedDao: LinkedDao
    ): LinkedRealtimeSyncManager {
        return LinkedRealtimeSyncManager(firestore, linkedDao)
    }

    @Provides
    @Singleton
    fun provideContactSyncManager(
        firestore: FirebaseFirestore,
        contactDao: ContactDao
    ): ContactRealtimeSyncManager {
        return ContactRealtimeSyncManager(firestore, contactDao)
    }

    @Provides
    @Singleton
    fun provideEmergencyHistorySyncManager(
        firestore: FirebaseFirestore,
        dao: EmergencyHistoryDao
    ): EmergencyHistorySyncManager {
        return EmergencyHistorySyncManager(firestore, dao)
    }

    @Provides
    @Singleton
    fun provideFirestoreService(firestore: FirebaseFirestore): FirestoreService {
        return FirestoreService(firestore)
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firestoreService: FirestoreService,
        syncContact: ContactRealtimeSyncManager,
        syncManagerUser: UserRealtimeSyncManager,
        syncLinked: LinkedRealtimeSyncManager,
        syncEmergencyHistory: EmergencyHistorySyncManager
    ): FirebaseRepository {
        return FirebaseRepositoryImpl(
            firestoreService,
            syncManagerUser,
            syncLinked,
            syncContact,
            syncEmergencyHistory
        )
    }
}