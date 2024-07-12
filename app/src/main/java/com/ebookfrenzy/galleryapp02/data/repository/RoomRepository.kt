package com.ebookfrenzy.galleryapp02.data.repository

import com.ebookfrenzy.galleryapp02.data.model.RoomModel
import com.ebookfrenzy.galleryapp02.data.remote.api.RoomService
import javax.inject.Inject

class RoomRepository @Inject constructor(private val roomService: RoomService) {
    suspend fun fetchRoom(roomId: String): RoomModel? {
        return roomService.getRoom(roomId)
    }
}
