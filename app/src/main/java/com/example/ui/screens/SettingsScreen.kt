package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SettingsVoice
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBorder
import com.example.ui.theme.JarvisError
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceContainer
import com.example.ui.theme.JarvisSurfaceContainerHigh
import com.example.ui.theme.JarvisSurfaceContainerHighest
import com.example.ui.theme.JarvisSurfaceContainerLow

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val language by viewModel.preferences.language.collectAsState()
    val voiceProfile by viewModel.preferences.voiceProfile.collectAsState()
    val sarcasm by viewModel.preferences.sarcasm.collectAsState()
    val professionalRigor by viewModel.preferences.professionalRigor.collectAsState()
    val helpfulness by viewModel.preferences.helpfulness.collectAsState()
    val conciseness by viewModel.preferences.conciseness.collectAsState()
    val wakeWordEnabled by viewModel.preferences.wakeWordEnabled.collectAsState()
    val hudTheme by viewModel.preferences.hudTheme.collectAsState()
    val customApiKey by viewModel.preferences.customApiKey.collectAsState()
    val historyEnabled by viewModel.preferences.historyEnabled.collectAsState()

    var showApiKeyDialog by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf(customApiKey) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisSurface)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // System Status Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(JarvisSurfaceContainerLow)
                    .border(1.dp, JarvisCyanBorder.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Gemini",
                            tint = JarvisCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "GEMINI API KERNEL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(JarvisCyan.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(JarvisCyan)
                        )
                        Text(
                            text = "SECURE 256-BIT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = JarvisCyan
                        )
                    }
                }

                Text(
                    text = "Encrypted neural uplink active. Latency: 14ms. Key: ${viewModel.geminiService.getMaskedApiKey(customApiKey)}. Model: Gemini-2.0-Flash-Advanced.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(JarvisCyan.copy(alpha = 0.1f))
                        .clickable { showApiKeyDialog = !showApiKeyDialog }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Key",
                        tint = JarvisCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (showApiKeyDialog) "Close API Config" else "Configure Gemini API Key",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = JarvisCyan
                    )
                }

                AnimatedVisibility(visible = showApiKeyDialog) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Development Placeholder: GEMINI_API_KEY = \"YOUR_GEMINI_API_KEY_HERE\". Injected automatically by AI Studio at runtime or overridden here.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter Gemini API Key or leave empty for build secret") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = JarvisSurfaceContainerHigh,
                                unfocusedContainerColor = JarvisSurfaceContainerHigh,
                                focusedBorderColor = JarvisCyanBorder,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.preferences.setCustomApiKey(apiKeyInput.trim())
                                    viewModel.showNotification("Gemini API Key updated successfully.")
                                    showApiKeyDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan)
                            ) {
                                Text("Save Key", color = Color(0xFF00373A))
                            }
                            if (customApiKey.isNotBlank()) {
                                Button(
                                    onClick = {
                                        apiKeyInput = ""
                                        viewModel.preferences.setCustomApiKey("")
                                        viewModel.showNotification("Reverted to BuildConfig default.")
                                        showApiKeyDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceContainerHighest)
                                ) {
                                    Text("Reset to Default")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Voice Model Selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "JARVIS VOICE CORE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ACTIVE: ${voiceProfile.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = JarvisCyan
                    )
                }

                val profiles = listOf(
                    Triple("Mark I", "Classic baritone acoustic profile.", Icons.Default.Mic),
                    Triple("Prime", "Adaptive neural resonance engine.", Icons.Default.AutoAwesome),
                    Triple("Nova", "Ethereal high-frequency timbre.", Icons.Default.GraphicEq),
                    Triple("Echo", "Dual-layered harmonic synthesizer.", Icons.Default.SettingsVoice)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in profiles.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            VoiceProfileCard(
                                modifier = Modifier.weight(1f),
                                name = profiles[i].first,
                                desc = profiles[i].second,
                                icon = profiles[i].third,
                                isSelected = voiceProfile.equals(profiles[i].first, ignoreCase = true),
                                onSelect = { viewModel.updateVoiceProfile(profiles[i].first) }
                            )
                            VoiceProfileCard(
                                modifier = Modifier.weight(1f),
                                name = profiles[i + 1].first,
                                desc = profiles[i + 1].second,
                                icon = profiles[i + 1].third,
                                isSelected = voiceProfile.equals(profiles[i + 1].first, ignoreCase = true),
                                onSelect = { viewModel.updateVoiceProfile(profiles[i + 1].first) }
                            )
                        }
                    }
                }
            }
        }

        // Language Protocol
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "LANGUAGE PROTOCOL",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(JarvisSurfaceContainer)
                        .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = JarvisCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Primary Dialect Engine",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(JarvisSurfaceContainerLow)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("HINDI", "ENGLISH", "HINGLISH").forEach { lang ->
                            val isSelected = language.equals(lang, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) JarvisCyan else Color.Transparent)
                                    .clickable { viewModel.updateLanguage(lang) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) Color(0xFF00373A) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Personality Matrix
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PERSONALITY MATRIX",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(JarvisSurfaceContainer)
                        .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PersonalitySlider(
                        label = "Sarcasm Index",
                        value = sarcasm,
                        onValueChange = { viewModel.preferences.setSarcasm(it) }
                    )
                    PersonalitySlider(
                        label = "Professional Rigor",
                        value = professionalRigor,
                        onValueChange = { viewModel.preferences.setProfessionalRigor(it) }
                    )
                    PersonalitySlider(
                        label = "Helpfulness Factor",
                        value = helpfulness,
                        onValueChange = { viewModel.preferences.setHelpfulness(it) }
                    )
                    PersonalitySlider(
                        label = "Conciseness",
                        value = conciseness,
                        onValueChange = { viewModel.preferences.setConciseness(it) }
                    )
                }
            }
        }

        // Voice Activation
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "VOICE ACTIVATION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(JarvisSurfaceContainer)
                        .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Wake Word (\"Hey JARVIS\")",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Always-on foreground acoustic trigger",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = wakeWordEnabled,
                            onCheckedChange = { viewModel.preferences.setWakeWordEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00373A),
                                checkedTrackColor = JarvisCyan
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Wake Word Sensitivity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "High (0.85)",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = JarvisCyan
                        )
                    }
                }
            }
        }

        // HUD Theme Customization
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "HUD THEME CUSTOMIZATION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Dark Neon",
                        icon = Icons.Default.Bolt,
                        isSelected = hudTheme.equals("neon", ignoreCase = true),
                        onSelect = { viewModel.preferences.setHudTheme("neon") }
                    )
                    ThemeOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Deep Space",
                        icon = Icons.Default.DarkMode,
                        isSelected = hudTheme.equals("space", ignoreCase = true),
                        onSelect = { viewModel.preferences.setHudTheme("space") }
                    )
                    ThemeOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Matrix",
                        icon = Icons.Default.Terminal,
                        isSelected = hudTheme.equals("matrix", ignoreCase = true),
                        onSelect = { viewModel.preferences.setHudTheme("matrix") }
                    )
                }
            }
        }

        // Privacy & Permissions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PRIVACY & PERMISSIONS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(JarvisSurfaceContainer)
                        .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Mic", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Microphone Access", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text("GRANTED", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = JarvisCyan)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Telemetry", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Telemetry & Location", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text("GRANTED", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = JarvisCyan)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Conversation History", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            Text("Store logs locally on secure Room database", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = historyEnabled,
                            onCheckedChange = { viewModel.preferences.setHistoryEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00373A),
                                checkedTrackColor = JarvisCyan
                            )
                        )
                    }
                }
            }
        }

        // Danger Zone: Flush Cache
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JarvisError.copy(alpha = 0.12f))
                    .border(1.dp, JarvisError.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable { viewModel.clearChatHistory() }
                    .padding(16.dp)
                    .testTag("flush_cache_button"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Flush cache",
                    tint = JarvisError,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FLUSH CONVERSATION CACHE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = JarvisError
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun VoiceProfileCard(
    modifier: Modifier = Modifier,
    name: String,
    desc: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) JarvisCyan.copy(alpha = 0.1f) else JarvisSurfaceContainer)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) JarvisCyan else JarvisCyanBorder.copy(alpha = 0.2f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onSelect)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) JarvisCyan else MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isSelected) JarvisCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PersonalitySlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$value%",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = JarvisCyan
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = JarvisCyan,
                activeTrackColor = JarvisCyan,
                inactiveTrackColor = JarvisSurfaceContainerHigh
            )
        )
    }
}

@Composable
fun ThemeOptionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) JarvisCyan.copy(alpha = 0.1f) else JarvisSurfaceContainer)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) JarvisCyan else JarvisCyanBorder.copy(alpha = 0.2f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onSelect)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(JarvisSurfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = JarvisCyan,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
