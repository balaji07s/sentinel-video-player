package com.example.subtitle

import java.util.regex.Pattern

object SubtitleParser {
    private val TIMESTAMP_PATTERN_SRT = Pattern.compile("(\\d{1,2}):(\\d{2}):(\\d{2})[,.](\\d{3})")
    private val HTML_TAG_PATTERN = Pattern.compile("<[^>]*>")

    fun parseSrtOrVtt(content: String): List<SubtitleCue> {
        val cues = mutableListOf<SubtitleCue>()
        val lines = content.lines()
        var i = 0

        while (i < lines.size) {
            val line = lines[i].trim()

            // Look for timestamp line (e.g., 00:01:20,000 --> 00:01:23,500)
            if (line.contains("-->")) {
                val parts = line.split("-->")
                if (parts.size == 2) {
                    val startMs = parseTimestamp(parts[0].trim())
                    val endMs = parseTimestamp(parts[1].trim().split(" ")[0])

                    val textBuilder = StringBuilder()
                    i++
                    while (i < lines.size && lines[i].isNotBlank()) {
                        val cleanText = HTML_TAG_PATTERN.matcher(lines[i].trim()).replaceAll("")
                        if (textBuilder.isNotEmpty()) textBuilder.append("\n")
                        textBuilder.append(cleanText)
                        i++
                    }

                    if (startMs != null && endMs != null && textBuilder.isNotEmpty()) {
                        cues.add(SubtitleCue(startMs, endMs, textBuilder.toString()))
                    }
                }
            }
            i++
        }

        return cues.sortedBy { it.startTimeMs }
    }

    private fun parseTimestamp(timeStr: String): Long? {
        val matcher = TIMESTAMP_PATTERN_SRT.matcher(timeStr)
        if (matcher.find()) {
            val hours = matcher.group(1)?.toLongOrNull() ?: 0L
            val minutes = matcher.group(2)?.toLongOrNull() ?: 0L
            val seconds = matcher.group(3)?.toLongOrNull() ?: 0L
            val millis = matcher.group(4)?.toLongOrNull() ?: 0L
            return (hours * 3600_000L) + (minutes * 60_000L) + (seconds * 1000L) + millis
        }
        return null
    }
}
