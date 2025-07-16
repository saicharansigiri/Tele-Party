package com.sigiri.teleparty.data.repository

import com.sigiri.teleparty.data.api.VideoApiService
import com.sigiri.teleparty.data.mock.MockDataProvider
import com.sigiri.teleparty.data.model.VideoMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository class to handle video data operations
 */
class VideoRepository(private val apiService: VideoApiService? = null) {

    /**
     * Fetches video metadata from the API or mock data
     * @param videoId The ID of the video
     * @return Flow emitting Resource containing VideoMetadata
     */
    fun getVideoMetadata(videoId: String): Flow<Resource<VideoMetadata>> = flow {
        emit(Resource.Loading())
        
        try {
            // Simulate network delay
            delay(1000)
            
            // Use mock data instead of actual API call
            val metadata = MockDataProvider.getVideoById(videoId)
            if (metadata != null) {
                emit(Resource.Success(metadata))
            } else {
                emit(Resource.Error("Video not found with ID: $videoId"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error fetching video metadata: ${e.localizedMessage}"))
        }
    }
    
    /**
     * Get video stream URL for the given video ID and resolution
     * @param videoId The ID of the video
     * @param resolution The requested resolution
     * @return The streaming URL for the video
     */
    fun getVideoStreamUrl(videoId: String, resolution: String): String {
        return MockDataProvider.getStreamUrl(videoId, resolution)
    }
    
    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: VideoRepository? = null
        
        fun getInstance(): VideoRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = VideoRepository(VideoApiService.create())
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Resource class to handle API responses
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
