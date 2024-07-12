package com.ebookfrenzy.galleryapp02.ui.gallery



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookfrenzy.galleryapp02.data.model.GalleryMapsModel
import com.ebookfrenzy.galleryapp02.data.repository.GalleryMapsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryMapsViewModel @Inject constructor(private val galleryMapsRepository: GalleryMapsRepository) : ViewModel() {
    private val _gallery = MutableStateFlow<GalleryMapsModel?>(null)
    val gallery: StateFlow<GalleryMapsModel?> get() = _gallery

    init {
        loadGallery("galleryId1")  // Asegúrate de pasar el ID correcto de la galería
    }

    private fun loadGallery(galleryId: String) {
        viewModelScope.launch {
            try {
                val galleryData = galleryMapsRepository.fetchGallery(galleryId)
                Log.d("GalleryMapsViewModel", "Gallery data loaded: $galleryData")
                _gallery.value = galleryData
            } catch (e: Exception) {
                Log.e("GalleryMapsViewModel", "Error loading gallery data", e)
            }
        }
    }
}
