package com.sigiri.teleparty.ui.components

import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.ui.PlayerView

/**
 * A reusable video player component using ExoPlayer
 *
 * @param url The URL of the video to play
 * @param modifier Modifier to apply to the player
 * @param autoPlay Whether to start playback automatically
 * @param useController Whether to show player controls
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    useController: Boolean = true,
) {
    val context = LocalContext.current
    
    // Create an ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build().apply {
                // Set player properties
                playWhenReady = autoPlay
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }

    // Create media source
    val mediaSource = remember(url) {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD) // DASH format
            .build()
            
        DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)
    }
    
    DisposableEffect(url) {
        // Set the media source when URL changes
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        
        onDispose {
            // Release the player when leaving the composition
            exoPlayer.release()
        }
    }

    // Render the player view
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    )
}
