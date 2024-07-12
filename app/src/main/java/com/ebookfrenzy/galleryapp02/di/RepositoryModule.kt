package com.ebookfrenzy.galleryapp02.di

import com.ebookfrenzy.galleryapp02.data.remote.api.FirebaseService
import com.ebookfrenzy.galleryapp02.data.remote.api.GalleryService
import com.ebookfrenzy.galleryapp02.data.remote.api.RoomService
import com.ebookfrenzy.galleryapp02.data.repository.GalleryMapsRepository
import com.ebookfrenzy.galleryapp02.data.repository.GalleryRepository
import com.ebookfrenzy.galleryapp02.data.repository.RoomRepository


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGalleryRepository(firebaseService: FirebaseService): GalleryRepository {
        return GalleryRepository(firebaseService)
    }
    @Provides
    @Singleton
    fun provideGalleryMapsRepository(galleryService: GalleryService): GalleryMapsRepository {
        return GalleryMapsRepository(galleryService)
    }

    @Provides
    @Singleton
    fun provideRoomRepository(roomService: RoomService): RoomRepository {
        return RoomRepository(roomService)
    }
}
