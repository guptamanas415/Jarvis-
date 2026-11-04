package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBorder
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceContainer
import com.example.ui.theme.JarvisSurfaceContainerHigh
import com.example.ui.theme.JarvisSurfaceContainerHighest
import com.example.ui.theme.JarvisSurfaceContainerLow

@Composable
fun HomeworkScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var selectedSubject by remember { mutableStateOf("math") } // "math" or "art"
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var followUpText by remember { mutableStateOf("") }

    val homeworkResult by viewModel.homeworkResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingHomework.collectAsState()

    // Activity result launchers for capture & browse
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedBitmap = loadBitmap(context, uri)
            viewModel.analyzeHomeworkProblem(
                imageBitmap = selectedBitmap,
                problemText = if (selectedSubject == "math") "Evaluate this definite integral equation." else "Provide creative design guidance on this art assignment.",
                subject = selectedSubject.uppercase()
            )
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            selectedImageUri = Uri.parse("content://homework_snap_${System.currentTimeMillis()}")
            viewModel.analyzeHomeworkProblem(
                imageBitmap = bitmap,
                problemText = if (selectedSubject == "math") "Evaluate this captured math problem." else "Analyze this captured artwork.",
                subject = selectedSubject.uppercase()
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisSurface)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Status & Subject Tabs
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACADEMIC // MODULE 04",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(JarvisSurfaceContainerHigh)
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
                            text = "AI TUTOR ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = JarvisCyan
                        )
                    }
                }

                // Subject Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(JarvisSurfaceContainerLow)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isMath = selectedSubject == "math"
                    val isArt = selectedSubject == "art"

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isMath) JarvisCyan else Color.Transparent)
                            .clickable { selectedSubject = "math" }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Functions,
                            contentDescription = "Math Matrix",
                            tint = if (isMath) Color(0xFF00373A) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Math Matrix",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isMath) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isMath) Color(0xFF00373A) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isArt) JarvisCyan else Color.Transparent)
                            .clickable { selectedSubject = "art" }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Art & Design",
                            tint = if (isArt) Color(0xFF00373A) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Art & Design",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isArt) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isArt) Color(0xFF00373A) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Intake Port Dropzone Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(JarvisSurfaceContainer)
                    .border(1.dp, JarvisCyanBorder.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(JarvisSurfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = JarvisCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Drop assignment file here or capture",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "JARVIS will instantly parse variables, diagrams, and constraints",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(JarvisSurfaceContainerHighest)
                            .clickable { cameraLauncher.launch(null) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture",
                            tint = JarvisCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CAPTURE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(JarvisCyan)
                            .clickable { galleryLauncher.launch("image/*") }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Browse",
                            tint = Color(0xFF00373A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BROWSE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF00373A)
                        )
                    }
                }
            }
        }

        // Analyzing indicator
        if (isAnalyzing) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(JarvisSurfaceContainerLow)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = JarvisCyan,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Deconstructing assignment telemetry with Gemini AI...",
                        style = MaterialTheme.typography.bodySmall,
                        color = JarvisCyan
                    )
                }
            }
        }

        // Telemetry & Analysis Section
        item {
            val res = homeworkResult
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TELEMETRY & ANALYSIS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "STATUS: PARSED",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = JarvisCyan
                    )
                }

                // Thumbnail Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(JarvisSurfaceContainer)
                        .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(JarvisSurfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Homework Thumbnail",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = if (selectedSubject == "math") Icons.Default.Functions else Icons.Default.Palette,
                                contentDescription = "Assignment",
                                tint = JarvisCyan,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedSubject == "math") "Calculus_Problem_04.jpg" else "Art_Composition_v2.png",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = JarvisCyan
                            )
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = JarvisCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = if (selectedSubject == "math") "Integral calculus & trigonometric substitution" else "Visual concept & color harmony guidance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Confidence: 99.8%",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(JarvisSurfaceContainerHigh, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Text(
                                text = "Difficulty: Advanced",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = JarvisCyan,
                                modifier = Modifier
                                    .background(JarvisSurfaceContainerHigh, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // AI Question Architecture Breakdown Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(JarvisSurfaceContainer)
                        .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Neural Architecture",
                            tint = JarvisCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "AI Question Architecture Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = res?.summary ?: "Detected parameters requiring analytical resolution. The core objective is isolated and ready for step-by-step computation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ArchitectureParamBox(
                            modifier = Modifier.weight(1f),
                            label = if (selectedSubject == "math") "Domain" else "Concept",
                            value = res?.domain ?: if (selectedSubject == "math") "x > 3" else "Modern HUD"
                        )
                        ArchitectureParamBox(
                            modifier = Modifier.weight(1f),
                            label = if (selectedSubject == "math") "Substitution" else "Composition",
                            value = res?.substitution ?: if (selectedSubject == "math") "x = 3sec(θ)" else "Golden Ratio"
                        )
                        ArchitectureParamBox(
                            modifier = Modifier.weight(1f),
                            label = if (selectedSubject == "math") "Target Identity" else "Palette",
                            value = res?.target ?: if (selectedSubject == "math") "tan²(θ)" else "Obsidian/Cyan"
                        )
                    }
                }
            }
        }

        // Step-by-Step Solutions Cards
        item {
            val res = homeworkResult
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STEP-BY-STEP SOLUTIONS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "3 STEPS COMPUTED",
                        style = MaterialTheme.typography.bodySmall,
                        color = JarvisCyan
                    )
                }

                SolutionStepCard(
                    stepNum = "1",
                    stepCode = "SUB-01",
                    title = if (selectedSubject == "math") "Apply Substitution & Formulate" else "Define Silhouette & Foundation",
                    description = res?.step1 ?: "Let x = 3sec(θ). Consequently, dx = 3sec(θ)tan(θ)dθ. Substitute into the denominator radical expression.",
                    codeBlock = if (selectedSubject == "math") "√(x² - 9) = √(9sec²(θ) - 9) = 3tan(θ)" else "Canvas: 16:9 Landscape // Dynamic perspective grid lines"
                )

                SolutionStepCard(
                    stepNum = "2",
                    stepCode = "INT-02",
                    title = if (selectedSubject == "math") "Simplify & Integrate" else "Apply Values, Shading & Lighting",
                    description = res?.step2 ?: "Substitute back into the integrand. The terms cancel cleanly, leaving an elementary trigonometric integral.",
                    codeBlock = if (selectedSubject == "math") "∫ (3sec(θ)tan(θ)dθ) / (9sec²(θ) · 3tan(θ)) = (1/9) ∫ cos(θ)dθ" else "Primary directional light from top right with deep ambient occlusion"
                )

                SolutionStepCard(
                    stepNum = "3",
                    stepCode = "RES-03",
                    title = if (selectedSubject == "math") "Evaluate Bounds & Final Result" else "Highlight Detailing & Polishing",
                    description = res?.step3 ?: "Integrate to obtain (1/9)sin(θ), revert back to Cartesian variable x, and evaluate over interval [3, 6].",
                    codeBlock = if (selectedSubject == "math") "Result: (√3) / 18  ≈  0.0962" else "Bloom pass at 40% opacity // Clean vector silhouettes"
                )
            }
        }

        // Consult JARVIS section
        item {
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
                    Text(
                        text = "Consult JARVIS on this problem",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Context loaded",
                            tint = JarvisCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Context Loaded",
                            style = MaterialTheme.typography.bodySmall,
                            color = JarvisCyan
                        )
                    }
                }

                OutlinedTextField(
                    value = followUpText,
                    onValueChange = { followUpText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("homework_followup_input"),
                    placeholder = {
                        Text(
                            "Ask a follow-up question (e.g., explain step 2)...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (followUpText.isNotBlank()) {
                                viewModel.handleUserInput("Concerning the homework problem: $followUpText")
                                followUpText = ""
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send follow-up",
                                tint = JarvisCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = JarvisSurfaceContainerLow,
                        unfocusedContainerColor = JarvisSurfaceContainerLow,
                        focusedBorderColor = JarvisCyanBorder,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ArchitectureParamBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(JarvisSurfaceContainerLow)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = JarvisCyan,
            maxLines = 1
        )
    }
}

@Composable
fun SolutionStepCard(
    stepNum: String,
    stepCode: String,
    title: String,
    description: String,
    codeBlock: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(JarvisSurfaceContainer)
            .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
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
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(JarvisCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNum,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF00373A)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stepCode,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = JarvisCyan
            )
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(JarvisSurfaceContainerLow)
                .padding(10.dp)
        ) {
            Text(
                text = codeBlock,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                color = JarvisCyan
            )
        }
    }
}

private fun loadBitmap(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (_: Exception) {
        null
    }
}
