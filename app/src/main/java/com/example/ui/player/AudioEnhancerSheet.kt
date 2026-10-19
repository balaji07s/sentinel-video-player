package com.example.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioEnhancerSettings
import com.example.ui.theme.MxAccentGreen
import com.example.ui.theme.MxAccentOrange
import com.example.ui.theme.MxCyan
import com.example.ui.theme.MxPreampRed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AudioEnhancerSheet(
    settings: AudioEnhancerSettings,
    presets: List<Pair<String, List<Int>>>,
    onSettingsChanged: (AudioEnhancerSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF101626),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .testTag("audio_enhancer_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = MxCyan,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Audio Enhancer & Equalizer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_audio_enhancer_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. VLC-style Preamp Gain (0% - 200%)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = if (settings.gainPercent > 100) MxPreampRed else MxCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Preamp Volume Boost",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${settings.gainPercent}%",
                                fontWeight = FontWeight.Bold,
                                color = if (settings.gainPercent > 100) MxPreampRed else MxCyan,
                                fontSize = 16.sp
                            )
                            if (settings.gainPercent > 100) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MxPreampRed)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "VLC BOOST",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = settings.gainPercent.toFloat(),
                        onValueChange = { onSettingsChanged(settings.copy(gainPercent = it.toInt())) },
                        valueRange = 0f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = if (settings.gainPercent > 100) MxPreampRed else MxCyan,
                            activeTrackColor = if (settings.gainPercent > 100) MxPreampRed else MxCyan,
                            inactiveTrackColor = Color(0xFF2C3955)
                        ),
                        modifier = Modifier.testTag("preamp_slider")
                    )

                    Text(
                        text = "VLC-style hardware loudness boost up to 200% for quiet tracks and older speakers.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. AI Noise Reduction & Speech Clarity
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSettingsChanged(settings.copy(noiseReductionEnabled = !settings.noiseReductionEnabled)) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (settings.noiseReductionEnabled) MxAccentGreen.copy(alpha = 0.2f) else Color(0xFF222B42)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = null,
                                tint = if (settings.noiseReductionEnabled) MxAccentGreen else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "AI Noise Reduction & Speech Clarity",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Suppresses background hum/hiss and elevates dialog",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Switch(
                        checked = settings.noiseReductionEnabled,
                        onCheckedChange = { onSettingsChanged(settings.copy(noiseReductionEnabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MxAccentGreen
                        ),
                        modifier = Modifier.testTag("noise_reduction_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Presets
            Text(
                text = "EQUALIZER PRESETS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MxCyan,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { (presetName, presetBands) ->
                    val isSelected = settings.presetName == presetName
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onSettingsChanged(
                                settings.copy(
                                    presetName = presetName,
                                    customBands = presetBands
                                )
                            )
                        },
                        label = { Text(presetName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MxCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF1E283F),
                            labelColor = Color.White
                        ),
                        modifier = Modifier.testTag("preset_$presetName")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Band Level Indicators
            val bandLabels = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
            Text(
                text = "5-BAND FREQUENCY CURVE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MxCyan,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0 until 5) {
                    val bandValue = if (i < settings.customBands.size) settings.customBands[i] else 0
                    val dbValue = bandValue / 100 // approx dB

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${if (dbValue > 0) "+" else ""}${dbValue}dB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (dbValue > 0) MxCyan else if (dbValue < 0) MxAccentOrange else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(60.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF26334F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(((Math.abs(dbValue) / 15f) * 60).coerceIn(4f, 60f).dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (dbValue >= 0) MxCyan else MxAccentOrange)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bandLabels[i],
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Reset Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E283F))
                    .clickable {
                        onSettingsChanged(
                            AudioEnhancerSettings(
                                gainPercent = 100,
                                noiseReductionEnabled = false,
                                equalizerEnabled = true,
                                presetName = "Normal",
                                customBands = listOf(0, 0, 0, 0, 0)
                            )
                        )
                    }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset Audio Enhancer to Defaults",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
