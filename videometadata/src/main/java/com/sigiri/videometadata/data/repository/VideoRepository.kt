package com.sigiri.videometadata.data.repository

import com.sigiri.videometadata.data.model.VideoMetadata
import retrofit2.Response

interface VideoRepository {
    suspend fun getVideoMetadata(videoId: String): Response<VideoMetadata>
}
