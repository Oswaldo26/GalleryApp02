package com.ebookfrenzy.galleryapp02.data.model




data class RoomModel(
    val id: String = "",
    val name: String = "",
    val sections: Map<String, Section> = emptyMap()
)