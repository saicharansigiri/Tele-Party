package com.sigiri.teleparty.data.mock

import com.sigiri.teleparty.data.model.MetaVideoTrack
import com.sigiri.teleparty.data.model.VideoMetadata

/**
 * Provider of mock data for testing and demonstration purposes
 */
object MockDataProvider {

    /**
     * Get a list of sample videos with metadata
     */
    fun getSampleVideos(): List<VideoMetadata> {
        return listOf(
            VideoMetadata(
                id = "video1",
                title = "Big Buck Bunny",
                description = "Big Buck Bunny is a short animated film by the Blender Institute, part of the Blender Foundation. It features a large rabbit fighting against three bullying rodents.",
                releaseDate = "2008-04-10",
                duration = 596000, // 9:56 in milliseconds
                genre = "Animation",
                thumbnailUrl = "https://peach.blender.org/wp-content/uploads/bbb-splash.png",
                availableTracks = listOf(
                    MetaVideoTrack("240p", "240p", 500),
                    MetaVideoTrack("480p", "480p", 1500),
                    MetaVideoTrack("720p", "720p", 2500),
                    MetaVideoTrack("1080p", "1080p", 5000)
                )
            ),
            VideoMetadata(
                id = "video2",
                title = "Sintel",
                description = "Sintel is a fantasy computer animated short film. It was produced by the Blender Foundation as part of the Durian Open Movie Project.",
                releaseDate = "2010-09-27",
                duration = 888000, // 14:48 in milliseconds
                genre = "Fantasy",
                thumbnailUrl = "https://durian.blender.org/wp-content/uploads/2010/05/sintel_trailer_1080p.jpg",
                availableTracks = listOf(
                    MetaVideoTrack("240p", "240p", 500),
                    MetaVideoTrack("480p", "480p", 1500),
                    MetaVideoTrack("720p", "720p", 2500),
                    MetaVideoTrack("1080p", "1080p", 5000)
                )
            ),
            VideoMetadata(
                id = "video3",
                title = "Tears of Steel",
                description = "Tears of Steel was produced by Blender Foundation and directed by Ian Hubert. It's the fourth film from the Blender Open Movie Project.",
                releaseDate = "2012-09-26",
                duration = 734000, // 12:14 in milliseconds
                genre = "Sci-Fi",
                thumbnailUrl = "https://mango.blender.org/wp-content/uploads/2012/09/tears_of_steel_poster.jpg",
                availableTracks = listOf(
                    MetaVideoTrack("240p", "240p", 500),
                    MetaVideoTrack("480p", "480p", 1500),
                    MetaVideoTrack("720p", "720p", 2500),
                    MetaVideoTrack("1080p", "1080p", 5000)
                )
            )
        )
    }

    /**
     * Get video metadata by ID
     */
    fun getVideoById(videoId: String): VideoMetadata? {
        return getSampleVideos().find { it.id == videoId }
    }

    /**
     * Get streaming URL for a video by ID and resolution
     */
    fun getStreamUrl(videoId: String, resolution: String): String {
        // These are publicly available DASH test streams
        return when (videoId) {
            "video1" -> "https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd" // Big Buck Bunny
            "video2" -> "https://dash.akamaized.net/dash264/TestCases/2c/qualcomm/1/MultiResMPEG2.mpd" // Sample MPEG-2
            "video3" -> "https://dash.akamaized.net/dash264/TestCases/1a/netflix/exMPD_BIP_TC1.mpd" // Sample DRM
            else -> "https://storage.googleapis.com/exoplayer-test-media-1/60fps/bbb-clear/manifest.mpd" // Default
        }
    }
}
