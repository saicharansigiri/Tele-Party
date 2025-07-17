package com.sigiri.teleparty.ui.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.sigiri.teleparty.VideoPlayerState
import com.sigiri.teleparty.VideoPlayerViewModel

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


@Composable
private fun VideoContent(
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


@Composable
fun ResolutionDropdown(
    resolutions: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(resolutions.firstOrNull().orEmpty()) }

    Box(Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = "Resolution: $selected",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(12.dp)
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            resolutions.forEach { res ->
                DropdownMenuItem(
                    text = { Text(res) },
                    onClick = {
                        selected = res
                        expanded = false
                        onSelected(res)
                    }
                )
            }
        }
    }
}

@Composable
private fun FullScreenLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Text(
        text = "Error: $message",
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}
