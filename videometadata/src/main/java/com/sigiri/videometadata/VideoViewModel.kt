package com.sigiri.videometadata

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigiri.videometadata.data.model.VideoMetadata
import com.sigiri.videometadata.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {

    companion object {
        private const val TAG = "VideoViewModel"
    }

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun fetchMetadata(id: String) {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                val response = repo.getVideoMetadata(id) // retrofit.Response<VideoMetadata>
                uiState = if (response.isSuccessful && response.body() != null) {
                    UiState.Success(response.body()!!)
                } else {
                    Log.e(TAG, "Failed: ${response.code()} ${response.message()}")
                    UiState.Error("Failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                uiState = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}



sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val data: VideoMetadata) : UiState()
    data class Error(val message: String) : UiState()
}
