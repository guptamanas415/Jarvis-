package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.JarvisBottomNavigation
import com.example.ui.components.JarvisScreen
import com.example.ui.components.JarvisTopBar
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HomeworkScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBorder
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceContainer
import com.example.ui.theme.JarvisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JarvisApp()
        }
    }
}

@Composable
fun JarvisApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(JarvisScreen.HOME) }
    val hudTheme by viewModel.preferences.hudTheme.collectAsState()
    val confirmationState by viewModel.confirmationState.collectAsState()
    val notificationMessage by viewModel.notificationMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Request necessary runtime permissions (Microphone & Phone State)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val recordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (!recordAudioGranted) {
            viewModel.showNotification("Microphone permission required for vocal speech recognition.")
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    LaunchedEffect(notificationMessage) {
        notificationMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Predictive back handler to return to Home from other sub-screens
    BackHandler(enabled = currentScreen != JarvisScreen.HOME) {
        currentScreen = JarvisScreen.HOME
    }

    JarvisTheme(hudTheme = hudTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = JarvisSurface,
            topBar = {
                JarvisTopBar(
                    sectionTitle = currentScreen.title,
                    onAvatarClick = { currentScreen = JarvisScreen.SETTINGS }
                )
            },
            bottomBar = {
                JarvisBottomNavigation(
                    currentScreen = currentScreen,
                    onSelectScreen = { currentScreen = it }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    JarvisScreen.HOME -> HomeScreen(viewModel = viewModel)
                    JarvisScreen.CHAT -> ChatScreen(viewModel = viewModel)
                    JarvisScreen.APPS -> AppsScreen(viewModel = viewModel)
                    JarvisScreen.HOMEWORK -> HomeworkScreen(viewModel = viewModel)
                    JarvisScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }

                // Confirmation Dialog for Sensitive Actions
                if (confirmationState.isVisible) {
                    AlertDialog(
                        onDismissRequest = confirmationState.onDismiss,
                        title = {
                            Text(
                                text = "JARVIS Authorization Required",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = JarvisCyan
                            )
                        },
                        text = {
                            Text(
                                text = confirmationState.prompt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = confirmationState.onConfirm,
                                colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan)
                            ) {
                                Text("Authorize", color = Color(0xFF00373A), fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = confirmationState.onDismiss) {
                                Text("Abort", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        },
                        containerColor = JarvisSurfaceContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}
