package com.ebookfrenzy.galleryapp02.di

import com.ebookfrenzy.galleryapp02.data.remote.api.LocationService
import com.ebookfrenzy.galleryapp02.data.repository.LocationRepository



import android.content.Context
import com.ebookfrenzy.galleryapp02.data.remote.api.GalleryService
import com.ebookfrenzy.galleryapp02.data.remote.api.RoomService
import com.google.firebase.firestore.FirebaseFirestore

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
    fun provideLocationService(@ApplicationContext context: Context): LocationService {
        return LocationService(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(locationService: LocationService): LocationRepository {
        return LocationRepository(locationService)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideGalleryService(firestore: FirebaseFirestore): GalleryService {
        return GalleryService(firestore)
    }

    @Provides
    @Singleton
    fun provideRoomService(firestore: FirebaseFirestore): RoomService {
        return RoomService(firestore)
    }
}
