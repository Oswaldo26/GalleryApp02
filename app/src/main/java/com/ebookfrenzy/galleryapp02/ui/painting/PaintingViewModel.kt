package com.ebookfrenzy.galleryapp02.ui.painting


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
class PaintingViewModel @Inject constructor(
    private val repository: PaintingRepository
) : ViewModel() {

    private val _paintings = MutableStateFlow<Resource<List<Painting>>>(Resource.Loading())
    val paintings: StateFlow<Resource<List<Painting>>> = _paintings

    init {
        fetchPaintings()
    }

    private fun fetchPaintings() {
        viewModelScope.launch {
            _paintings.value = repository.getPaintings()
        }
    }
}
