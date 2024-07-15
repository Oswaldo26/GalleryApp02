package com.ebookfrenzy.galleryapp02.ui.room



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.ebookfrenzy.galleryapp02.data.model.RoomModel
import com.ebookfrenzy.galleryapp02.data.repository.GalleryMapsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(private val repository: GalleryMapsRepository) : ViewModel() {

    private val _room = MutableStateFlow<RoomModel?>(null)
    val room: StateFlow<RoomModel?> get() = _room

    fun getRoomById(galleryId: String, roomId: String) {
        viewModelScope.launch {
            val gallery = repository.fetchGallery(galleryId)
            _room.value = gallery?.rooms?.get(roomId)
        }
    }
}
