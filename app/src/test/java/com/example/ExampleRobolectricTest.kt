package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.AudioEnhancerEngine
import com.example.data.local.MediaPreferences
import com.example.data.model.DecoderType
import com.example.data.model.MediaItemData
import com.example.data.model.ThemeContrastMode
import com.example.subtitle.SubtitleParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MX Player Ultra", appName)
    }

    @Test
    fun testMediaFormatting() {
        assertEquals("00:00", MediaItemData.formatDuration(0L))
        assertEquals("02:30", MediaItemData.formatDuration(150_000L))
        assertEquals("1:05:40", MediaItemData.formatDuration(3940_000L))

        assertEquals("500.0 KB", MediaItemData.formatFileSize(512_000L))
        assertEquals("150.7 MB", MediaItemData.formatFileSize(158_000_000L))
        assertEquals("1.5 GB", MediaItemData.formatFileSize(1_610_612_736L))
    }

    @Test
    fun testSubtitleParser() {
        val srtText = """
            1
            00:01:20,000 --> 00:01:23,500
            Hello <i>World</i>

            2
            00:01:24,000 --> 00:01:28,000
            This is MX Player Ultra!
        """.trimIndent()

        val cues = SubtitleParser.parseSrtOrVtt(srtText)
        assertEquals(2, cues.size)
        assertEquals(80_000L, cues[0].startTimeMs)
        assertEquals(83_500L, cues[0].endTimeMs)
        assertEquals("Hello World", cues[0].text)
        assertEquals("This is MX Player Ultra!", cues[1].text)
    }

    @Test
    fun testPreferences() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = MediaPreferences(context)

        prefs.saveResumePosition("test_uri", 45_000L)
        assertEquals(45_000L, prefs.getResumePosition("test_uri"))

        prefs.setDecoderType(DecoderType.HW_PLUS)
        assertEquals(DecoderType.HW_PLUS, prefs.getDecoderType())

        prefs.setThemeContrastMode(ThemeContrastMode.HIGH_CONTRAST_ACCESSIBILITY)
        assertEquals(ThemeContrastMode.HIGH_CONTRAST_ACCESSIBILITY, prefs.getThemeContrastMode())

        prefs.setPreampGain(175)
        assertEquals(175, prefs.getPreampGain())

        prefs.setNoiseReductionEnabled(true)
        assertTrue(prefs.isNoiseReductionEnabled())
    }

    @Test
    fun testAudioEnhancerPresets() {
        val engine = AudioEnhancerEngine()
        val normalPreset = engine.presets.find { it.first == "Normal" }
        assertNotNull(normalPreset)
        assertEquals(listOf(0, 0, 0, 0, 0), normalPreset?.second)

        val vocalPreset = engine.presets.find { it.first == "Vocal Clarity" }
        assertNotNull(vocalPreset)
        assertEquals(5, vocalPreset?.second?.size)
    }
}
