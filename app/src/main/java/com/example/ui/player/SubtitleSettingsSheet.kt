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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SubtitlesOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubtitleAppearance
import com.example.subtitle.OnlineSubtitleResult
import com.example.subtitle.SubtitleSearchService
import com.example.subtitle.SubtitleTrack
import com.example.ui.theme.MxAccentGreen
import com.example.ui.theme.MxAccentOrange
import com.example.ui.theme.MxCyan
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubtitleSettingsSheet(
    videoTitle: String,
    availableTracks: List<SubtitleTrack>,
    selectedTrack: SubtitleTrack?,
    appearance: SubtitleAppearance,
    onTrackSelected: (SubtitleTrack?) -> Unit,
    onTrackAdded: (SubtitleTrack) -> Unit,
    onAppearanceChanged: (SubtitleAppearance) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val searchService = remember { SubtitleSearchService() }

    var searchQuery by remember { mutableStateOf(videoTitle) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<OnlineSubtitleResult>>(emptyList()) }
    var downloadingId by remember { mutableStateOf<String?>(null) }

    val languages = listOf("English", "Spanish", "French", "German", "Hindi", "Japanese", "Arabic")

    val colorOptions = listOf(
        "White" to 0xFFFFFFFFL,
        "Yellow" to 0xFFFFEB3BL,
        "Cyan" to 0xFF00E5FFL,
        "Green" to 0xFF69F0AEL,
        "Pink" to 0xFFFF80ABL
    )

    val bgOptions = listOf(
        "Translucent" to 0x88000000L,
        "Solid Black" to 0xFF000000L,
        "None" to 0x00000000L
    )

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
                .testTag("subtitle_settings_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = null,
                        tint = MxCyan,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Subtitles & Captions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_subtitle_sheet_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Active Subtitle Tracks
            Text(
                text = "ACTIVE SUBTITLE TRACK",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MxCyan,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option to turn off
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTrack == null) MxCyan.copy(alpha = 0.15f) else Color(0xFF172033))
                    .clickable { onTrackSelected(null) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SubtitlesOff,
                    contentDescription = null,
                    tint = if (selectedTrack == null) MxCyan else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "None (Disable Subtitles)",
                    color = if (selectedTrack == null) MxCyan else Color.White,
                    fontWeight = if (selectedTrack == null) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }

            availableTracks.forEach { track ->
                val isSelected = selectedTrack?.id == track.id
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MxCyan.copy(alpha = 0.15f) else Color(0xFF172033))
                        .clickable { onTrackSelected(track) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = null,
                            tint = if (isSelected) MxCyan else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = track.title,
                                color = if (isSelected) MxCyan else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${track.language} • ${track.cues.size} lines",
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
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Online Subtitle Search & Download
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = MxCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Download Subtitles from Web",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Movie / Series Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = MxCyan,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedLabelColor = MxCyan
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("subtitle_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Language chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        languages.forEach { lang ->
                            FilterChip(
                                selected = selectedLanguage == lang,
                                onClick = { selectedLanguage = lang },
                                label = { Text(lang, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MxCyan,
                                    selectedLabelColor = Color.Black,
                                    containerColor = Color(0xFF222C44),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isSearching = true
                                searchResults = searchService.searchSubtitles(searchQuery, selectedLanguage)
                                isSearching = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MxCyan, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_subtitles_button")
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Searching Online...")
                        } else {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Search Matching Subtitles", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Search Results List
                    if (searchResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "SEARCH RESULTS (${searchResults.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MxAccentGreen
                        )

                        searchResults.forEach { result ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF222C44))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = result.movieTitle,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${result.language} • ${result.format} • ★ ${result.rating}",
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }

                                val isDownloading = downloadingId == result.id
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            downloadingId = result.id
                                            val downloadedTrack = searchService.downloadSubtitle(result)
                                            onTrackAdded(downloadedTrack)
                                            onTrackSelected(downloadedTrack)
                                            downloadingId = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MxAccentGreen,
                                        contentColor = Color.Black
                                    ),
                                    enabled = !isDownloading,
                                    modifier = Modifier.testTag("download_sub_${result.id}")
                                ) {
                                    if (isDownloading) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Timing Sync Offset (-10s to +10s)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Subtitle Timing Synchronization",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                onAppearanceChanged(appearance.copy(delayMs = appearance.delayMs - 100))
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF222C44))
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Delay -100ms", tint = MxCyan)
                        }

                        Text(
                            text = "${if (appearance.delayMs > 0) "+" else ""}${appearance.delayMs} ms",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (appearance.delayMs == 0L) Color.White else MxAccentOrange
                        )

                        IconButton(
                            onClick = {
                                onAppearanceChanged(appearance.copy(delayMs = appearance.delayMs + 100))
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF222C44))
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Delay +100ms", tint = MxCyan)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Reset delay to 0 ms",
                            color = MxCyan,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable { onAppearanceChanged(appearance.copy(delayMs = 0L)) }
                                .padding(4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Subtitle Appearance (Font size, color, background)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Appearance & High-Readability Styling",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Font Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Text Size", fontSize = 13.sp, color = Color.LightGray)
                        Text(text = "${appearance.fontSizeSp.toInt()} sp", fontWeight = FontWeight.Bold, color = MxCyan)
                    }

                    Slider(
                        value = appearance.fontSizeSp,
                        onValueChange = { onAppearanceChanged(appearance.copy(fontSizeSp = it)) },
                        valueRange = 14f..36f,
                        colors = SliderDefaults.colors(thumbColor = MxCyan, activeTrackColor = MxCyan)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Text Color options
                    Text(text = "Text Color", fontSize = 13.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colorOptions.forEach { (name, colorLong) ->
                            val isSelected = appearance.textColor == colorLong
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorLong))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MxAccentOrange else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable { onAppearanceChanged(appearance.copy(textColor = colorLong)) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Background Opacity
                    Text(text = "Background Opacity", fontSize = 13.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bgOptions.forEach { (label, bgLong) ->
                            FilterChip(
                                selected = appearance.backgroundColor == bgLong,
                                onClick = { onAppearanceChanged(appearance.copy(backgroundColor = bgLong)) },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toggles
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAppearanceChanged(appearance.copy(outlineEnabled = !appearance.outlineEnabled)) }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("High Contrast Outline", fontSize = 13.sp, color = Color.LightGray)
                        Switch(
                            checked = appearance.outlineEnabled,
                            onCheckedChange = { onAppearanceChanged(appearance.copy(outlineEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedTrackColor = MxCyan)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAppearanceChanged(appearance.copy(isBold = !appearance.isBold)) }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Bold Font Weight", fontSize = 13.sp, color = Color.LightGray)
                        Switch(
                            checked = appearance.isBold,
                            onCheckedChange = { onAppearanceChanged(appearance.copy(isBold = it)) },
                            colors = SwitchDefaults.colors(checkedTrackColor = MxCyan)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
