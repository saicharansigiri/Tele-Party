package com.sigiri.teleparty.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the DRM Video Player feature
 */
@UnstableApi
@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState: MutableStateFlow<VideoPlayerState> =
        MutableStateFlow(VideoPlayerState.Loading)
    val uiState: StateFlow<VideoPlayerState> = _uiState.asStateFlow()

    private val trackSelector = DefaultTrackSelector(context).apply {
        setParameters(buildUponParameters().setForceHighestSupportedBitrate(true))
    }

    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(DefaultLoadControl())
        .build().apply {
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
        }

    init {
        loadVideo(
            manifestUrl = "https://storage.googleapis.com/shaka-demo-assets/sintel-widevine/dash.mpd",
            licenseUrl = "https://cwip-shaka-proxy.appspot.com/no_auth"
        )
    }

    fun loadVideo(manifestUrl: String, licenseUrl: String) {
        viewModelScope.launch {
            try {
                val drmConfiguration = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(licenseUrl)
                    .setMultiSession(true)
                    .build()

                val mediaItem = MediaItem.Builder()
                    .setUri(manifestUrl)
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setDrmConfiguration(drmConfiguration)
                    .build()

                val dataSourceFactory = DefaultHttpDataSource.Factory()
                val mediaSource = DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)

                player.setMediaSource(mediaSource)
                player.prepare()

                delay(1500) // Let player buffer and parse tracks

                val tracks = extractVideoTracks()
                _uiState.value = VideoPlayerState.Success(tracks)

            } catch (e: Exception) {
                _uiState.value = VideoPlayerState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun extractVideoTracks(): List<VideoTrack> {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return emptyList()

        val videoTracks = mutableListOf<VideoTrack>()

        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            if (mappedTrackInfo.getRendererType(rendererIndex) != C.TRACK_TYPE_VIDEO) continue

            val trackGroups: TrackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
            for (groupIndex in 0 until trackGroups.length) {
                val group = trackGroups.get(groupIndex)
                for (trackIndex in 0 until group.length) {
                    val format = group.getFormat(trackIndex)

                    videoTracks.add(
                        VideoTrack(
                            id = "$rendererIndex-$groupIndex-$trackIndex",
                            height = format.height,
                            width = format.width,
                            bitrate = format.bitrate
                        )
                    )
                }
            }
        }

        return videoTracks.distinctBy { it.height }.sortedByDescending { it.height }
    }

    fun selectTrackByHeight(targetHeight: Int) {
        val parameters = trackSelector.buildUponParameters()
            .setMaxVideoSize(Int.MAX_VALUE, targetHeight)
            .build()
        trackSelector.parameters = parameters
    }

}

data class VideoTrack(
    val id: String,
    val height: Int,
    val width: Int,
    val bitrate: Int,
)


sealed class VideoPlayerState {
    object Loading : VideoPlayerState()
    data class Success(val availableTracks: List<VideoTrack>) : VideoPlayerState()
    data class Error(val message: String) : VideoPlayerState()
}