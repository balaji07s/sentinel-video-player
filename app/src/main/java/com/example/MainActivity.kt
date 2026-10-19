package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.data.local.LocalMediaScanner
import com.example.data.local.MediaPreferences
import com.example.data.model.MediaItemData
import com.example.data.model.ThemeContrastMode
import com.example.ui.library.LibraryScreen
import com.example.ui.player.MXVideoPlayer
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Library : Screen()
    data class Player(val video: MediaItemData) : Screen()
    object Settings : Screen()
}

class MainActivity : ComponentActivity() {
    private lateinit var preferences: MediaPreferences
    private lateinit var mediaScanner: LocalMediaScanner

    private var currentScreen by mutableStateOf<Screen>(Screen.Library)
    private var videoList by mutableStateOf<List<MediaItemData>>(emptyList())
    private var themeMode by mutableStateOf(ThemeContrastMode.CYBER_VIOLET)
    private var isTvMode by mutableStateOf(false)
    private var isNoiseReductionEnabled by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        refreshVideoList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep screen on during video playback
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        preferences = MediaPreferences(this)
        mediaScanner = LocalMediaScanner(this, preferences)
        themeMode = preferences.getThemeContrastMode()
        isTvMode = preferences.isTvMode()
        isNoiseReductionEnabled = preferences.isNoiseReductionEnabled()

        checkPermissionsAndLoadVideos()
        handleIncomingIntent(intent)

        setContent {
            MyApplicationTheme(contrastMode = themeMode) {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                // Fullscreen immersive mode when in player
                LaunchedEffect(currentScreen) {
                    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
                    if (currentScreen is Screen.Player) {
                        windowInsetsController.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                    } else {
                        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = if (currentScreen is Screen.Player) {
                            Modifier.fillMaxSize()
                        } else {
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        }
                    ) {
                        when (val screen = currentScreen) {
                            is Screen.Library -> {
                                LibraryScreen(
                                    videos = videoList,
                                    onVideoClick = { video ->
                                        currentScreen = Screen.Player(video)
                                    },
                                    onOpenSettings = {
                                        currentScreen = Screen.Settings
                                    },
                                    isTvMode = isTvMode,
                                    onToggleTvMode = {
                                        val newTvMode = !isTvMode
                                        isTvMode = newTvMode
                                        preferences.setTvMode(newTvMode)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                if (newTvMode) "TV & Leanback Mode Enabled" else "Standard Mobile Mode Enabled"
                                            )
                                        }
                                    }
                                )
                            }

                            is Screen.Player -> {
                                MXVideoPlayer(
                                    video = screen.video,
                                    playlist = videoList,
                                    onNavigateBack = {
                                        currentScreen = Screen.Library
                                        refreshVideoList()
                                    },
                                    onPlayNext = { nextVideo ->
                                        currentScreen = Screen.Player(nextVideo)
                                    },
                                    onPlayPrev = { prevVideo ->
                                        currentScreen = Screen.Player(prevVideo)
                                    }
                                )
                            }

                            is Screen.Settings -> {
                                SettingsScreen(
                                    currentThemeMode = themeMode,
                                    currentDecoder = preferences.getDecoderType(),
                                    isTvMode = isTvMode,
                                    isNoiseReductionEnabled = isNoiseReductionEnabled,
                                    onThemeModeChanged = { mode ->
                                        themeMode = mode
                                        preferences.setThemeContrastMode(mode)
                                    },
                                    onDecoderChanged = { dec ->
                                        preferences.setDecoderType(dec)
                                    },
                                    onTvModeToggled = { enabled ->
                                        isTvMode = enabled
                                        preferences.setTvMode(enabled)
                                    },
                                    onNoiseReductionToggled = { enabled ->
                                        isNoiseReductionEnabled = enabled
                                        preferences.setNoiseReductionEnabled(enabled)
                                    },
                                    onClearHistory = {
                                        preferences.clearAllHistory()
                                        refreshVideoList()
                                        scope.launch {
                                            snackbarHostState.showSnackbar("History & Resume Progress Cleared")
                                        }
                                    },
                                    onNavigateBack = {
                                        currentScreen = Screen.Library
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val action = intent?.action
        val data: Uri? = intent?.data
        if (action == Intent.ACTION_VIEW && data != null) {
            val title = data.lastPathSegment ?: "External Video"
            val externalItem = MediaItemData(
                id = "external_${System.currentTimeMillis()}",
                uri = data,
                title = title.substringBeforeLast("."),
                folderName = "External Stream",
                durationMs = 0L,
                sizeBytes = 0L,
                resolution = "HD Video",
                mimeType = intent.type ?: "video/*",
                format = title.substringAfterLast(".", "MP4").uppercase()
            )
            currentScreen = Screen.Player(externalItem)
        }
    }

    private fun checkPermissionsAndLoadVideos() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }

        refreshVideoList()
    }

    private fun refreshVideoList() {
        kotlinx.coroutines.MainScope().launch {
            videoList = mediaScanner.loadAllVideos()
        }
    }

    // Android TV / Remote D-Pad Navigation Support
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                // Handled natively by Compose
                return super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_BACK -> {
                if (currentScreen !is Screen.Library) {
                    currentScreen = Screen.Library
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
