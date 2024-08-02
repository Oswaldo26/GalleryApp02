package com.ebookfrenzy.galleryapp02.ui.painting



import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.ebookfrenzy.galleryapp02.utils.Resource

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaintingDetailScreen(paintingId: String, viewModel: PaintingViewModel = hiltViewModel()) {
    val paintingState = viewModel.paintings.collectAsState()
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Scaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val paintings = paintingState.value) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Success -> {
                    paintings.data?.firstOrNull { it.id == paintingId }?.let { painting ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = painting.title, style = MaterialTheme.typography.titleLarge)
                            Text(text = painting.artist, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(painting.imageUrl),
                                contentDescription = painting.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Agrega aquí más detalles de la pintura si es necesario
                            Text(text = painting.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (isPlaying) {
                                        mediaPlayer?.stop()
                                        mediaPlayer?.reset()
                                        mediaPlayer?.release()
                                        mediaPlayer = null
                                        isPlaying = false
                                    } else {
                                        mediaPlayer = MediaPlayer().apply {
                                            setDataSource(painting.audioUrl)
                                            prepare()
                                            start()
                                            setOnCompletionListener {
                                                isPlaying = false
                                            }
                                        }
                                        isPlaying = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                   containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isPlaying) "Detener Audio" else "Iniciar Audio")
                            }
                        }
                    } ?: run {
                        Text(text = "Painting not found")
                    }
                }
                is Resource.Error -> {
                    Text(text = paintings.message ?: "An error occurred")
                }
            }
        }
    }
}
