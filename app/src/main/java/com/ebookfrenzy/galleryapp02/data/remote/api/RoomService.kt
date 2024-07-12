package com.ebookfrenzy.galleryapp02.data.remote.api

import com.ebookfrenzy.galleryapp02.data.model.RoomModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoomService(private val firestore: FirebaseFirestore) {
    suspend fun getRoom(roomId: String): RoomModel? {
        return try {
            val snapshot = firestore.collection("rooms").document(roomId).get().await()
            snapshot.toObject(RoomModel::class.java)
        } catch (e: Exception) {
            null
        }
    }
}