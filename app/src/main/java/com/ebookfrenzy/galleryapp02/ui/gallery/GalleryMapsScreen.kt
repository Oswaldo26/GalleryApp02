package com.ebookfrenzy.galleryapp02.ui.gallery

import androidx.activity.compose.ManagedActivityResultLauncher


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize

@Composable
fun GalleryMapsScreen(
    viewModel: GalleryMapsViewModel = hiltViewModel(),
    onRoomClick: (String) -> Unit
) {
    val gallery by viewModel.gallery.collectAsState()
    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    val context = LocalContext.current
    val activity = context as? Activity

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            viewModel.startScanning()
        } else {
            // Manejar el caso donde los permisos no fueron otorgados
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp).onGloballyPositioned { coordinates ->
        screenSize = coordinates.size
    }) {
        gallery?.let { gallery ->
            Column {
                Text(text = gallery.name, modifier = Modifier.padding(top = 50.dp, bottom = 16.dp))

                // Dibujar el plano de la galería en forma de U
                Box(modifier = Modifier.fillMaxSize()) {
                    val roomWidth = 300f  // Ancho de las habitaciones aumentado
                    val roomHeight = 300f  // Altura de las habitaciones aumentada
                    val padding = 45f  // Espacio entre las habitaciones y los bordes
                    val centerX = screenSize.width / 2f  // Centro de la pantalla en X en pixeles
                    val centerY = screenSize.height / 2f  // Centro de la pantalla en Y en pixeles

                    val rooms = listOf(
                        // Parte superior de la U
                        Offset(centerX - 1.5f * (roomWidth + padding), centerY - roomHeight - padding) to Size(roomWidth, roomHeight), // Room 1
                        Offset(centerX - 0.5f * (roomWidth + padding), centerY - roomHeight - padding) to Size(roomWidth, roomHeight), // Room 2
                        Offset(centerX + 0.5f * (roomWidth + padding), centerY - roomHeight - padding) to Size(roomWidth, roomHeight), // Room 3
                        // Parte izquierda de la U
                        Offset(centerX - 1.5f * (roomWidth + padding), centerY) to Size(roomWidth, roomHeight), // Room 4
                        // Parte derecha de la U
                        Offset(centerX + 0.5f * (roomWidth + padding), centerY) to Size(roomWidth, roomHeight) // Room 5
                    )

                    val roomIds = gallery.rooms.keys.toList()
                    rooms.forEachIndexed { index, (offset, size) ->
                        val roomId = roomIds.getOrNull(index) ?: "roomId${index + 1}"
                        val room = gallery.rooms[roomId]

                        Canvas(
                            modifier = Modifier
                                .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
                                .size(size.width.dp, size.height.dp)
                                .clickable {
                                    requestPermissionsAndStartScanning(activity, requestPermissionLauncher, viewModel)
                                    onRoomClick(roomId)
                                }
                        ) {
                            drawRect(
                                color = Color.LightGray,
                                size = Size(roomWidth, roomHeight),
                                style = Stroke(width = 2f)
                            )

                            room?.let {
                                drawIntoCanvas { canvas ->
                                    canvas.nativeCanvas.drawText(
                                        it.name,
                                        20f,
                                        40f,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.BLACK
                                            textSize = 30f
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: run {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        }
    }
}

private fun requestPermissionsAndStartScanning(
    activity: Activity?,
    requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    viewModel: GalleryMapsViewModel
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
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    } else {
        viewModel.startScanning()
    }
}