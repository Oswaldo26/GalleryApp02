package com.ebookfrenzy.galleryapp02.ui.gallery

import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import com.ebookfrenzy.galleryapp02.R

@Composable
fun GalleryMapsScreen(
    viewModel: GalleryMapsViewModel = hiltViewModel(),
    onRoomClick: (String) -> Unit,
    roomOffsets: List<Pair<Dp, Dp>> = listOf(
        0.dp to 0.dp, //Rectangulo1
        0.dp to 300.dp, //Rectangulo2
        100.dp to 0.dp, //Rectangulo3
        250.dp to 0.dp, //Rectangulo4
        250.dp to 100.dp, //Rectangulo5
        250.dp to 300.dp //Rectangulo6
    ),
    roomSizes: List<Pair<Dp, Dp>> = listOf(
        100.dp to 300.dp,
        100.dp to 150.dp,
        150.dp to 100.dp,
        100.dp to 100.dp,
        100.dp to 200.dp,
        100.dp to 150.dp
    ),
    nameOffsets: List<Pair<Dp, Dp>> = listOf(
        -85.dp to -160.dp,
        -85.dp to -150.dp,
        -105.dp to -80.dp,
        -85.dp to -80.dp,
        -85.dp to 0.dp,
        -85.dp to -300.dp
    )
) {
    val gallery by viewModel.gallery.collectAsState()
    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    val context = LocalContext.current
    val density = LocalDensity.current

    // Variables para mover los iconos
    val iconOffsets = remember {
        mutableStateListOf(
            mutableStateOf(Offset(240f, 80f)),
            mutableStateOf(Offset(650f, 80f)),
            mutableStateOf(Offset(450f, 240f)),
            mutableStateOf(Offset(240f, 480f)),
            mutableStateOf(Offset(100f, 795f)),
            mutableStateOf(Offset(780f, 250f)),
            mutableStateOf(Offset(780f, 800f)),
            mutableStateOf(Offset(650f, 500f))
        )
    }

    // Variable para mover la imagen central
    var centerImageOffset by remember { mutableStateOf(Offset(350f, 500f)) }
    // Variable para mover el texto vertical
    var verticalTextOffset by remember { mutableStateOf(Offset(50f, 1300f)) }
    // Variable para mover la puerta doble
    var additionalImageOffset by remember { mutableStateOf(Offset(100f, 1350f)) }
    // Variable para mover el cuadrado adicional
    var additionalSquareOffset by remember { mutableStateOf(Offset(690f, 1250f)) }
    // Variable para mover el ícono adicional
    var additionalIconOffset by remember { mutableStateOf(Offset(780f, 1300f)) }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
        .onGloballyPositioned { coordinates ->
            screenSize = coordinates.size
        }
    ) {
        gallery?.let { gallery ->
            Column {
                Text(text = gallery.name, modifier = Modifier.padding(top = 50.dp, bottom = 16.dp))

                // Dibujar el plano de la galería
                Box(modifier = Modifier.fillMaxSize()) {
                    val rooms = roomSizes.mapIndexed { index, (width, height) ->
                        val offset = roomOffsets.getOrNull(index) ?: 0.dp to 0.dp
                        val size = with(density) { width.toPx() to height.toPx() }
                        val position = with(density) { offset.first.toPx() to offset.second.toPx() }
                        Offset(position.first, position.second) to Size(size.first, size.second)
                    }

                    val roomIds = gallery.rooms.keys.toList()
                    rooms.forEachIndexed { index, (offset, size) ->
                        val roomId = roomIds.getOrNull(index) ?: "roomId${index + 1}"
                        val room = gallery.rooms[roomId]
                        val nameOffset = nameOffsets.getOrNull(index) ?: 0.dp to 0.dp

                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        offset.x.toInt(),
                                        offset.y.toInt()
                                    )
                                }
                                .size(size.width.dp, size.height.dp)
                                .clickable {
                                    onRoomClick(roomId)
                                }
                        ) {
                            Canvas(modifier = Modifier.size(size.width.dp, size.height.dp)) {
                                drawRect(
                                    color = Color.Black,
                                    size = size,
                                    style = Stroke(width = 1f)
                                )
                            }

                            room?.let {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .offset(
                                            x = with(density) { nameOffset.first.toPx().toDp() },
                                            y = with(density) { nameOffset.second.toPx().toDp() }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = it.name,
                                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Dibujar íconos que se pueden mover
                    iconOffsets.forEachIndexed { index, iconOffset ->
                        var iconPosition by remember { iconOffset }

                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        iconPosition.x.toInt(),
                                        iconPosition.y.toInt()
                                    )
                                }
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        iconPosition = iconPosition.copy(
                                            x = iconPosition.x + dragAmount.x,
                                            y = iconPosition.y + dragAmount.y
                                        )
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.door),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }

                    // Añadir una imagen en el centro que se puede mover
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(centerImageOffset.x.toInt(), centerImageOffset.y.toInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    centerImageOffset = centerImageOffset.copy(
                                        x = centerImageOffset.x + dragAmount.x,
                                        y = centerImageOffset.y + dragAmount.y
                                    )
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plaza), // Reemplaza con el id de tu imagen
                            contentDescription = null,
                            modifier = Modifier.size(100.dp) // Ajusta el tamaño según sea necesario
                        )
                    }

                    // Añadir texto vertical que se puede mover
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(verticalTextOffset.x.toInt(), verticalTextOffset.y.toInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    verticalTextOffset = verticalTextOffset.copy(
                                        x = verticalTextOffset.x + dragAmount.x,
                                        y = verticalTextOffset.y + dragAmount.y
                                    )
                                }
                            }
                    ) {
                        Column {
                            "Entrada".forEach { char ->
                                Text(text = char.toString())
                            }
                        }
                    }

                    // Añadir la puerta doble
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(additionalImageOffset.x.toInt(), additionalImageOffset.y.toInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    additionalImageOffset = additionalImageOffset.copy(
                                        x = additionalImageOffset.x + dragAmount.x,
                                        y = additionalImageOffset.y + dragAmount.y
                                    )
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.puertadoble), // Reemplaza con el id de tu imagen
                            contentDescription = null,
                            modifier = Modifier.size(70.dp) // Ajusta el tamaño según sea necesario
                        )
                    }

                    // Cuadrado adicional SSHH
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(additionalSquareOffset.x.toInt(), additionalSquareOffset.y.toInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    additionalSquareOffset = additionalSquareOffset.copy(
                                        x = additionalSquareOffset.x + dragAmount.x,
                                        y = additionalSquareOffset.y + dragAmount.y
                                    )
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.size(60.dp, 60.dp)) { // Ajusta el tamaño del nuevo cuadrado
                            drawRect(
                                color = Color.Black,
                                size = Size(100.dp.toPx(), 50.dp.toPx()), // Ajusta el tamaño del nuevo cuadrado
                                style = Stroke(width = 1f)
                            )
                        }
                    }

                    // Añadir ícono adicional que se puede mover
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(additionalIconOffset.x.toInt(), additionalIconOffset.y.toInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    additionalIconOffset = additionalIconOffset.copy(
                                        x = additionalIconOffset.x + dragAmount.x,
                                        y = additionalIconOffset.y + dragAmount.y
                                    )
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bano), // Reemplaza con el id de tu ícono
                            contentDescription = null,
                            modifier = Modifier.size(30.dp) // Ajusta el tamaño según sea necesario
                        )
                    }
                }
            }
        } ?: run {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        }
    }
}
