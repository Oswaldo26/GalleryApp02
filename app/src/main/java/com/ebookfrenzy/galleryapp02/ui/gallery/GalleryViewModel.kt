package com.ebookfrenzy.galleryapp02.ui.gallery


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookfrenzy.galleryapp02.data.model.Gallery
import com.ebookfrenzy.galleryapp02.data.repository.GalleryRepository

import com.ebookfrenzy.galleryapp02.utils.Resource

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: GalleryRepository
) : ViewModel() {

    private val _galleries = MutableStateFlow<Resource<List<Gallery>>>(Resource.Loading())
    val galleries: StateFlow<Resource<List<Gallery>>> = _galleries

    init {
        fetchGalleries()
    }

    private fun fetchGalleries() {
        viewModelScope.launch {
            _galleries.value = repository.getGalleries().also {
                if(it is Resource.Success){
                    Log.d("GalleryViewModel", "Galleries: ${it.data}")
                } else if(it is Resource.Error){
                    Log.e("GalleryViewModel", "Error: ${it.message}")
                }
            }
        }
    }
}
