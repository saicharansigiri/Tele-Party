package com.sigiri.teleparty.ui.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.sigiri.teleparty.VideoPlayerState
import com.sigiri.teleparty.VideoPlayerViewModel
import com.sigiri.teleparty.ui.components.ErrorMessage
import com.sigiri.teleparty.ui.components.FullScreenLoader
import com.sigiri.teleparty.ui.components.VideoContent

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(viewModel: VideoPlayerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val isBuffering by viewModel.isBuffering.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            ) {
                when (val state = uiState) {
                    is VideoPlayerState.Loading -> {
                        FullScreenLoader()
                    }

                    is VideoPlayerState.Error -> {
                        ErrorMessage(message = state.message)
                    }

                    is VideoPlayerState.Success -> {
                        VideoContent(
                            aspectRatio = state.aspectRatio,
                            resolutions = state.availableTracks.map { "${it.height}p" },
                            isBuffering = isBuffering,
                            onResolutionSelected = { resStr ->
                                val height = resStr.removeSuffix("p").toIntOrNull()
                                height?.let { viewModel.selectTrackByHeight(it) }
                            },
                            player = viewModel.player
                        )
                    }
                }
            }
        }
    }
}
