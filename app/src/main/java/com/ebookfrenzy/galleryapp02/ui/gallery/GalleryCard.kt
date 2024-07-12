package com.ebookfrenzy.galleryapp02.ui.gallery

import android.util.Log
import com.ebookfrenzy.galleryapp02.data.model.Gallery



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Composable
fun GalleryCard(gallery: Gallery) {
    Log.d("GalleryCard", "Loading image from URL: ${gallery.imageUrl}")
    Image(
        painter = rememberImagePainter(data = gallery.imageUrl),
        contentDescription = gallery.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}