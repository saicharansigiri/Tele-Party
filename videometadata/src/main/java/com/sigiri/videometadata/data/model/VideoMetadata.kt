package com.sigiri.videometadata.data.model

data class VideoMetadata(
    val title: String?,
    val description: String?,
    val releaseDate: String?,
    val duration: Int?,
    val genres: List<String>?,
    val languages: List<String>?,
    val tags: List<Tag>?,
    val contributors: List<Contributor>?
)

data class Contributor(
    val name: String?,
    val type: String? // "actor", "director"
)

data class Tag(
    val name: String?
)
