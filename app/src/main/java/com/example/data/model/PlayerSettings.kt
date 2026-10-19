package com.example.data.model

enum class DecoderType(val label: String, val description: String) {
    HW("HW", "Hardware Decoder (Fastest, Battery-friendly)"),
    HW_PLUS("HW+", "Hardware+ Surface Decoder (Advanced HW acceleration)"),
    SW("SW", "Software Decoder (Full FFmpeg codec fallback for all formats)")
}

enum class AspectRatioMode(val label: String) {
    FIT("Fit to Screen"),
    ZOOM_CROP("Crop & Fill"),
    STRETCH("Stretch to Screen"),
    RATIO_16_9("16:9 Widescreen"),
    RATIO_4_3("4:3 Standard"),
    CUSTOM_ZOOM("Custom Zoom & Crop")
}

enum class ThemeContrastMode(val label: String, val subtitle: String) {
    CYBER_VIOLET("Cyber Neon Dark", "Deep obsidian with electric cyan & violet"),
    ELECTRIC_LIGHT("Electric Light", "Clean bright mode with violet accents"),
    EMERALD_GREEN("Emerald Green", "Fresh vibrant green and dark green background"),
    SUNSET_ORANGE("Sunset Orange", "Warm amber and rich dark red-orange tones"),
    ROYAL_INDIGO("Royal Indigo", "Deep royal blue and vibrant gold accents"),
    HIGH_CONTRAST_ACCESSIBILITY("High Contrast", "High-visibility yellow & cyan for accessibility"),
    SEPIA_NIGHT("Night Sepia", "Warm tones for low-light viewing")
}

data class AudioEnhancerSettings(
    val gainPercent: Int = 100, // 0% to 200% (VLC Preamp style)
    val noiseReductionEnabled: Boolean = false,
    val noiseReductionLevel: Float = 0.7f,
    val equalizerEnabled: Boolean = true,
    val presetName: String = "Normal",
    val customBands: List<Int> = listOf(0, 0, 0, 0, 0) // 5 bands in mB
)

data class SubtitleAppearance(
    val fontSizeSp: Float = 20f,
    val textColor: Long = 0xFFFFFFFF,
    val backgroundColor: Long = 0x88000000,
    val outlineEnabled: Boolean = true,
    val isBold: Boolean = true,
    val delayMs: Long = 0L // Negative or positive offset
)
