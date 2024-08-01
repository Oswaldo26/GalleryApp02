package com.ebookfrenzy.galleryapp02.data.remote.api



import com.ebookfrenzy.galleryapp02.data.model.Gallery
import com.ebookfrenzy.galleryapp02.data.model.GalleryMapsModel
import com.ebookfrenzy.galleryapp02.data.model.Painting

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getGalleries(): List<Gallery> {
        return try {
            val snapshot = firestore.collection("galleries").get().await()
            snapshot.documents.mapNotNull { it.toObject(Gallery::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getGalleryMaps(galleryId: String): GalleryMapsModel? {
        return try {
            val snapshot = firestore.collection("galleriesMaps").document(galleryId).get().await()
            snapshot.toObject(GalleryMapsModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPaintings(): List<Painting> {
        return try {
            val snapshot = firestore.collection("paintings").get().await()
            snapshot.documents.mapNotNull { it.toObject(Painting::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getPainting(paintingId: String): Painting? {
        return try {
            val document = firestore.collection("paintings").document(paintingId).get().await()
            document.toObject(Painting::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
