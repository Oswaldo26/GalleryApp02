package com.ebookfrenzy.galleryapp02.ui.maps



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebookfrenzy.galleryapp02.data.model.LocationModel
import com.ebookfrenzy.galleryapp02.data.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _location = MutableStateFlow<LocationModel?>(null)
    val location: StateFlow<LocationModel?> = _location

    // Definir las coordenadas de los dos puntos específicos
    val pointA = LatLng(-16.397772680301617, -71.53749712889858) // Centro cultural de la unsa
    val pointB = LatLng(-16.395819290344026, -71.5333523977673) // Centro cultural peruano norteamericano
    val pointC = LatLng(-16.39645307741121, -71.53621277241473) // Alianza Francesa de Arequipa

    fun fetchLocation() {
        viewModelScope.launch {
            val currentLocation = locationRepository.getCurrentLocation()
            _location.value = currentLocation
        }
    }
}
