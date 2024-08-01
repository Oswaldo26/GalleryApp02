package com.ebookfrenzy.galleryapp02.ui.painting

import android.annotation.SuppressLint
import androidx.compose.ui.Alignment


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaintingDetailScreen(paintingId: String, viewModel: PaintingViewModel = hiltViewModel()) {
    val paintingState = viewModel.paintings.collectAsState()

    Scaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val paintings = paintingState.value) {
                is com.ebookfrenzy.galleryapp02.utils.Resource.Loading -> CircularProgressIndicator()
                is com.ebookfrenzy.galleryapp02.utils.Resource.Success -> {
                    paintings.data?.firstOrNull { it.id == paintingId }?.let { painting ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = painting.title, style = androidx.compose.material.MaterialTheme.typography.h4)
                            Text(text = painting.artist, style = androidx.compose.material.MaterialTheme.typography.h6)
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(painting.imageUrl),
                                contentDescription = painting.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                            )
                            // Agrega aquí más detalles de la pintura si es necesario
                           // Text(text = "Aquí puedes añadir más detalles sobre la pintura.", style = androidx.compose.material.MaterialTheme.typography.body1)
                        }
                    } ?: run {
                        Text(text = "Painting not found")
                    }
                }
                is com.ebookfrenzy.galleryapp02.utils.Resource.Error -> {
                    Text(text = paintings.message ?: "An error occurred")
                }
            }
        }
    }
}
