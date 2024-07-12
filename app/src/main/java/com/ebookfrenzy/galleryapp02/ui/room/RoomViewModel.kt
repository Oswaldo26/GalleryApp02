package com.ebookfrenzy.galleryapp02.ui.room



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookfrenzy.galleryapp02.data.model.RoomModel
import com.ebookfrenzy.galleryapp02.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(private val roomRepository: RoomRepository) : ViewModel() {
    private val _room = MutableStateFlow<RoomModel?>(null)
    val room: StateFlow<RoomModel?> get() = _room

    fun loadRoom(roomId: String) {
        viewModelScope.launch {
            _room.value = roomRepository.fetchRoom(roomId)
        }
    }
}
