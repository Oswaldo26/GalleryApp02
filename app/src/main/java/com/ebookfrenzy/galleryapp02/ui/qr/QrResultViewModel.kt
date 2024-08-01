package com.ebookfrenzy.galleryapp02.ui.qr


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookfrenzy.galleryapp02.data.model.Painting
import com.ebookfrenzy.galleryapp02.data.repository.PaintingRepository
import com.ebookfrenzy.galleryapp02.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrResultViewModel @Inject constructor(private val repository: PaintingRepository) : ViewModel() {
    private val _painting = MutableStateFlow<Resource<Painting?>>(Resource.Loading())
    val painting: StateFlow<Resource<Painting?>> get() = _painting

    fun getPaintingById(paintingId: String) {
        viewModelScope.launch {
            _painting.value = repository.getPaintingById(paintingId)
        }
    }
}
