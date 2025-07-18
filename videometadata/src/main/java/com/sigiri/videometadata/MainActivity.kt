package com.sigiri.videometadata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sigiri.videometadata.ui.theme.TelePartyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TelePartyTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: VideoViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState

    val knownVideos = remember {
        mapOf(
            "Jab We Met" to "acb8b71b41fa86ace0e3e10d75e78e22",
            "Housefull 2" to "f11cec31eff8ae5a0984017b3c252e02",
            "Yamla Pagla Deewana" to "a3b53d8994a29b8cfae0e061427be565",
            "The Monkey King 2" to "500f8c5e98bf9d06e109d8b880a76342",
            "Anaconda 3" to "6f913706db4658ceb8443d9ef805f529"
        )
    }

    var selectedVideoId by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            VideoInputSection(
                videoIdMap = knownVideos,
                selectedVideoId = selectedVideoId,
                onVideoIdChanged = { selectedVideoId = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.fetchMetadata(selectedVideoId) },
                enabled = selectedVideoId.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fetch Metadata")
            }

            Spacer(modifier = Modifier.height(24.dp))

            VideoMetadataResult(uiState)
        }
    }
}

@Composable
fun VideoMetadataResult(uiState: UiState) {
    when (uiState) {
        is UiState.Idle -> {
            Text("Enter a video ID and press Fetch")
        }

        is UiState.Loading -> {
            CircularProgressIndicator()
        }

        is UiState.Success -> {
            val data = uiState.data
            Column {
                Text("Title: ${data.title}")
                Text("Description: ${data.description}")
                Text("Genres: ${data.genres?.joinToString()}")
                Text("Languages: ${data.languages?.joinToString()}")
                Text("Release Date: ${data.releaseDate}")
                Text("Duration: ${data.duration}")
            }
        }

        is UiState.Error -> {
            Text("Error: ${uiState.message}", color = Color.Red)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoInputSection(
    videoIdMap: Map<String, String>,
    selectedVideoId: String,
    onVideoIdChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTitle by remember { mutableStateOf("") }

    Column {
        Text("Enter Video ID manually or select from known titles")

        OutlinedTextField(
            value = selectedVideoId,
            onValueChange = {
                onVideoIdChanged(it)
                selectedTitle = ""
            },
            label = { Text("Enter Video ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedTitle,
                onValueChange = {},
                readOnly = true,
                label = { Text("Or select movie") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                videoIdMap.keys.forEach { title ->
                    DropdownMenuItem(
                        text = { Text(title) },
                        onClick = {
                            selectedTitle = title
                            expanded = false
                            val resolvedId = videoIdMap[title] ?: ""
                            onVideoIdChanged(resolvedId)
                        }
                    )
                }
            }
        }
    }
}
