package com.ebookfrenzy.galleryapp02.ui.maps



import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(multiplePermissionsState.allPermissionsGranted) {
        if (multiplePermissionsState.allPermissionsGranted) {
            viewModel.fetchLocation()
        }
    }

    val location by viewModel.location.collectAsState()

    Scaffold {
        Box(modifier = Modifier.fillMaxSize()) {
            if (multiplePermissionsState.allPermissionsGranted) {
                location?.let { loc ->
                    val cameraPositionState = rememberCameraPositionState()

                    LaunchedEffect(Unit) {
                        // Calcula los límites para los puntos de interés
                        val bounds = LatLngBounds.Builder()
                            .include(viewModel.pointA)
                            .include(viewModel.pointB)
                            .include(viewModel.pointC)
                            .include(LatLng(loc.latitude, loc.longitude))
                            .build()

                        // Mueve la cámara para mostrar todos los puntos de interés
                        cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 150))
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true)
                    ) {
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = viewModel.pointA),
                            title = "Centro Cultural de la Unsa"
                        )
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = viewModel.pointB),
                            title = "Centro Cultural Peruano Norteamericano"
                        )
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = viewModel.pointC),
                            title = "Alianza Francesa de Arequipa"
                        )
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                            title = "You are here"
                        )
                    }
                } ?: run {
                    Text(text = "Getting location...")
                }
            } else {
                LaunchedEffect(Unit) {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
                Text(text = "Permissions not granted")
            }
        }
    }
}