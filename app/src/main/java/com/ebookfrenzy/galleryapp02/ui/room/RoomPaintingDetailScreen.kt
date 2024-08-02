package com.ebookfrenzy.galleryapp02.ui.room

import androidx.compose.ui.Alignment


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RoomPaintingDetailScreen(imageUrl: String) {
    Scaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.padding(16.dp)) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Aquí puedes añadir más detalles sobre la pintura si es necesario
                //Text(text = painting.description, style = androidx.compose.material.MaterialTheme.typography.body1)

            }
        }
    }
}
