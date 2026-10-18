package com.example.audio

import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.NoiseSuppressor
import android.util.Log
import com.example.data.model.AudioEnhancerSettings

class AudioEnhancerEngine {
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var equalizer: Equalizer? = null
    private var noiseSuppressor: NoiseSuppressor? = null
    private var currentSessionId: Int = 0

    val presets = listOf(
        "Normal" to listOf(0, 0, 0, 0, 0),
        "Vocal Clarity" to listOf(-300, 100, 600, 500, -200),
        "Bass Boost" to listOf(700, 400, 0, -100, -200),
        "Rock" to listOf(400, 200, -100, 200, 500),
        "Pop" to listOf(-100, 200, 500, 200, -100),
        "Jazz" to listOf(0, 200, 0, 300, 400),
        "Classical" to listOf(400, 300, -100, 200, 300),
        "Movie Theater" to listOf(500, 100, 300, 400, 300)
    )

    fun attachToAudioSession(audioSessionId: Int, initialSettings: AudioEnhancerSettings) {
        if (audioSessionId <= 0) return
        if (currentSessionId == audioSessionId && loudnessEnhancer != null) {
            applySettings(initialSettings)
            return
        }

        release()
        currentSessionId = audioSessionId

        try {
            loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            Log.w("AudioEnhancer", "LoudnessEnhancer not supported on this device/session", e)
        }

        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            Log.w("AudioEnhancer", "Equalizer not supported on this device/session", e)
        }

        if (NoiseSuppressor.isAvailable()) {
            try {
                noiseSuppressor = NoiseSuppressor.create(audioSessionId)
            } catch (e: Exception) {
                Log.w("AudioEnhancer", "NoiseSuppressor not initialized", e)
            }
        }

        applySettings(initialSettings)
    }

    fun applySettings(settings: AudioEnhancerSettings) {
        // 1. VLC-style Preamp Gain (0% - 200%)
        // 100% = normal (0 mB), 200% = +2000 mB (+20 dB boost)
        try {
            loudnessEnhancer?.let { enhancer ->
                if (settings.gainPercent > 100) {
                    val extraPercent = settings.gainPercent - 100
                    val targetGainMb = (extraPercent * 20) // e.g. 100 * 20 = 2000 mB (+20 dB)
                    enhancer.setTargetGain(targetGainMb)
                    enhancer.enabled = true
                } else if (settings.gainPercent in 0..100) {
                    // Normal or attenuated
                    enhancer.setTargetGain(0)
                    enhancer.enabled = false
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEnhancer", "Failed to update LoudnessEnhancer gain", e)
        }

        // 2. Hardware Noise Suppressor
        try {
            noiseSuppressor?.let {
                it.enabled = settings.noiseReductionEnabled
            }
        } catch (e: Exception) {
            Log.w("AudioEnhancer", "Failed to toggle NoiseSuppressor", e)
        }

        // 3. Equalizer and Speech / Noise Reduction DSP curve
        try {
            equalizer?.let { eq ->
                eq.enabled = settings.equalizerEnabled || settings.noiseReductionEnabled

                val numBands = eq.numberOfBands.toInt()
                val minLevel = eq.bandLevelRange?.get(0)?.toInt() ?: -1500
                val maxLevel = eq.bandLevelRange?.get(1)?.toInt() ?: 1500

                // Find preset curve
                val baseBands = presets.find { it.first == settings.presetName }?.second
                    ?: settings.customBands

                for (i in 0 until minOf(numBands, 5)) {
                    var bandMb = if (i < baseBands.size) baseBands[i] else 0

                    // If Noise Reduction is enabled, filter low-frequency hum & high-frequency hiss,
                    // and boost voice formant clarity (band 2 & 3)
                    if (settings.noiseReductionEnabled) {
                        bandMb = when (i) {
                            0 -> (bandMb - 600).coerceAtLeast(minLevel) // Cut sub-rumble (<100Hz)
                            1 -> (bandMb - 200).coerceAtLeast(minLevel) // Clean low-mid mud
                            2 -> (bandMb + 450).coerceAtMost(maxLevel)  // Boost dialogue presence (1kHz)
                            3 -> (bandMb + 400).coerceAtMost(maxLevel)  // Boost dialogue intelligibility (3kHz)
                            4 -> (bandMb - 500).coerceAtLeast(minLevel) // Cut tape & high hiss (>8kHz)
                            else -> bandMb
                        }
                    }

                    val clamped = bandMb.coerceIn(minLevel, maxLevel).toShort()
                    eq.setBandLevel(i.toShort(), clamped)
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEnhancer", "Failed to configure Equalizer", e)
        }
    }

    fun release() {
        try {
            loudnessEnhancer?.release()
        } catch (e: Exception) { /* ignore */ }
        loudnessEnhancer = null

        try {
            equalizer?.release()
        } catch (e: Exception) { /* ignore */ }
        equalizer = null

        try {
            noiseSuppressor?.release()
        } catch (e: Exception) { /* ignore */ }
        noiseSuppressor = null

        currentSessionId = 0
    }
}
