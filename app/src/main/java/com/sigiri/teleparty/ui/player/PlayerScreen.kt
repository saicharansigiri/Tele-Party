package com.sigiri.teleparty.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sigiri.teleparty.ui.components.VideoPlayer
import com.sigiri.teleparty.viewmodel.VideoPlayerViewModel

/**
 * Screen for the DRM Video Player feature
 */
@Composable
fun PlayerScreen(viewModel: VideoPlayerViewModel = viewModel()) {
    val streamUrl by viewModel.streamUrl.collectAsState()
    val selectedTrack by viewModel.selectedTrack.collectAsState()
    val availableTracks by viewModel.availableTracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val videoId by viewModel.currentVideoId.collectAsState()
    val currentVideo by viewModel.currentVideo.collectAsState()
    
    // Local state for resolution dropdown menu
    var showResolutionDropdown by remember { mutableStateOf(false) }
    
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Video Player
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            ) {
                Column {
                    // Title bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(com.sigiri.teleparty.R.drawable.ic_video_lib),
                            contentDescription = "Video",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentVideo?.title ?: "DRM Video Player",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            streamUrl?.let { url ->
                                VideoPlayer(
                                    url = url,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: run {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No video loaded")
                                }
                            }
                        }
                    }
                }
            }
            
            // Error message
            errorMessage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Controls section
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Video Controls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Resolution selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current: ${selectedTrack?.resolution ?: "Not selected"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Box {
                            OutlinedButton(onClick = { showResolutionDropdown = true }) {
                                Text("Change Resolution")
                            }
                            
                            DropdownMenu(
                                expanded = showResolutionDropdown,
                                onDismissRequest = { showResolutionDropdown = false }
                            ) {
                                availableTracks.forEach { track ->
                                    DropdownMenuItem(
                                        text = { Text(text = "${track.resolution} (${track.bitrateKbps} kbps)") },
                                        onClick = {
                                            viewModel.selectTrack(track)
                                            showResolutionDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Reload button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FilledTonalButton(onClick = { viewModel.reloadStream() }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Reload Stream")
                        }
                    }
                }
            }
            
            // Video Information
            currentVideo?.let { video ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Video Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Title: ${video.title}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "Release Date: ${video.releaseDate}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        val duration = formatDuration(video.duration)
                        Text(
                            text = "Duration: $duration",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        video.genre?.let { genre ->
                            Text(
                                text = "Genre: $genre",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Text(
                            text = "Description: ${video.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // Available tracks
                        Text(
                            text = "Available Resolutions:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            video.availableTracks.forEach { track ->
                                FilledTonalButton(
                                    onClick = { viewModel.selectTrack(track) },
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(track.resolution)
                                }
                            }
                        }
                    }
                }
            }
            
            // Sample videos section
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sample Videos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { viewModel.loadVideo("video1") }
                        ) {
                            Text("Big Buck Bunny")
                        }
                        
                        Button(
                            onClick = { viewModel.loadVideo("video2") }
                        ) {
                            Text("Sintel")
                        }
                        
                        Button(
                            onClick = { viewModel.loadVideo("video3") }
                        ) {
                            Text("Tears of Steel")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Format duration in milliseconds to a human-readable string
 */
private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / (1000 * 60)) % 60
    val hours = durationMs / (1000 * 60 * 60)
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
