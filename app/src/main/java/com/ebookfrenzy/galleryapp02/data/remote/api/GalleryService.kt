package com.ebookfrenzy.galleryapp02.data.remote.api

import com.ebookfrenzy.galleryapp02.data.model.GalleryMapsModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GalleryService(private val firestore: FirebaseFirestore) {
    suspend fun getGalleryMaps(galleryId: String): GalleryMapsModel? {
        return try {
            val snapshot = firestore.collection("galleriesMaps").document(galleryId).get().await()
            snapshot.toObject(GalleryMapsModel::class.java)
        } catch (e: Exception) {
            null
        }
    }
}