package com.sigiri.teleparty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigiri.teleparty.data.model.VideoMetadata
import com.sigiri.teleparty.data.repository.Resource
import com.sigiri.teleparty.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for the Video Metadata Fetcher feature
 */
class MetadataViewModel : ViewModel() {
    
    private val repository = VideoRepository.getInstance()
    
    // Video ID input by the user
    private val _videoId = MutableStateFlow("")
    val videoId: StateFlow<String> = _videoId
    
    // Available sample video IDs for demo
    private val _sampleVideoIds = MutableStateFlow(listOf("video1", "video2", "video3"))
    val sampleVideoIds: StateFlow<List<String>> = _sampleVideoIds
    
    // Video metadata result
    private val _metadata = MutableStateFlow<VideoMetadata?>(null)
    val metadata: StateFlow<VideoMetadata?> = _metadata
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    /**
     * Update the video ID entered by the user
     */
    fun onVideoIdChange(id: String) {
        _videoId.value = id
    }
    
    /**
     * Fetch metadata for the current video ID
     */
    fun fetchMetadata() {
        val currentId = _videoId.value.trim()
        if (currentId.isEmpty()) {
            _error.value = "Please enter a valid video ID"
            return
        }
        
        _isLoading.value = true
        _error.value = null
        _metadata.value = null
        
        viewModelScope.launch {
            repository.getVideoMetadata(currentId)
                .catch { exception ->
                    _error.value = exception.localizedMessage
                    _isLoading.value = false
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _metadata.value = resource.data
                            _isLoading.value = false
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                            _isLoading.value = false
                        }
                        is Resource.Loading -> {
                            // Already handled before collecting
                        }
                    }
                }
        }
    }
    
    /**
     * Load a sample video by ID from the available samples
     */
    fun loadSampleVideo(videoId: String) {
        onVideoIdChange(videoId)
        fetchMetadata()
    }
    
    /**
     * Clear any error messages
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Clear the current metadata
     */
    fun clearMetadata() {
        _metadata.value = null
    }
}
