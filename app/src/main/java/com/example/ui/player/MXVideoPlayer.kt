package com.example.ui.player

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.audio.AudioEnhancerEngine
import com.example.data.local.MediaPreferences
import com.example.data.model.AspectRatioMode
import com.example.data.model.AudioEnhancerSettings
import com.example.data.model.DecoderType
import com.example.data.model.MediaItemData
import com.example.data.model.SubtitleAppearance
import com.example.subtitle.SubtitleCue
import com.example.subtitle.SubtitleTrack
import com.example.ui.theme.MxAccentGreen
import com.example.ui.theme.MxAccentOrange
import com.example.ui.theme.MxCyan
import com.example.ui.theme.MxPreampRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun MXVideoPlayer(
    video: MediaItemData,
    playlist: List<MediaItemData>,
    onNavigateBack: () -> Unit,
    onPlayNext: ((MediaItemData) -> Unit)? = null,
    onPlayPrev: ((MediaItemData) -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val preferences = remember { MediaPreferences(context) }
    val audioEnhancer = remember { AudioEnhancerEngine() }
    val scope = rememberCoroutineScope()

    // Preferences & Settings State
    var decoderType by remember { mutableStateOf(preferences.getDecoderType()) }
    var aspectRatioMode by remember { mutableStateOf(AspectRatioMode.FIT) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var audioSettings by remember {
        mutableStateOf(
            AudioEnhancerSettings(
                gainPercent = preferences.getPreampGain(),
                noiseReductionEnabled = preferences.isNoiseReductionEnabled(),
                presetName = preferences.getEqualizerPreset()
            )
        )
    }
    var subtitleAppearance by remember { mutableStateOf(preferences.getSubtitleAppearance()) }

    // Subtitles state
    var availableSubtitleTracks by remember { mutableStateOf<List<SubtitleTrack>>(emptyList()) }
    var selectedSubtitleTrack by remember { mutableStateOf<SubtitleTrack?>(null) }
    var currentSubtitleText by remember { mutableStateOf<String?>(null) }

    // Playback state
    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(video.lastPlayedPositionMs) }
    var totalDurationMs by remember { mutableLongStateOf(video.durationMs) }
    var isBuffering by remember { mutableStateOf(true) }

    // UI & Controls Visibility
    var areControlsVisible by remember { mutableStateOf(true) }
    var isScreenLocked by remember { mutableStateOf(false) }
    var isAudioSheetOpen by remember { mutableStateOf(false) }
    var isSubtitleSheetOpen by remember { mutableStateOf(false) }
    var isAspectCropSheetOpen by remember { mutableStateOf(false) }

    // Gestures State & HUDs
    var isVolumeHudVisible by remember { mutableStateOf(false) }
    var volumePercent by remember { mutableIntStateOf(audioSettings.gainPercent) }
    var isBrightnessHudVisible by remember { mutableStateOf(false) }
    var brightnessPercent by remember { mutableIntStateOf(70) }
    var isSeekHudVisible by remember { mutableStateOf(false) }
    var seekTargetMs by remember { mutableLongStateOf(0L) }
    var seekDeltaMs by remember { mutableLongStateOf(0L) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    // ExoPlayer build
    val exoPlayer = remember(decoderType) {
        val renderersFactory = DefaultRenderersFactory(context).apply {
            setExtensionRendererMode(
                when (decoderType) {
                    DecoderType.HW -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                    DecoderType.HW_PLUS -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
                    DecoderType.SW -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
                }
            )
            setEnableDecoderFallback(true)
        }

        ExoPlayer.Builder(context, renderersFactory)
            .setAudioAttributes(
                androidx.media3.common.AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()
    }

    // Initialize initial sample subtitles if applicable
    LaunchedEffect(video.id) {
        val defaultCues = listOf(
            SubtitleCue(1000L, 4500L, "MX Player Ultra: Hardware Accelerated Playback"),
            SubtitleCue(5000L, 8500L, "AI Noise Reduction & VLC Preamp 200% Active"),
            SubtitleCue(9000L, 14000L, "Swipe left for Brightness • Swipe right for Volume"),
            SubtitleCue(15000L, 20000L, "Enjoy privacy-first local media playback")
        )
        val initialTrack = SubtitleTrack(
            id = "default_en",
            language = "English",
            title = "English [Auto-Generated]",
            cues = defaultCues
        )
        availableSubtitleTracks = listOf(initialTrack)
        selectedSubtitleTrack = initialTrack
    }

    // Setup player source & listeners
    DisposableEffect(exoPlayer, video.uri) {
        val mediaItem = MediaItem.fromUri(video.uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.seekTo(video.lastPlayedPositionMs)
        exoPlayer.playbackParameters = androidx.media3.common.PlaybackParameters(playbackSpeed)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = (playbackState == Player.STATE_BUFFERING)
                if (playbackState == Player.STATE_READY) {
                    totalDurationMs = exoPlayer.duration.coerceAtLeast(0L)
                    val sessionId = exoPlayer.audioSessionId
                    if (sessionId != C.AUDIO_SESSION_ID_UNSET) {
                        audioEnhancer.attachToAudioSession(sessionId, audioSettings)
                    }
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            preferences.saveResumePosition(video.uri.toString(), exoPlayer.currentPosition)
            exoPlayer.removeListener(listener)
            exoPlayer.release()
            audioEnhancer.release()
        }
    }

    // Audio Enhancer settings sync
    LaunchedEffect(audioSettings) {
        audioEnhancer.applySettings(audioSettings)
        preferences.setPreampGain(audioSettings.gainPercent)
        preferences.setNoiseReductionEnabled(audioSettings.noiseReductionEnabled)
        preferences.setEqualizerPreset(audioSettings.presetName)
    }

    // Periodic timer for progress tracking & subtitle cues
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val pos = exoPlayer.currentPosition
            currentPositionMs = pos

            // Subtitle sync
            val track = selectedSubtitleTrack
            if (track != null && track.cues.isNotEmpty()) {
                val syncPos = pos + subtitleAppearance.delayMs
                val activeCue = track.cues.firstOrNull { it.startTimeMs <= syncPos && syncPos <= it.endTimeMs }
                currentSubtitleText = activeCue?.text
            } else {
                currentSubtitleText = null
            }

            delay(250)
        }
    }

    // Controls Auto-Hide timer
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying && !isScreenLocked) {
            delay(4000)
            areControlsVisible = false
        }
    }

    // System Brightness Adjustment
    val setScreenBrightness: (Int) -> Unit = { percent ->
        activity?.window?.let { window ->
            val layoutParams = window.attributes
            layoutParams.screenBrightness = (percent.coerceIn(5, 100) / 100f)
            window.attributes = layoutParams
        }
    }

    BackHandler {
        preferences.saveResumePosition(video.uri.toString(), currentPositionMs)
        onNavigateBack()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("mx_player_screen")
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // 1. Video Surface with AspectRatio & Zoom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = zoomScale,
                    scaleY = zoomScale
                ),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = when (aspectRatioMode) {
                            AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                            AspectRatioMode.ZOOM_CROP -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                            AspectRatioMode.RATIO_16_9 -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                            AspectRatioMode.RATIO_4_3 -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                            AspectRatioMode.CUSTOM_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        }
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { playerView ->
                    playerView.resizeMode = when (aspectRatioMode) {
                        AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                        AspectRatioMode.ZOOM_CROP -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                        AspectRatioMode.RATIO_16_9 -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                        AspectRatioMode.RATIO_4_3 -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                        AspectRatioMode.CUSTOM_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Full Gesture Detector Overlay
        if (!isScreenLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            if (zoom != 1f) {
                                zoomScale = (zoomScale * zoom).coerceIn(1.0f, 2.5f)
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                areControlsVisible = !areControlsVisible
                            },
                            onDoubleTap = { offset ->
                                val x = offset.x
                                val width = size.width
                                when {
                                    x < width * 0.35f -> {
                                        // Left double tap: seek -10s
                                        val target = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                                        exoPlayer.seekTo(target)
                                        currentPositionMs = target
                                        seekTargetMs = target
                                        seekDeltaMs = -10_000L
                                        isSeekHudVisible = true
                                        scope.launch {
                                            delay(1200)
                                            isSeekHudVisible = false
                                        }
                                    }
                                    x > width * 0.65f -> {
                                        // Right double tap: seek +10s
                                        val target = (exoPlayer.currentPosition + 10_000L).coerceAtMost(totalDurationMs)
                                        exoPlayer.seekTo(target)
                                        currentPositionMs = target
                                        seekTargetMs = target
                                        seekDeltaMs = +10_000L
                                        isSeekHudVisible = true
                                        scope.launch {
                                            delay(1200)
                                            isSeekHudVisible = false
                                        }
                                    }
                                    else -> {
                                        // Center double tap: play/pause
                                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                                    }
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        var dragDirection: String? = null // "vertical_left", "vertical_right", "horizontal"
                        var initialDragY = 0f
                        var initialDragX = 0f
                        var startVal = 0

                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: continue

                                if (change.pressed) {
                                    if (dragDirection == null) {
                                        val position = change.position
                                        initialDragX = position.x
                                        initialDragY = position.y

                                        if (position.x < size.width * 0.5f) {
                                            dragDirection = "vertical_left"
                                            startVal = brightnessPercent
                                        } else {
                                            dragDirection = "vertical_right"
                                            startVal = volumePercent
                                        }
                                    } else {
                                        val deltaY = initialDragY - change.position.y
                                        val deltaX = change.position.x - initialDragX

                                        // Horizontal seek scrub
                                        if (Math.abs(deltaX) > 60 && Math.abs(deltaX) > Math.abs(deltaY) * 1.5f) {
                                            dragDirection = "horizontal"
                                            val seekFraction = deltaX / size.width
                                            val scrubDelta = (seekFraction * 60_000L).toLong() // up to 60s per drag
                                            seekDeltaMs = scrubDelta
                                            seekTargetMs = (exoPlayer.currentPosition + scrubDelta).coerceIn(0L, totalDurationMs)
                                            isSeekHudVisible = true
                                        } else if (dragDirection == "vertical_left") {
                                            // Brightness drag
                                            val deltaPercent = (deltaY / (size.height * 0.6f) * 100).toInt()
                                            brightnessPercent = (startVal + deltaPercent).coerceIn(5, 100)
                                            setScreenBrightness(brightnessPercent)
                                            isBrightnessHudVisible = true
                                        } else if (dragDirection == "vertical_right") {
                                            // Volume & Preamp drag (0 - 200%)
                                            val deltaPercent = (deltaY / (size.height * 0.6f) * 100).toInt()
                                            val newVol = (startVal + deltaPercent).coerceIn(0, 200)
                                            volumePercent = newVol
                                            audioSettings = audioSettings.copy(gainPercent = newVol)
                                            isVolumeHudVisible = true
                                        }
                                    }
                                } else {
                                    // Pointer released
                                    if (dragDirection == "horizontal" && isSeekHudVisible) {
                                        exoPlayer.seekTo(seekTargetMs)
                                        currentPositionMs = seekTargetMs
                                    }
                                    dragDirection = null
                                    scope.launch {
                                        delay(800)
                                        isVolumeHudVisible = false
                                        isBrightnessHudVisible = false
                                        isSeekHudVisible = false
                                    }
                                }
                            }
                        }
                    }
            )
        }

        // 3. Subtitle Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (areControlsVisible) 110.dp else 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            SubtitleOverlayView(
                text = currentSubtitleText,
                appearance = subtitleAppearance
            )
        }

        // 4. On-Screen HUDs (Volume, Brightness, Seek)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrightnessHud(brightnessPercent = brightnessPercent, visible = isBrightnessHudVisible)
                SeekScrubHud(
                    visible = isSeekHudVisible,
                    targetPositionMs = seekTargetMs,
                    deltaMs = seekDeltaMs,
                    totalDurationMs = totalDurationMs
                )
                VolumeHud(volumePercent = volumePercent, visible = isVolumeHudVisible)
            }
        }

        // 5. Buffering Indicator
        if (isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MxCyan,
                    modifier = Modifier.size(54.dp),
                    strokeWidth = 3.dp
                )
            }
        }

        // 6. Screen Lock HUD
        ScreenLockHud(
            isLocked = isScreenLocked,
            onUnlock = {
                isScreenLocked = false
                areControlsVisible = true
            }
        )

        // 7. Full Controls (Top & Bottom Bars)
        AnimatedVisibility(
            visible = areControlsVisible && !isScreenLocked,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top gradient bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xEE000000), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = {
                                    preferences.saveResumePosition(video.uri.toString(), currentPositionMs)
                                    onNavigateBack()
                                },
                                modifier = Modifier.testTag("player_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Column {
                                Text(
                                    text = video.title,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${video.resolution} • ${video.format}",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Right Action Controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Decoder Pill (HW / HW+ / SW)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (decoderType) {
                                            DecoderType.HW -> MxCyan.copy(alpha = 0.25f)
                                            DecoderType.HW_PLUS -> MxAccentGreen.copy(alpha = 0.25f)
                                            DecoderType.SW -> MxAccentOrange.copy(alpha = 0.25f)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when (decoderType) {
                                            DecoderType.HW -> MxCyan
                                            DecoderType.HW_PLUS -> MxAccentGreen
                                            DecoderType.SW -> MxAccentOrange
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        val next = when (decoderType) {
                                            DecoderType.HW -> DecoderType.HW_PLUS
                                            DecoderType.HW_PLUS -> DecoderType.SW
                                            DecoderType.SW -> DecoderType.HW
                                        }
                                        decoderType = next
                                        preferences.setDecoderType(next)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("decoder_toggle_pill")
                            ) {
                                Text(
                                    text = decoderType.label,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Equalizer / Audio Enhancer button
                            IconButton(
                                onClick = { isAudioSheetOpen = true },
                                modifier = Modifier.testTag("open_audio_enhancer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Audio Enhancer",
                                    tint = if (audioSettings.gainPercent > 100 || audioSettings.noiseReductionEnabled) MxCyan else Color.White
                                )
                            }

                            // Subtitles button
                            IconButton(
                                onClick = { isSubtitleSheetOpen = true },
                                modifier = Modifier.testTag("open_subtitles_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Subtitles,
                                    contentDescription = "Subtitles",
                                    tint = if (selectedSubtitleTrack != null) MxCyan else Color.White
                                )
                            }

                            // Aspect Ratio Cycle button
                            IconButton(
                                onClick = { isAspectCropSheetOpen = true },
                                modifier = Modifier.testTag("aspect_ratio_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AspectRatio,
                                    contentDescription = "Aspect Ratio",
                                    tint = Color.White
                                )
                            }

                            // Lock Screen button
                            IconButton(
                                onClick = { isScreenLocked = true },
                                modifier = Modifier.testTag("lock_screen_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockOpen,
                                    contentDescription = "Lock Touch Controls",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Bottom gradient bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xEE000000))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Time & Scrubber Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = MediaItemData.formatDuration(currentPositionMs),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Slider(
                                value = if (totalDurationMs > 0) (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f) else 0f,
                                onValueChange = { fraction ->
                                    val newPos = (fraction * totalDurationMs).toLong()
                                    currentPositionMs = newPos
                                    exoPlayer.seekTo(newPos)
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = MxCyan,
                                    activeTrackColor = MxCyan,
                                    inactiveTrackColor = Color(0x55FFFFFF)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .testTag("playback_scrubber")
                            )

                            Text(
                                text = MediaItemData.formatDuration(totalDurationMs),
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }

                        // Playback Control Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Aspect Ratio Label indicator
                            Text(
                                text = aspectRatioMode.label,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )

                            // Main Controls (Prev, -10s, Play/Pause, +10s, Next)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Previous video
                                if (onPlayPrev != null) {
                                    IconButton(
                                        onClick = {
                                            val idx = playlist.indexOfFirst { it.id == video.id }
                                            if (idx > 0) onPlayPrev(playlist[idx - 1])
                                        },
                                        modifier = Modifier.testTag("prev_video_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
                                    }
                                }

                                // -10s
                                IconButton(
                                    onClick = {
                                        val target = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                                        exoPlayer.seekTo(target)
                                        currentPositionMs = target
                                    },
                                    modifier = Modifier.testTag("rewind_10s_button")
                                ) {
                                    Icon(imageVector = Icons.Default.FastRewind, contentDescription = "Rewind 10s", tint = Color.White)
                                }

                                // Play / Pause Button
                                IconButton(
                                    onClick = {
                                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                                    },
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(MxCyan)
                                        .testTag("play_pause_button")
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = Color.Black,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                // +10s
                                IconButton(
                                    onClick = {
                                        val target = (exoPlayer.currentPosition + 10_000L).coerceAtMost(totalDurationMs)
                                        exoPlayer.seekTo(target)
                                        currentPositionMs = target
                                    },
                                    modifier = Modifier.testTag("forward_10s_button")
                                ) {
                                    Icon(imageVector = Icons.Default.FastForward, contentDescription = "Forward 10s", tint = Color.White)
                                }

                                // Next video
                                if (onPlayNext != null) {
                                    IconButton(
                                        onClick = {
                                            val idx = playlist.indexOfFirst { it.id == video.id }
                                            if (idx >= 0 && idx < playlist.size - 1) onPlayNext(playlist[idx + 1])
                                        },
                                        modifier = Modifier.testTag("next_video_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                                    }
                                }
                            }

                            // Playback Speed button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF222C44))
                                    .clickable {
                                        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                                        val nextIdx = (speeds.indexOf(playbackSpeed) + 1) % speeds.size
                                        val newSpeed = speeds[nextIdx]
                                        playbackSpeed = newSpeed
                                        exoPlayer.playbackParameters = androidx.media3.common.PlaybackParameters(newSpeed)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("playback_speed_button")
                            ) {
                                Text(
                                    text = "${playbackSpeed}x",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. Modals / Bottom Sheets
        if (isAudioSheetOpen) {
            AudioEnhancerSheet(
                settings = audioSettings,
                presets = audioEnhancer.presets,
                onSettingsChanged = { updated ->
                    audioSettings = updated
                    volumePercent = updated.gainPercent
                },
                onDismiss = { isAudioSheetOpen = false }
            )
        }

        if (isSubtitleSheetOpen) {
            SubtitleSettingsSheet(
                videoTitle = video.title,
                availableTracks = availableSubtitleTracks,
                selectedTrack = selectedSubtitleTrack,
                appearance = subtitleAppearance,
                onTrackSelected = { selectedSubtitleTrack = it },
                onTrackAdded = { newTrack ->
                    availableSubtitleTracks = availableSubtitleTracks + newTrack
                },
                onAppearanceChanged = { updated ->
                    subtitleAppearance = updated
                    preferences.saveSubtitleAppearance(updated)
                },
                onDismiss = { isSubtitleSheetOpen = false }
            )
        }

        if (isAspectCropSheetOpen) {
            AspectCropSheet(
                currentMode = aspectRatioMode,
                zoomScale = zoomScale,
                onModeSelected = { mode ->
                    aspectRatioMode = mode
                },
                onZoomChanged = { scale ->
                    zoomScale = scale
                },
                onDismiss = { isAspectCropSheetOpen = false }
            )
        }
    }
}
