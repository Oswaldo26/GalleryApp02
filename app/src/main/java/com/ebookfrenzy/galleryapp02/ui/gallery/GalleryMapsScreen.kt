package com.ebookfrenzy.galleryapp02.ui.gallery

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale



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
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import com.ebookfrenzy.galleryapp02.R

@Composable
fun GalleryMapsScreen(
    viewModel: GalleryMapsViewModel = hiltViewModel(),
    onRoomClick: (String) -> Unit,
    topTrackHeight: Dp = 35.dp,
    topTrackWidth: Dp = 350.dp,
    topTrackOffsetX: Dp = -10.dp,
    topTrackOffsetY: Dp = -200.dp,
    leftTrackWidth: Dp = 35.dp,
    leftTrackHeight: Dp = 450.dp,
    leftTrackOffsetX: Dp = -90.dp,
    leftTrackOffsetY: Dp = -20.dp,
    plazaRadius: Dp = 50.dp,
    plazaOffsetX: Dp = 15.dp,
    plazaOffsetY: Dp = -55.dp,
    roomOffsets: List<Pair<Dp, Dp>> = listOf(
        -50.dp to -180.dp,
        80.dp to -180.dp,
        -50.dp to 30.dp,
        80.dp to -25.dp
    ),
    roomIconSize: Dp = 72.dp,
    trackIconSize: Dp = 72.dp,
    plazaIconSize: Dp = 100.dp
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
                    val roomWidth = 120f  // Ancho de las habitaciones aumentado
                    val roomHeight = 120f  // Altura de las habitaciones aumentada
                    val padding = 45f  // Espacio entre las habitaciones y los bordes
                    val centerX = screenSize.width / 2f  // Centro de la pantalla en X en pixeles
                    val centerY = screenSize.height / 2f  // Centro de la pantalla en Y en pixeles

                    val rooms = listOf(
                        // Parte superior de la U
                        Offset(centerX - 1.5f * (roomWidth + padding), centerY - roomHeight - padding) to Size(roomWidth, roomHeight), // Room 1
                        Offset(centerX - 0.5f * (roomWidth + padding), centerY - roomHeight - padding) to Size(roomWidth, roomHeight), // Room 2
                        Offset(centerX - 1.5f * (roomWidth + padding), centerY) to Size(roomWidth, roomHeight), // Room 4 (arriba a la izquierda)
                        Offset(centerX - 0.5f * (roomWidth + padding), centerY + roomHeight + padding) to Size(roomWidth, roomHeight)  // Room 3 (debajo de Room 4)
                    )

                    val roomIds = gallery.rooms.keys.toList()
                    rooms.forEachIndexed { index, (offset, size) ->
                        val roomId = roomIds.getOrNull(index) ?: "roomId${index + 1}"
                        val room = gallery.rooms[roomId]
                        val roomOffset = roomOffsets.getOrNull(index) ?: 0.dp to 0.dp

                        Box(
                            modifier = Modifier
                                .offset { IntOffset((offset.x + roomOffset.first.toPx()).toInt(), (offset.y + roomOffset.second.toPx()).toInt()) }
                                .size(size.width.dp, size.height.dp)
                                .clickable {
                                    requestPermissionsAndStartScanning(activity, requestPermissionLauncher, viewModel)
                                    onRoomClick(roomId)
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.room1),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )

                            room?.let {
                                Box(modifier = Modifier.padding(8.dp).align(Alignment.Center)) {
                                    Text(
                                        text = it.name,
                                        style = androidx.compose.material.MaterialTheme.typography.body1,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }

                    // Añadir la pista en la parte superior
                    Box(
                        modifier = Modifier
                            .offset { IntOffset((centerX - (topTrackWidth.toPx() / 2) + topTrackOffsetX.toPx()).toInt(), (centerY - roomHeight - padding - topTrackHeight.toPx() + topTrackOffsetY.toPx()).toInt()) }
                            .size(topTrackWidth, topTrackHeight)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.track_icon),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()

                        )
                        Text(
                            text = "",
                            modifier = Modifier.align(Alignment.Center),
                            style = androidx.compose.material.MaterialTheme.typography.body1
                        )
                    }

                    // Añadir la pista en el lado izquierdo
                    Box(
                        modifier = Modifier
                            .offset { IntOffset((centerX - roomWidth - padding - leftTrackWidth.toPx() + leftTrackOffsetX.toPx()).toInt(), (centerY - (leftTrackHeight.toPx() / 2) + leftTrackOffsetY.toPx()).toInt()) }
                            .size(leftTrackWidth, leftTrackHeight)
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawRect(
                                color = Color.Gray,
                                size = Size(leftTrackWidth.toPx(), leftTrackHeight.toPx()),
                                style = Stroke(width = 2f)
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.track_vertical),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight() // Usa todo el alto disponible
                                .width(leftTrackWidth) // Usa el ancho especificado

                        )
                        Text(
                            text = "",
                            modifier = Modifier.align(Alignment.Center),
                            style = androidx.compose.material.MaterialTheme.typography.body1
                        )
                    }

                    // Añadir la plaza en el centro
                    Box(
                        modifier = Modifier
                            .offset { IntOffset((centerX - plazaRadius.toPx() + plazaOffsetX.toPx()).toInt(), (centerY - plazaRadius.toPx() + plazaOffsetY.toPx()).toInt()) }
                            .size(plazaRadius * 2)
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.plaza_icon),
                            contentDescription = null,
                            modifier = Modifier.size(plazaIconSize)
                        )
                        Text(
                            text = "",
                            modifier = Modifier.align(Alignment.Center),
                            style = androidx.compose.material.MaterialTheme.typography.body1
                        )
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

    }
}
