package com.sigiri.teleparty.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoContent(
    aspectRatio: Float,
    resolutions: List<String>,
    isBuffering: Boolean,
    onResolutionSelected: (String) -> Unit,
    player: ExoPlayer
) {
    Column {
        if (resolutions.isNotEmpty()){
            ResolutionDropdown(
                resolutions = resolutions,
                onSelected = onResolutionSelected
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .padding(top = 8.dp)
        ) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = player
                        useController = true
                    }
                },
                modifier = Modifier.matchParentSize()
            )

            if (isBuffering) {
                Box(
                    modifier = Modifier
                        .matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}