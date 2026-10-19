package com.example.ui.library

import com.example.R

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItemData
import com.example.ui.theme.MxAccentGreen
import com.example.ui.theme.MxAccentOrange
import com.example.ui.theme.MxCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    videos: List<MediaItemData>,
    onVideoClick: (MediaItemData) -> Unit,
    onOpenSettings: () -> Unit,
    isTvMode: Boolean,
    onToggleTvMode: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Folders, 1: All Videos
    var searchQuery by remember { mutableStateOf("") }
    var selectedFolderFilter by remember { mutableStateOf<String?>(null) }

    val recentVideos = remember(videos) {
        videos.filter { it.lastPlayedPositionMs > 0 }.sortedByDescending { it.dateModified }
    }

    val filteredVideos = remember(videos, searchQuery, selectedFolderFilter) {
        videos.filter { video ->
            val matchesSearch = searchQuery.isBlank() ||
                video.title.contains(searchQuery, ignoreCase = true) ||
                video.folderName.contains(searchQuery, ignoreCase = true)
            val matchesFolder = selectedFolderFilter == null || video.folderName == selectedFolderFilter
            matchesSearch && matchesFolder
        }
    }

    val folders = remember(videos) {
        videos.groupBy { it.folderName }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090E1A))
            .testTag("library_screen")
    ) {
        // App Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F2416)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = coil.compose.rememberAsyncImagePainter(R.drawable.sentinel_video_player_logo_1792390668021),
                        contentDescription = "Sentinel Logo",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Sentinel Video Player",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (isTvMode) "TV & Leanback Mode Active" else "HW+ Video Decoder • Privacy First",
                        fontSize = 11.sp,
                        color = if (isTvMode) MxAccentOrange else Color(0xFF7F92B0)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // TV Mode switch button
                IconButton(
                    onClick = onToggleTvMode,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isTvMode) MxAccentOrange.copy(alpha = 0.2f) else Color.Transparent)
                        .testTag("tv_mode_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "Toggle TV Mode",
                        tint = if (isTvMode) MxAccentOrange else Color.LightGray
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        }

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search videos, movies, series...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MxCyan)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = MxCyan,
                    unfocusedBorderColor = Color(0xFF1E283F),
                    focusedContainerColor = Color(0xFF131A2B),
                    unfocusedContainerColor = Color(0xFF131A2B)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_field")
            )
        }

        // Recent Plays Section (if any have resume positions)
        if (recentVideos.isNotEmpty() && searchQuery.isEmpty() && selectedFolderFilter == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MxAccentOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RECENT PLAYS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MxAccentOrange,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentVideos) { video ->
                        RecentVideoCard(
                            video = video,
                            onClick = { onVideoClick(video) },
                            isTvMode = isTvMode
                        )
                    }
                }
            }
        }

        // Folder Filter Banner (if selected)
        if (selectedFolderFilter != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF192238))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = MxCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Folder: $selectedFolderFilter", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = "Show All",
                    color = MxCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { selectedFolderFilter = null }
                )
            }
        }

        // Tabs: Folders / All Videos
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF090E1A),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MxCyan,
                    height = 3.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Folders (${folders.size})", fontWeight = FontWeight.Bold)
                    }
                },
                selectedContentColor = MxCyan,
                unselectedContentColor = Color.Gray,
                modifier = Modifier.testTag("tab_folders")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("All Videos (${videos.size})", fontWeight = FontWeight.Bold)
                    }
                },
                selectedContentColor = MxCyan,
                unselectedContentColor = Color.Gray,
                modifier = Modifier.testTag("tab_all_videos")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content
        if (selectedTab == 0 && selectedFolderFilter == null) {
            // Folders List View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(folders.entries.toList()) { (folderName, folderVideos) ->
                    FolderItemCard(
                        folderName = folderName,
                        videos = folderVideos,
                        onClick = {
                            selectedFolderFilter = folderName
                            selectedTab = 1
                        },
                        isTvMode = isTvMode
                    )
                }
            }
        } else {
            // Videos List View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredVideos) { video ->
                    VideoItemCard(
                        video = video,
                        onClick = { onVideoClick(video) },
                        isTvMode = isTvMode
                    )
                }
            }
        }
    }
}

@Composable
fun RecentVideoCard(
    video: MediaItemData,
    onClick: () -> Unit,
    isTvMode: Boolean
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141C2E)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .width(if (isTvMode) 220.dp else 170.dp)
            .testTag("recent_card_${video.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTvMode) 120.dp else 95.dp)
                    .background(Color(0xFF222B42)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MxCyan,
                    modifier = Modifier.size(36.dp)
                )

                // Duration Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xCC000000))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = video.durationFormatted, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { video.progressFraction },
                color = MxCyan,
                trackColor = Color(0xFF2E3B59),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = video.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Resume at ${MediaItemData.formatDuration(video.lastPlayedPositionMs)}",
                    color = MxAccentOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FolderItemCard(
    folderName: String,
    videos: List<MediaItemData>,
    onClick: () -> Unit,
    isTvMode: Boolean
) {
    val totalSize = videos.sumOf { it.sizeBytes }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("folder_card_$folderName")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (isTvMode) 58.dp else 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E283F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = MxCyan,
                    modifier = Modifier.size(if (isTvMode) 32.dp else 26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folderName,
                    color = Color.White,
                    fontSize = if (isTvMode) 17.sp else 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${videos.size} videos • ${MediaItemData.formatFileSize(totalSize)}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E283F))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${videos.size}",
                    color = MxCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun VideoItemCard(
    video: MediaItemData,
    onClick: () -> Unit,
    isTvMode: Boolean
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("video_card_${video.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail container
            Box(
                modifier = Modifier
                    .width(if (isTvMode) 130.dp else 105.dp)
                    .height(if (isTvMode) 80.dp else 65.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF222B42)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MxCyan,
                    modifier = Modifier.size(28.dp)
                )

                // Resolution Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xDD000000))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.format,
                        color = MxCyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Duration Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xDD000000))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.durationFormatted,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    color = Color.White,
                    fontSize = if (isTvMode) 15.sp else 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${video.resolution} • ${video.sizeFormatted}",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )

                    if (video.lastPlayedPositionMs > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${MediaItemData.formatDuration(video.lastPlayedPositionMs)}",
                            color = MxAccentOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Play Button
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E283F))
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Video",
                    tint = MxCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
