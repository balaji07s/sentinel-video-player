package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DecoderType
import com.example.data.model.SubtitleAppearance
import com.example.data.model.ThemeContrastMode

class MediaPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mx_player_ultra_prefs", Context.MODE_PRIVATE)

    fun getResumePosition(uriString: String): Long {
        return prefs.getLong("pos_$uriString", 0L)
    }

    fun saveResumePosition(uriString: String, positionMs: Long) {
        prefs.edit().putLong("pos_$uriString", positionMs).apply()
    }

    fun getDecoderType(): DecoderType {
        val name = prefs.getString("decoder_type", DecoderType.HW.name) ?: DecoderType.HW.name
        return try {
            DecoderType.valueOf(name)
        } catch (e: Exception) {
            DecoderType.HW
        }
    }

    fun setDecoderType(decoderType: DecoderType) {
        prefs.edit().putString("decoder_type", decoderType.name).apply()
    }

    fun getThemeContrastMode(): ThemeContrastMode {
        val name = prefs.getString("theme_mode", ThemeContrastMode.MIDNIGHT_BLUE.name)
            ?: ThemeContrastMode.MIDNIGHT_BLUE.name
        return try {
            ThemeContrastMode.valueOf(name)
        } catch (e: Exception) {
            ThemeContrastMode.MIDNIGHT_BLUE
        }
    }

    fun setThemeContrastMode(mode: ThemeContrastMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun getPreampGain(): Int {
        return prefs.getInt("audio_preamp_gain", 100)
    }

    fun setPreampGain(gain: Int) {
        prefs.edit().putInt("audio_preamp_gain", gain.coerceIn(0, 200)).apply()
    }

    fun isNoiseReductionEnabled(): Boolean {
        return prefs.getBoolean("audio_noise_reduction", false)
    }

    fun setNoiseReductionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("audio_noise_reduction", enabled).apply()
    }

    fun getEqualizerPreset(): String {
        return prefs.getString("eq_preset", "Normal") ?: "Normal"
    }

    fun setEqualizerPreset(preset: String) {
        prefs.edit().putString("eq_preset", preset).apply()
    }

    fun isTvMode(): Boolean {
        return prefs.getBoolean("tv_mode_enabled", false)
    }

    fun setTvMode(enabled: Boolean) {
        prefs.edit().putBoolean("tv_mode_enabled", enabled).apply()
    }

    fun getSubtitleAppearance(): SubtitleAppearance {
        val fontSize = prefs.getFloat("sub_font_size", 20f)
        val textColor = prefs.getLong("sub_text_color", 0xFFFFFFFF)
        val bgColor = prefs.getLong("sub_bg_color", 0x88000000)
        val outline = prefs.getBoolean("sub_outline", true)
        val bold = prefs.getBoolean("sub_bold", true)
        return SubtitleAppearance(
            fontSizeSp = fontSize,
            textColor = textColor,
            backgroundColor = bgColor,
            outlineEnabled = outline,
            isBold = bold,
            delayMs = 0L
        )
    }

    fun saveSubtitleAppearance(appearance: SubtitleAppearance) {
        prefs.edit()
            .putFloat("sub_font_size", appearance.fontSizeSp)
            .putLong("sub_text_color", appearance.textColor)
            .putLong("sub_bg_color", appearance.backgroundColor)
            .putBoolean("sub_outline", appearance.outlineEnabled)
            .putBoolean("sub_bold", appearance.isBold)
            .apply()
    }

    fun clearAllHistory() {
        val allKeys = prefs.all.keys
        val editor = prefs.edit()
        for (key in allKeys) {
            if (key.startsWith("pos_")) {
                editor.remove(key)
            }
        }
        editor.apply()
    }
}
