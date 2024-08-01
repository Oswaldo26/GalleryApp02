package com.ebookfrenzy.galleryapp02.ui.painting


import android.annotation.SuppressLint
import com.ebookfrenzy.galleryapp02.data.model.Painting
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.ebookfrenzy.galleryapp02.utils.Resource
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaintingScreen(navController: NavHostController, viewModel: PaintingViewModel = hiltViewModel()) {
    val paintingState = viewModel.paintings.collectAsState()
    val excludedIds = listOf("paintingId1", "paintingId2", "paintingId3", "paintingId4")

    Scaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val paintings = paintingState.value) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Success -> {
                    paintings.data?.let { paintingList ->
                        val filteredPaintings = paintingList.filterNot { it.id in excludedIds }
                        Column(modifier = Modifier.padding(top = 16.dp)) { // Añadir padding en la parte superior
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredPaintings) { painting ->
                                    PaintingItem(painting, navController)
                                }
                            }
                        }
                    } ?: run {
                        Text(text = "No paintings available")
                    }
                }
                is Resource.Error -> {
                    Text(text = paintings.message ?: "An error occurred")
                }
            }
        }
    }
}

@Composable
fun PaintingItem(painting: Painting, navController: NavHostController) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable { navController.navigate("paintingDetail/${painting.id}") }) {
        Text(text = painting.title, style = androidx.compose.material.MaterialTheme.typography.h6)
        Text(text = painting.artist, style = androidx.compose.material.MaterialTheme.typography.body2)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberAsyncImagePainter(painting.imageUrl),
            contentDescription = painting.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}
