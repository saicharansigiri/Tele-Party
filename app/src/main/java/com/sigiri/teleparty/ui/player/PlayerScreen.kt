package com.sigiri.teleparty.ui.player

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.sigiri.teleparty.viewmodel.VideoPlayerState
import com.sigiri.teleparty.viewmodel.VideoPlayerViewModel

/**
 * Screen for the DRM Video Player feature
 */
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(viewModel: VideoPlayerViewModel = hiltViewModel()) {

    val state by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            ) {
                when (state) {
                    is VideoPlayerState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is VideoPlayerState.Error -> {
                        val message = (state as VideoPlayerState.Error).message
                        Text(
                            text = "Error: $message",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    is VideoPlayerState.Success -> {
                        val resolutions = (state as VideoPlayerState.Success).availableTracks

                        Column {
                            ResolutionDropdown(
                                resolutions = resolutions.map { "${it.height}p" },
                                onSelected = { resStr ->
                                    val height = resStr.removeSuffix("p").toIntOrNull()
                                    height?.let { viewModel.selectTrackByHeight(it) }
                                }
                            )

                            AndroidView(
                                factory = { context ->
                                    PlayerView(context).apply {
                                        player = viewModel.player
                                        useController = true

                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f/9f)
                                    .padding(top = 8.dp)
                            )
                        }
                    }
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
    var selected by remember { mutableStateOf(resolutions.firstOrNull() ?: "") }

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
