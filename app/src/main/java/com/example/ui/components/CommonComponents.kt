package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBorder
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceContainer
import com.example.ui.theme.JarvisSurfaceContainerHigh
import com.example.voice.VoiceState

@Composable
fun JarvisTopBar(
    sectionTitle: String,
    onAvatarClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .background(JarvisSurface.copy(alpha = 0.95f))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = "Neural link",
                tint = JarvisCyan,
                modifier = Modifier.size(24.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "JARVIS // ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = sectionTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = JarvisCyan
                )
            }
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(JarvisSurfaceContainerHigh)
                .border(1.dp, JarvisCyan.copy(alpha = 0.3f), CircleShape)
                .clickable(onClick = onAvatarClick)
                .testTag("avatar_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User profile",
                tint = JarvisCyan,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

enum class JarvisScreen(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    CHAT("Chat", Icons.AutoMirrored.Filled.Chat),
    APPS("Apps", Icons.Default.Apps),
    HOMEWORK("Homework", Icons.Default.School),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun JarvisBottomNavigation(
    currentScreen: JarvisScreen,
    onSelectScreen: (JarvisScreen) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(72.dp)
            .background(JarvisSurface.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                color = JarvisCyanBorder.copy(alpha = 0.3f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        JarvisScreen.entries.forEach { screen ->
            val isSelected = currentScreen == screen
            val activeBg = if (isSelected) JarvisCyan.copy(alpha = 0.12f) else Color.Transparent
            val contentColor = if (isSelected) JarvisCyan else MaterialTheme.colorScheme.onSurfaceVariant

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(activeBg)
                    .clickable { onSelectScreen(screen) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("nav_tab_${screen.name.lowercase()}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = screen.icon,
                    contentDescription = screen.title,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = screen.title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun JarvisVoiceVisualizerHub(
    voiceState: VoiceState,
    rmsAmplitude: Float,
    isListening: Boolean,
    onToggleListen: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rings")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1.15f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    val reactiveAmpScale = 1f + (rmsAmplitude * 0.35f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(JarvisSurfaceContainer.copy(alpha = 0.7f))
            .border(1.dp, JarvisCyanBorder, RoundedCornerShape(20.dp))
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Outer concentric pulsing rings
        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ring 3 (Outer most)
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .scale(if (isListening) pulseScale2 * reactiveAmpScale else 1f)
                    .border(1.dp, JarvisCyan.copy(alpha = 0.15f), CircleShape)
            )

            // Ring 2
            Box(
                modifier = Modifier
                    .size(175.dp)
                    .scale(if (isListening) pulseScale1 * reactiveAmpScale else 1f)
                    .border(1.dp, JarvisCyan.copy(alpha = 0.3f), CircleShape)
            )

            // Ring 1 (Inner glow)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(JarvisCyanGlow.copy(alpha = if (isListening) 0.3f else 0.08f), CircleShape)
                    .border(1.dp, JarvisCyan.copy(alpha = 0.5f), CircleShape)
            )

            // Core Mic Button
            val coreBrush = if (isListening) {
                Brush.linearGradient(listOf(JarvisCyan, JarvisCyanLight))
            } else {
                Brush.linearGradient(listOf(JarvisSurfaceContainerHigh, JarvisSurfaceContainer))
            }

            Box(
                modifier = Modifier
                    .size(108.dp)
                    .scale(reactiveAmpScale)
                    .clip(CircleShape)
                    .background(coreBrush)
                    .border(2.dp, JarvisCyan, CircleShape)
                    .clickable(onClick = onToggleListen)
                    .testTag("mic_visualizer_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice input trigger",
                        tint = if (isListening) Color(0xFF00373A) else JarvisCyan,
                        modifier = Modifier.size(38.dp)
                    )
                    Text(
                        text = when (voiceState) {
                            VoiceState.LISTENING -> "ACTIVE"
                            VoiceState.PROCESSING -> "THINKING"
                            VoiceState.SPEAKING -> "SPEAKING"
                            VoiceState.IDLE -> "TAP TO MIC"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isListening) Color(0xFF00373A) else JarvisCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Status Indicator pill
        val statusText = when (voiceState) {
            VoiceState.LISTENING -> "Listening for command..."
            VoiceState.PROCESSING -> "Neural processing active..."
            VoiceState.SPEAKING -> "Transmitting vocal response..."
            VoiceState.IDLE -> if (isListening) "Listening for trigger..." else "Voice input paused"
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(JarvisSurfaceContainerHigh.copy(alpha = 0.85f))
                .border(1.dp, JarvisCyan.copy(alpha = 0.25f), RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isListening) JarvisCyan else Color.Gray)
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isListening) JarvisCyan else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
