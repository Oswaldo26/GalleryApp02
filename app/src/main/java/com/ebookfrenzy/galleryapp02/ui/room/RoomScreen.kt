package com.ebookfrenzy.galleryapp02.ui.room

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp



import android.Manifest
import android.app.Activity

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ebookfrenzy.galleryapp02.ui.gallery.GalleryMapsViewModel
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize

@Composable
fun RoomScreen(
    roomId: String,
    roomHeight: Dp = 650.dp,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            Log.d("RoomScreen", "All permissions granted. Starting beacon scanning...")
            viewModel.startScanning()
        } else {
            Log.d("RoomScreen", "Permissions not granted.")
            // Manejar el caso donde los permisos no fueron otorgados
        }
    }

    LaunchedEffect(roomId) {
        viewModel.getRoomById("galleryId1", roomId) // Ajusta el ID de la galería según sea necesario
        requestPermissionsAndStartScanning(activity, requestPermissionLauncher, viewModel)
    }

    val room by viewModel.room.collectAsState()
    val userPosition by viewModel.calculateUserPosition().collectAsState(initial = null)

    room?.let {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = it.name, modifier = Modifier.padding(bottom = 16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(roomHeight)
                    .padding(16.dp)
            ) {
                var canvasSize by remember { mutableStateOf(IntSize.Zero) }

                Canvas(modifier = Modifier.matchParentSize().onGloballyPositioned { coordinates ->
                    canvasSize = coordinates.size
                }) {
                    // Dibujar el rectángulo de la habitación
                    drawRect(
                        color = Color.LightGray,
                        size = canvasSize.toSize(),
                        style = Stroke(width = 2f)
                    )
                }

                // Dibujar las imágenes dentro del rectángulo
                val circleRadius = 30f
                val circleDiameter = circleRadius * 2
                val circlePadding = 120f
                val circlePositions = listOf(
                    Offset(circlePadding, circlePadding),
                    Offset(canvasSize.width - circleDiameter - circlePadding, circlePadding),
                    Offset(circlePadding, canvasSize.height - circleDiameter - circlePadding),
                    Offset(canvasSize.width - circleDiameter - circlePadding, canvasSize.height - circleDiameter - circlePadding)
                )

                circlePositions.forEachIndexed { index, position ->
                    if (index < it.imageUrls.size) {
                        val imageUrl = it.imageUrls[index]
                        ImageWithOffset(
                            imageUrl = imageUrl,
                            position = position,
                            size = circleDiameter.dp
                        )
                    }
                }

                // Dibujar la posición del usuario
                userPosition?.let { position ->
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            color = Color.Red,
                            radius = circleRadius,
                            center = position
                        )
                    }
                }
            }
        }
    } ?: run {
        Text(text = "Room not found", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ImageWithOffset(imageUrl: String, position: Offset, size: Dp) {
    Box(
        modifier = Modifier
            .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
            .size(size)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size)
        )
    }
}

private fun requestPermissionsAndStartScanning(
    activity: Activity?,
    requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    viewModel: RoomViewModel
) {
    val permissions = listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionsToRequest = permissions.filter {
        ContextCompat.checkSelfPermission(activity!!, it) != PackageManager.PERMISSION_GRANTED
    }

    if (permissionsToRequest.isNotEmpty()) {
        Log.d("RoomScreen", "Requesting permissions: $permissionsToRequest")
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    } else {
        Log.d("RoomScreen", "Permissions already granted. Starting beacon scanning...")
        viewModel.startScanning()
    }
}
