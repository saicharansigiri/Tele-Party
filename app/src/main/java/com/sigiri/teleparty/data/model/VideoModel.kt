package com.sigiri.teleparty.data.model

/**
 * Model class representing video track options
 */
data class MetaVideoTrack(
    val id: String,
    val resolution: String,
    val bitrateKbps: Int,
    val mimeType: String = "video/mp4"
)

/**
 * Model class for video metadata
 */
data class VideoMetadata(
    val id: String,
    val title: String,
    val description: String,
    val releaseDate: String,
    val duration: Long,
    val genre: String?,
    val thumbnailUrl: String?,
    val availableTracks: List<MetaVideoTrack>
)

/**
 * Model class representing response from metadata API
 */
data class VideoMetadataResponse(
    val success: Boolean,
    val data: VideoMetadata?,
    val error: String?
)

/**
 * Class to represent available video resolutions
 */
enum class VideoResolution(val label: String, val height: Int) {
    RES_240P("240p", 240),
    RES_480P("480p", 480),
    RES_720P("720p", 720),
    RES_1080P("1080p", 1080);
    
    companion object {
        fun fromHeight(height: Int): VideoResolution {
            return when {
                height <= 240 -> RES_240P
                height <= 480 -> RES_480P
                height <= 720 -> RES_720P
                else -> RES_1080P
            }
        }
    }
}
