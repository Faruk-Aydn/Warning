package com.hakankuru.yanimda.data.di

import android.app.Application
import com.hakankuru.yanimda.data.repository.LocationTrackerRepositoryImpl
import com.hakankuru.yanimda.domain.repository.LocationTrackerRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    /**
     * Google'ın FusedLocationProviderClient nesnesini oluşturur.
     * Bu nesne, konum servislerine (GPS, Wifi, Hücresel) erişmemizi sağlar.
     */
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    /**
     * UseCase'lerin kullanacağı LocationTracker arayüzünü sağlar.
     * UseCase "LocationTracker" ister, Hilt ona "LocationTrackerImpl" verir.
     */
    @Provides
    @Singleton
    fun provideLocationTracker(
        locationClient: FusedLocationProviderClient,
        app: Application
    ): LocationTrackerRepository {
        return LocationTrackerRepositoryImpl(locationClient, app)
    }
}