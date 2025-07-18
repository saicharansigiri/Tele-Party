package com.sigiri.videometadata.data.repository

import com.sigiri.videometadata.data.model.VideoMetadata
import com.sigiri.videometadata.data.remote.VideoMetadataApi
import retrofit2.Response
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val api: VideoMetadataApi
) : VideoRepository {
    override suspend fun getVideoMetadata(videoId: String): Response<VideoMetadata> {
        return api.getVideoMetadata(videoId = videoId)
    }
}
