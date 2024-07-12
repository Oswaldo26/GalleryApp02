package com.ebookfrenzy.galleryapp02.ui.room

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RoomScreen(viewModel: RoomViewModel = hiltViewModel()) {
    val room by viewModel.room.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        room?.let { room ->
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = room.name, modifier = Modifier.padding(bottom = 16.dp))

                room.sections.forEach { section ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                           // Text(text = section.name, modifier = Modifier.padding(bottom = 8.dp))
                            //Text(text = section.details)
                        }
                    }
                }
            }
        } ?: run {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        }
    }
}