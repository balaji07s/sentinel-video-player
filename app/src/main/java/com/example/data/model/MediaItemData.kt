package com.example.data.model

import android.net.Uri
import java.util.Locale

/**
 * Represents a video file item, either from local device storage or bundled sample library.
 */
data class MediaItemData(
    val id: String,
    val uri: Uri,
    val title: String,
    val folderName: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val resolution: String = "1080p",
    val mimeType: String = "video/mp4",
    val dateModified: Long = System.currentTimeMillis(),
    val lastPlayedPositionMs: Long = 0L,
    val isSample: Boolean = false,
    val format: String = "MP4"
) {
    val durationFormatted: String
        get() = formatDuration(durationMs)

    val sizeFormatted: String
        get() = formatFileSize(sizeBytes)

    val progressFraction: Float
        get() = if (durationMs > 0) (lastPlayedPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    companion object {
        fun formatDuration(ms: Long): String {
            if (ms <= 0) return "00:00"
            val totalSeconds = ms / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }
        }

        fun formatFileSize(bytes: Long): String {
            if (bytes <= 0) return "Unknown"
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format(Locale.getDefault(), "%.1f GB", gb)
                mb >= 1.0 -> String.format(Locale.getDefault(), "%.1f MB", mb)
                else -> String.format(Locale.getDefault(), "%.1f KB", kb)
            }
        }
    }
}
