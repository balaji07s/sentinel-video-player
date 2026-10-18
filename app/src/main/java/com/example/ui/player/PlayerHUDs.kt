package com.example.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItemData
import com.example.data.model.SubtitleAppearance
import com.example.ui.theme.MxAccentOrange
import com.example.ui.theme.MxCyan
import com.example.ui.theme.MxPreampRed

@Composable
fun VolumeHud(
    volumePercent: Int,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xCC000000))
                .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 20.dp)
                .width(64.dp)
                .testTag("volume_hud")
        ) {
            val isBoost = volumePercent > 100
            val icon = when {
                volumePercent <= 0 -> Icons.Default.VolumeMute
                volumePercent < 50 -> Icons.Default.VolumeDown
                else -> Icons.Default.VolumeUp
            }

            Icon(
                imageVector = icon,
                contentDescription = "Volume",
                tint = if (isBoost) MxPreampRed else MxCyan,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Vertical volume meter
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Fraction 0 to 1 for standard 100%, up to 200% for preamp
                val standardFraction = (volumePercent.coerceAtMost(100) / 100f)
                val boostFraction = if (volumePercent > 100) (volumePercent - 100) / 100f else 0f

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (boostFraction > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(boostFraction * 0.5f)
                                .background(MxPreampRed)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(standardFraction)
                            .background(if (isBoost) MxAccentOrange else MxCyan)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "$volumePercent%",
                color = if (isBoost) MxPreampRed else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            if (isBoost) {
                Text(
                    text = "PREAMP",
                    color = MxPreampRed,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun BrightnessHud(
    brightnessPercent: Int,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xCC000000))
                .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 20.dp)
                .width(64.dp)
                .testTag("brightness_hud")
        ) {
            Icon(
                imageVector = Icons.Default.BrightnessHigh,
                contentDescription = "Brightness",
                tint = MxAccentOrange,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Vertical brightness meter
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.BottomCenter
            ) {
                val fraction = (brightnessPercent.coerceIn(0, 100) / 100f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction)
                        .background(MxAccentOrange)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "$brightnessPercent%",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SeekScrubHud(
    visible: Boolean,
    targetPositionMs: Long,
    deltaMs: Long,
    totalDurationMs: Long,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xDD111827))
                .border(1.dp, MxCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("seek_scrub_hud")
        ) {
            val isForward = deltaMs >= 0
            Icon(
                imageVector = if (isForward) Icons.Default.FastForward else Icons.Default.FastRewind,
                contentDescription = null,
                tint = if (isForward) MxCyan else MxAccentOrange,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "${if (isForward) "+" else ""}${MediaItemData.formatDuration(Math.abs(deltaMs))}",
                    color = if (isForward) MxCyan else MxAccentOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "${MediaItemData.formatDuration(targetPositionMs)} / ${MediaItemData.formatDuration(totalDurationMs)}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SubtitleOverlayView(
    text: String?,
    appearance: SubtitleAppearance,
    modifier: Modifier = Modifier
) {
    if (text.isNullOrBlank()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(appearance.backgroundColor))
                .then(
                    if (appearance.outlineEnabled) {
                        Modifier.border(1.dp, Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    } else Modifier
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = text,
                color = Color(appearance.textColor),
                fontSize = appearance.fontSizeSp.sp,
                fontWeight = if (appearance.isBold) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = (appearance.fontSizeSp * 1.3f).sp
            )
        }
    }
}

@Composable
fun ScreenLockHud(
    isLocked: Boolean,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isLocked) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.TopStart
    ) {
        IconButton(
            onClick = onUnlock,
            modifier = Modifier
                .size(52.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xCC000000))
                .border(2.dp, MxAccentOrange, CircleShape)
                .testTag("unlock_screen_button")
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Unlock Controls",
                tint = MxAccentOrange,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
