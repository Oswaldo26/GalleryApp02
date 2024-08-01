package com.ebookfrenzy.galleryapp02.ui.gallery

import android.annotation.SuppressLint
import androidx.compose.ui.draw.alpha

import androidx.compose.ui.unit.Dp
import com.ebookfrenzy.galleryapp02.utils.Resource
import com.google.accompanist.pager.HorizontalPagerIndicator



import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel(), imageHeight: Dp = 300.dp, imageAlpha: Float = 1f) {
    val galleryState = viewModel.galleries.collectAsState()



    Scaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val galleries = galleryState.value) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Success -> {
                    galleries.data?.let { galleryList ->
                        val pagerState = rememberPagerState()
                        Column(modifier = Modifier.fillMaxSize()) {
                            HorizontalPager(
                                count = galleryList.size,
                                state = pagerState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize() // Asegura que el HorizontalPager ocupe todo el tamaño disponible
                                    .padding( bottom = 15.dp) // Añade padding superior e inferior
                            ) { page ->
                                val gallery = galleryList[page]
                                Log.d("GalleryScreen", "Loading image from URL: ${gallery.imageUrl}")
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth() // Asegura que la imagen ocupe todo el ancho disponible


                                ) {
                                    AsyncImage(
                                        model = gallery.imageUrl,
                                        contentDescription = gallery.name,

                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize() // Asegura que la imagen ocupe todo el tamaño disponible
                                            .alpha(imageAlpha) // Ajusta la transparencia de la imagen
                                    )

                                }
                            }
                            HorizontalPagerIndicator(
                                pagerState = pagerState,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(16.dp)
                            )
                        }
                    } ?: run {
                        Text(text = "No galleries available")
                    }
                }
                is Resource.Error -> {
                    Text(text = galleries.message ?: "An error occurred")
                }
            }
        }
    }
}