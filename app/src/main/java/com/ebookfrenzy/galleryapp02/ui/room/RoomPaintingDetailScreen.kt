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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.padding(16.dp)) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // This will make the image take most of the screen
                )
                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}
