package com.sigiri.teleparty

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
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

@UnstableApi
@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoPlayerState>(VideoPlayerState.Loading)
    val uiState: StateFlow<VideoPlayerState> = _uiState.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val trackSelector = DefaultTrackSelector(context).apply {
        setParameters(buildUponParameters().setForceHighestSupportedBitrate(true))
    }

    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(DefaultLoadControl())
        .build().apply {
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_OFF

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    _isBuffering.value = (state == Player.STATE_BUFFERING)
                }
            })
        }

    init {
        loadVideo(
            manifestUrl = "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd",
            licenseUrl = "https://cwip-shaka-proxy.appspot.com/no_auth"
        )
    }

    fun loadVideo(manifestUrl: String, licenseUrl: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Preparing media item for: $manifestUrl")

                val drmConfig = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(licenseUrl)
                    .setMultiSession(true)
                    .build()

                val mediaItem = MediaItem.Builder()
                    .setUri(manifestUrl)
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setDrmConfiguration(drmConfig)
                    .build()

                val mediaSource = DashMediaSource.Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(mediaItem)

                player.setMediaSource(mediaSource)
                player.prepare()

                // Delay to ensure track info is ready
                delay(2000)

                val videoTracks = extractVideoTracks()

                if (videoTracks.isEmpty()) {
                    Log.w(TAG, "No video tracks extracted. TrackInfo is null or empty.")
                } else {
                    Log.d(TAG, "Extracted ${videoTracks.size} video tracks")
                }

                val aspectRatio = videoTracks.firstOrNull()?.let {
                    it.width.toFloat() / it.height
                } ?: DEFAULT_ASPECT_RATIO

                _uiState.value = VideoPlayerState.Success(
                    availableTracks = videoTracks,
                    aspectRatio = aspectRatio
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error loading video: ${e.message}", e)
                _uiState.value = VideoPlayerState.Error("Playback failed: ${e.message}")
            }
        }
    }

    fun selectTrackByHeight(targetHeight: Int) {
        Log.d(TAG, "User selected resolution height: $targetHeight")
        val params = trackSelector.buildUponParameters()
            .setMaxVideoSize(Int.MAX_VALUE, targetHeight)
            .build()
        trackSelector.parameters = params
    }

    private fun extractVideoTracks(): List<VideoTrack> {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        if (mappedTrackInfo == null) {
            Log.w(TAG, "MappedTrackInfo is null. Cannot extract video tracks.")
            return emptyList()
        }

        val tracks = mutableListOf<VideoTrack>()

        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            if (mappedTrackInfo.getRendererType(rendererIndex) != C.TRACK_TYPE_VIDEO) continue

            val trackGroups: TrackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
            for (groupIndex in 0 until trackGroups.length) {
                val group = trackGroups[groupIndex]
                for (trackIndex in 0 until group.length) {
                    val format = group.getFormat(trackIndex)
                    if (format.height <= 0 || format.width <= 0) continue

                    tracks.add(
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

        return tracks.distinctBy { it.height }.sortedByDescending { it.height }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Releasing player")
        player.release()
    }

    companion object {
        private const val TAG = "VideoPlayerVM"
        private const val DEFAULT_ASPECT_RATIO = 16f / 9f
    }
}

// Models

data class VideoTrack(
    val id: String,
    val height: Int,
    val width: Int,
    val bitrate: Int,
)

sealed class VideoPlayerState {
    object Loading : VideoPlayerState()
    data class Success(
        val availableTracks: List<VideoTrack>,
        val aspectRatio: Float,
    ) : VideoPlayerState()

    data class Error(val message: String) : VideoPlayerState()
}
