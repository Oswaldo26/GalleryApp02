package com.ebookfrenzy.galleryapp02.ui.room

import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.toSize


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset

@Composable
fun RoomScreen(roomId: String, roomHeight: Dp = 650.dp, viewModel: RoomViewModel = hiltViewModel()) {
    LaunchedEffect(roomId) {
        viewModel.getRoomById("galleryId1", roomId) // Ajusta el ID de la galería según sea necesario
    }

    val room by viewModel.room.collectAsState()

    room?.let {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = it.name, modifier = Modifier.padding(bottom = 16.dp))
            //Text(text = it.description, modifier = Modifier.padding(bottom = 16.dp))

            // Dibujar la habitación como un rectángulo con altura modificable y pequeños círculos
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(roomHeight)
                .padding(16.dp)
            ) {
                var canvasSize by remember { mutableStateOf(Size.Zero) }

                Canvas(modifier = Modifier.matchParentSize().onGloballyPositioned { coordinates ->
                    canvasSize = coordinates.size.toSize()
                }) {
                    // Dibujar el rectángulo
                    drawRect(
                        color = Color.LightGray,
                        size = canvasSize,
                        style = Stroke(width = 2f)
                    )
                }

                // Dibujar las imágenes dentro del rectángulo
                val circleRadius = 30f // Aumentar el radio del círculo
                val circleDiameter = circleRadius * 2
                val circlePadding = 120f // Añadir padding para mantener los círculos dentro del rectángulo
                val circlePositions = listOf(
                    Offset(circlePadding, circlePadding),
                    Offset(canvasSize.width - circleDiameter - circlePadding, circlePadding),
                    //Offset((canvasSize.width - circleDiameter) / 2, (canvasSize.height - circleDiameter) / 2),
                    Offset(circlePadding, canvasSize.height - circleDiameter - circlePadding),
                    Offset(canvasSize.width - circleDiameter - circlePadding, canvasSize.height - circleDiameter - circlePadding)
                )

                circlePositions.forEachIndexed { index, position ->
                    if (index < it.imageUrls.size) {
                        val imageUrl = it.imageUrls[index]
                        Box(modifier = Modifier
                            .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
                            .size(circleDiameter.dp) // Aumentar el tamaño del círculo
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(circleDiameter.dp) // Aumentar el tamaño del círculo
                            )
                        }
                    }
                }
            }
        }
    } ?: run {
        Text(text = "Room not found", modifier = Modifier.padding(16.dp))
    }
}
