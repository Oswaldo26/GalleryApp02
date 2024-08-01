package com.ebookfrenzy.galleryapp02.data.repository



import com.ebookfrenzy.galleryapp02.data.model.Painting
import com.ebookfrenzy.galleryapp02.data.remote.api.FirebaseService
import com.ebookfrenzy.galleryapp02.utils.Resource
import javax.inject.Inject

class PaintingRepository @Inject constructor(private val firebaseService: FirebaseService) {
    suspend fun getPaintings(): Resource<List<Painting>> {
        return try {
            val paintings = firebaseService.getPaintings()
            if (paintings.isNotEmpty()) {
                Resource.Success(paintings)
            } else {
                Resource.Error("No paintings found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getPaintingById(paintingId: String): Resource<Painting?> {
        return try {
            val painting = firebaseService.getPainting(paintingId)
            if (painting != null) {
                Resource.Success(painting)
            } else {
                Resource.Error("Painting not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
