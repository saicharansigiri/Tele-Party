package com.sigiri.videometadata.data.remote

import com.sigiri.videometadata.data.model.VideoMetadata
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoMetadataApi {

    @GET("v1/web/detail/video")
    suspend fun getVideoMetadata(
        @Query("type") type: String = "movie",
        @Query("id") videoId: String,
    ): Response<VideoMetadata>
}