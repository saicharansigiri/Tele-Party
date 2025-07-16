package com.sigiri.teleparty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigiri.teleparty.data.model.VideoMetadata
import com.sigiri.teleparty.data.model.VideoTrack
import com.sigiri.teleparty.data.repository.Resource
import com.sigiri.teleparty.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the DRM Video Player feature
 */
class VideoPlayerViewModel : ViewModel() {
    
    private val repository = VideoRepository.getInstance()
    
    // Current video ID
    private val _currentVideoId = MutableStateFlow<String?>(null)
    val currentVideoId: StateFlow<String?> = _currentVideoId

    // Current video metadata
    private val _currentVideo = MutableStateFlow<VideoMetadata?>(null)
    val currentVideo: StateFlow<VideoMetadata?> = _currentVideo

    // Stream URL for the current video
    private val _streamUrl = MutableStateFlow<String?>(null)
    val streamUrl: StateFlow<String?> = _streamUrl

    // Available video tracks
    private val _availableTracks = MutableStateFlow<List<VideoTrack>>(emptyList())
    val availableTracks: StateFlow<List<VideoTrack>> = _availableTracks

    // Currently selected track/resolution
    private val _selectedTrack = MutableStateFlow<VideoTrack?>(null)
    val selectedTrack: StateFlow<VideoTrack?> = _selectedTrack
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Initialize with default video on startup
     */
    init {
        loadVideo("video1") // Load Big Buck Bunny by default
    }

    /**
     * Load a video with the given ID
     * @param videoId The ID of the video to load
     */
    fun loadVideo(videoId: String) {
        _isLoading.value = true
        _currentVideoId.value = videoId
        _errorMessage.value = null
        
        viewModelScope.launch {
            repository.getVideoMetadata(videoId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _currentVideo.value = resource.data
                        _availableTracks.value = resource.data?.availableTracks ?: emptyList()
                        
                        // Select default resolution (720p)
                        val defaultTrack = _availableTracks.value.find { it.resolution == "720p" } 
                            ?: _availableTracks.value.firstOrNull()
                        defaultTrack?.let { selectTrack(it) }
                        
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _errorMessage.value = resource.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        // Already handled
                    }
                }
            }
        }
    }

    /**
     * Select a video track/resolution
     * @param track The track to select
     */
    fun selectTrack(track: VideoTrack) {
        _selectedTrack.value = track
        _currentVideoId.value?.let { videoId ->
            _streamUrl.value = repository.getVideoStreamUrl(videoId, track.resolution)
        }
    }

    /**
     * Reload the current video stream with the selected resolution
     */
    fun reloadStream() {
        _currentVideoId.value?.let { videoId ->
            _selectedTrack.value?.let { track ->
                _streamUrl.value = repository.getVideoStreamUrl(videoId, track.resolution)
            }
        }
    }

    /**
     * Clear any error messages
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
