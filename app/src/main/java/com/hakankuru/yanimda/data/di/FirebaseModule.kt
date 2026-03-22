package com.hakankuru.yanimda.data.di

import com.hakankuru.yanimda.data.local.dao.ContactDao
import com.hakankuru.yanimda.data.local.dao.EmergencyHistoryDao
import com.hakankuru.yanimda.data.local.dao.LinkedDao
import com.hakankuru.yanimda.data.local.dao.ProfileDao
import com.hakankuru.yanimda.data.remote.service.FirestoreService
import com.hakankuru.yanimda.data.remote.listener.ContactRealtimeSyncManager
import com.hakankuru.yanimda.data.remote.listener.EmergencyHistorySyncManager
import com.hakankuru.yanimda.data.remote.listener.LinkedRealtimeSyncManager
import com.hakankuru.yanimda.data.remote.listener.UserRealtimeSyncManager
import com.hakankuru.yanimda.data.repository.FirebaseRepositoryImpl
import com.hakankuru.yanimda.domain.repository.FirebaseRepository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideFirebaseAuth():
            FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
//        if (BuildConfig.DEBUG) {
//            auth.useEmulator("10.0.2.2", 9099)
//            auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
//        }
        return auth
    }

    @Provides
    @Singleton
    fun provideFirestore():
            FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
//        if (BuildConfig.DEBUG) {
//            firestore.useEmulator("10.0.2.2", 8085)
//        }
        return firestore
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