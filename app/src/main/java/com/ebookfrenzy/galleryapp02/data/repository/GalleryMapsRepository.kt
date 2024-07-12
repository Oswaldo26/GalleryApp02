package com.ebookfrenzy.galleryapp02.data.repository


import com.ebookfrenzy.galleryapp02.data.model.GalleryMapsModel
import com.ebookfrenzy.galleryapp02.data.remote.api.GalleryService
import javax.inject.Inject

class GalleryMapsRepository @Inject constructor(private val firebaseService: GalleryService) {
    suspend fun fetchGallery(galleryId: String): GalleryMapsModel? {
        return firebaseService.getGalleryMaps(galleryId)
    }
}