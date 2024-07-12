package com.ebookfrenzy.galleryapp02.data.repository





import android.util.Log
import com.ebookfrenzy.galleryapp02.data.model.Gallery
import com.ebookfrenzy.galleryapp02.data.remote.api.FirebaseService

import com.ebookfrenzy.galleryapp02.utils.Resource
import javax.inject.Inject

class GalleryRepository @Inject constructor(private val firebaseService: FirebaseService) {
    suspend fun getGalleries(): Resource<List<Gallery>> {
        return try {
            val galleries = firebaseService.getGalleries()
            Log.d("GalleryRepository", "Galleries fetched: ${galleries.size}")
            galleries.forEach { Log.d("GalleryRepository", "Gallery: $it") }
            if (galleries.isNotEmpty()) {
                Resource.Success(galleries)
            } else {
                Resource.Error("No galleries found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
