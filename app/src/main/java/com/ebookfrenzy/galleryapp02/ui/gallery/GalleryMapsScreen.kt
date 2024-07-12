package com.ebookfrenzy.galleryapp02.ui.gallery

import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.nativeCanvas


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun GalleryMapsScreen(
    viewModel: GalleryMapsViewModel = hiltViewModel(),
    onRoomClick: (String) -> Unit
) {
    val gallery by viewModel.gallery.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        gallery?.let { gallery ->
            Column {
                Text(text = gallery.name, modifier = Modifier.padding(bottom = 16.dp))

                // Dibujar el plano de la galería en forma de L
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val roomWidth = 500f  // Aumentar el ancho de las habitaciones
                    val roomHeight = 300f  // Aumentar la altura de las habitaciones
                    val padding = 50f  // Ajustar el padding

                    val rooms = listOf(
                        Offset(padding, padding) to Size(roomWidth, roomHeight), // Room 1
                        Offset(padding + roomWidth + padding, padding) to Size(roomWidth, roomHeight), // Room 2
                        Offset(padding, padding + roomHeight + padding) to Size(roomWidth, roomHeight), // Room 3
                        Offset(padding, padding + roomHeight * 2 + padding * 2) to Size(roomWidth, roomHeight) // Room 4
                    )

                    val roomIds = gallery.rooms.keys.toList()
                    rooms.forEachIndexed { index, (offset, size) ->
                        val roomId = roomIds.getOrNull(index) ?: "roomId${index + 1}"
                        val room = gallery.rooms[roomId]

                        drawRect(
                            color = Color.LightGray,
                            topLeft = offset,
                            size = size,
                            style = Stroke(width = 2f)
                        )

                        room?.let {
                            drawIntoCanvas { canvas ->
                                canvas.nativeCanvas.drawText(
                                    it.name,
                                    offset.x + 20,
                                    offset.y + 40,
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
        } ?: run {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        }
    }
}
