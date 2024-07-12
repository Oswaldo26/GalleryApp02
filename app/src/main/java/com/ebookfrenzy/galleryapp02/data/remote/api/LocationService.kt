package com.ebookfrenzy.galleryapp02.data.remote.api



import android.annotation.SuppressLint
import android.content.Context
import com.ebookfrenzy.galleryapp02.data.model.LocationModel

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationService(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationModel? {
        val location = fusedLocationClient.lastLocation.await()
        return location?.let {
            LocationModel(it.latitude, it.longitude)
        }
    }
}
