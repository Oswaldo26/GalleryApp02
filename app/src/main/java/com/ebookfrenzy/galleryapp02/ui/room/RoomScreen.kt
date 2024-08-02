package com.ebookfrenzy.galleryapp02.ui.room


import androidx.compose.ui.Alignment



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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController

@Composable
fun RoomScreen(
    navController: NavController,
    roomId: String,
    roomHeightMeters: Float = 6f, // Altura de la habitación en metros
    roomWidthMeters: Float = 5f, // Ancho de la habitación en metros
    viewModel: RoomViewModel = hiltViewModel(),
    paintingOffsets: List<Pair<Float, Float>> = listOf(
        0.5f to 1f, // Imagen 1 (izquierda)
        0.5f to 6f, // Imagen 2 (izquierda)
        0.5f to 11f, // Imagen 3 (izquierda)
        6f to 1f, // Imagen 4 (derecha)
        6f to 6f, // Imagen 5 (derecha)
        6f to 11f // Imagen 6 (derecha)
    )
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
        viewModel.getRoomById("galleryId1", roomId)
        requestPermissionsAndStartScanning(activity, requestPermissionLauncher, viewModel)
    }

    val room by viewModel.room.collectAsState()
    val userPosition by viewModel.userPosition.collectAsState(initial = null)

    room?.let {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((roomHeightMeters * 100).dp) // Ajustar altura basada en metros
                    .padding(16.dp)
            ) {
                var canvasSize by remember { mutableStateOf(IntSize.Zero) }

                Canvas(modifier = Modifier.matchParentSize().onGloballyPositioned { coordinates ->
                    canvasSize = coordinates.size
                }) {
                    // Dibujar el rectángulo de la habitación con esquinas redondeadas
                    drawRoundRect(
                        color = Color.LightGray,
                        size = canvasSize.toSize(),
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        style = Stroke(width = 8f) // Ajustar el grosor de la línea
                    )
                }

                // Dibujar las imágenes dentro del rectángulo
                val circleRadius = 40f
                val circleDiameter = circleRadius * 2

                paintingOffsets.forEachIndexed { index, (offsetX, offsetY) ->
                    if (index < it.imageUrls.size) {
                        val imageUrl = it.imageUrls[index]
                        ImageWithOffset(
                            imageUrl = imageUrl,
                            position = Offset(offsetX * 100, offsetY * 100), // Convertir metros a unidades canvas
                            size = circleDiameter.dp,
                            title = it.name,
                            navController = navController// Pasar el nombre de la pintura como título
                        )
                    }
                }

                // Dibujar la posición del usuario
                userPosition?.let { position ->
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            color = Color.Red,
                            radius = circleRadius,
                            center = Offset(
                                position.x * canvasSize.width / roomWidthMeters,
                                position.y * canvasSize.height / roomHeightMeters
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it.name, modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = it.description, modifier = Modifier.align(Alignment.CenterHorizontally))

        }
    } ?: run {
       // Text(text = "Room not found", modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
    }
}

@Composable
fun ImageWithOffset(imageUrl: String, position: Offset, size: Dp, title: String,navController: NavController) {
    Column(
        modifier = Modifier
            .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
            .size(size)
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set("imageUrl", imageUrl)
                navController.navigate("roomPaintingDetail")
            }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size)
        )
        Text(text = title, modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally))
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
