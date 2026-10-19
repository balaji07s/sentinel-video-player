package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DecoderType
import com.example.data.model.ThemeContrastMode
import com.example.ui.theme.HighContrastYellow
import com.example.ui.theme.MxAccentGreen
import com.example.ui.theme.MxAccentOrange
import com.example.ui.theme.MxCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentThemeMode: ThemeContrastMode,
    currentDecoder: DecoderType,
    isTvMode: Boolean,
    isNoiseReductionEnabled: Boolean,
    onThemeModeChanged: (ThemeContrastMode) -> Unit,
    onDecoderChanged: (DecoderType) -> Unit,
    onTvModeToggled: (Boolean) -> Unit,
    onNoiseReductionToggled: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090E1A))
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Player Settings & Preferences",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            // 1. Theme & Contrast Modes
            SectionHeader(icon = Icons.Default.Visibility, title = "ACCESSIBILITY & HIGH-CONTRAST THEMES")
            Spacer(modifier = Modifier.height(10.dp))

            ThemeContrastMode.values().forEach { mode ->
                val isSelected = currentThemeMode == mode
                Card(
                    onClick = { onThemeModeChanged(mode) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1B253D) else Color(0xFF131A2B)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("theme_mode_${mode.name}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (mode) {
                                            ThemeContrastMode.CYBER_VIOLET -> MxCyan
                                            ThemeContrastMode.ELECTRIC_LIGHT -> Color.White
                                            ThemeContrastMode.EMERALD_GREEN -> Color(0xFF00E676)
                                            ThemeContrastMode.SUNSET_ORANGE -> Color(0xFFFF9800)
                                            ThemeContrastMode.ROYAL_INDIGO -> Color(0xFF536DFE)
                                            ThemeContrastMode.HIGH_CONTRAST_ACCESSIBILITY -> HighContrastYellow
                                            ThemeContrastMode.SEPIA_NIGHT -> Color(0xFFFFB300)
                                        }
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = mode.label,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = mode.subtitle,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MxCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Hardware Acceleration
            SectionHeader(icon = Icons.Default.Memory, title = "VIDEO DECODING ENGINE")
            Spacer(modifier = Modifier.height(10.dp))

            DecoderType.values().forEach { dec ->
                val isSelected = currentDecoder == dec
                Card(
                    onClick = { onDecoderChanged(dec) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1B253D) else Color(0xFF131A2B)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("decoder_choice_${dec.name}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = dec.label,
                                    color = MxCyan,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (dec == DecoderType.HW) "(Recommended)" else "",
                                    color = MxAccentGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = dec.description,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MxCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Audio & TV Mode toggles
            SectionHeader(icon = Icons.Default.GraphicEq, title = "AUDIO & TV PREFERENCES")
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Default AI Noise Reduction",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cleans up dialogue and removes mic background hum",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = isNoiseReductionEnabled,
                            onCheckedChange = onNoiseReductionToggled,
                            colors = SwitchDefaults.colors(checkedTrackColor = MxAccentGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Android TV & D-Pad Optimization",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Enhances card sizes and focus indicators for remotes",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = isTvMode,
                            onCheckedChange = onTvModeToggled,
                            colors = SwitchDefaults.colors(checkedTrackColor = MxAccentOrange)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Privacy & Performance
            SectionHeader(icon = Icons.Default.Security, title = "PRIVACY & LOCAL DEVICE COMMITMENT")
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MxAccentGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MxAccentGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "100% Ad-Free & Local Playback",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "• Zero ads and zero third-party tracking.\n• Video playback executes purely on your local hardware.\n• Hardware decoders are tuned for minimal battery and memory overhead on older phones & Android TVs.",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Clear History Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2B161B))
                    .clickable { onClearHistory() }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MxAccentOrange,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Clear Resume Progress & History",
                    color = MxAccentOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MxCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MxCyan,
            letterSpacing = 1.sp
        )
    }
}
