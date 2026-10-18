package com.example.subtitle

data class SubtitleCue(
    val startTimeMs: Long,
    val endTimeMs: Long,
    val text: String
)

data class SubtitleTrack(
    val id: String,
    val language: String,
    val title: String,
    val cues: List<SubtitleCue> = emptyList(),
    val isExternal: Boolean = false,
    val sourceUrl: String? = null
)

data class OnlineSubtitleResult(
    val id: String,
    val movieTitle: String,
    val language: String,
    val format: String,
    val downloadCount: Int,
    val rating: Float,
    val content: String? = null,
    val downloadUrl: String? = null
)
