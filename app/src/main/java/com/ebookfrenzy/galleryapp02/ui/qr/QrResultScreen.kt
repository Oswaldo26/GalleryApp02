package com.ebookfrenzy.galleryapp02.ui.qr


import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun QrResultScreen(paintingId: String, viewModel: QrResultViewModel = hiltViewModel()) {
    val paintingState by viewModel.painting.collectAsState()
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var audioUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(paintingId) {
        viewModel.getPaintingById(paintingId)
    }

    fun playAudio(url: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener {
                it.start()
            }
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            Toast.makeText(context, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val painting = paintingState) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Success -> {
                    painting.data?.let { painting ->
                        audioUrl = painting.audioUrl // Asignar la URL del audio
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = painting.title, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
                            Text(text = painting.artist, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(painting.imageUrl),
                                contentDescription = painting.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    audioUrl?.let { url ->
                                        playAudio(url)
                                    } ?: run {
                                        Toast.makeText(context, "Audio URL not found", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(text = "Reproducir Audio")
                            }
                        }
                    } ?: run {
                        Text(text = "Painting not found")
                    }
                }
                is Resource.Error -> {
                    Text(text = painting.message ?: "An error occurred")
                }
            }
        }
    }
}
