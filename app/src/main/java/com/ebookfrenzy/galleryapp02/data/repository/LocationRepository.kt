package com.ebookfrenzy.galleryapp02.data.repository

import android.content.Context
import com.ebookfrenzy.galleryapp02.data.model.LocationModel
import com.ebookfrenzy.galleryapp02.data.remote.api.LocationService

import kotlinx.coroutines.tasks.await

class LocationRepository(private val locationService: LocationService) {

    suspend fun getCurrentLocation(): LocationModel? {
        return locationService.getCurrentLocation()
    }
}