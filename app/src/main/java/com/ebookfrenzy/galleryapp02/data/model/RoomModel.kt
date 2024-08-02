package com.ebookfrenzy.galleryapp02.data.model




data class RoomModel(
    val id: String = "",
    val name: String = "",
    val imageUrls: List<String> = emptyList(),
    val sections: Map<String, Section> = emptyMap(),
    val description: String = "",
    val paintings: List<Painting> = emptyList(),
)