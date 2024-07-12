package com.ebookfrenzy.galleryapp02.data.model



data class GalleryMapsModel(
    val id: String = "",
    val name: String = "",
    val rooms: Map<String, RoomModel> = emptyMap()
)