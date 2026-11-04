# JARVIS — Futuristic Personal AI Voice Assistant

JARVIS is a native Android AI voice and automation assistant built with Kotlin and Jetpack Compose. Styled directly from the Google Stitch design system, JARVIS couples a dark obsidian HUD aesthetic with Google Gemini for intelligent conversations, multimodal image breakdown, homework deconstruction, app orchestration, and hands-free voice assistance.

---

## 🚀 Features

### 1. Home — Voice Assistant Hub
- **Animated Central Voice Visualizer**: Multi-ring concentric pulsating circles that dynamically react to speech audio RMS amplitude.
- **Vocal State Machine**: Real-time status indicators (`Listening`, `Processing`, `Speaking`, `Idle`).
- **Telemetry Clock**: Live seconds-accurate header clock with neural link status badge.
- **Quick Commands Matrix**: Direct shortcuts for Voice Search, Audio Playback, Atmospheric Weather, and Core Diagnostics.
- **Gemini Neural Status**: Real-time latency measurement and uptime status.
- **Recent Activity Feed**: Historical command prompts and AI summaries with quick clear controls.

### 2. Chat — Separate AI Conversation
- **Multimodal AI**: Upload images, blueprints, schematics, and photos from Gallery or Camera.
- **Telemetry Cards**: Anomaly detection cards, stress concentration vectors, and structured analysis.
- **Voice Dictation**: Built-in microphone input and text-to-speech replay on every message.
- **Full History**: Stored securely on-device via Room SQLite database.

### 3. Apps — Connected Applications & Shopping
- **App Orchestration**: Fast voice-command launching for YouTube, Amazon, Flipkart, Meesho, Spotify, Phone Dialer, and System Settings.
- **AI Shopping Assistant**: Multi-retailer tab filtering (Amazon, Flipkart, Meesho) with voice search triggers that safely open the requested queries (never auto-charging without confirmation).
- **YouTube Assistant**: Handles recipe queries, programming tutorials, and educational videos.

### 4. Homework Assistant — Academic Module
- **Dual Matrix**: Dedicated **Math Matrix** (integral calculus, algebra, derivatives) and **Art & Design** (composition, color harmony, perspective).
- **Homework Intake Port**: Dropzone with Camera Capture and Gallery Browse.
- **Question Architecture Breakdown**: Automatically isolates Domain, Substitution method, and Target Identity.
- **3-Step Explanations**: Step-by-step mathematical reasoning and final numerical approximations.
- **Consult JARVIS**: Context-aware follow-up queries on the same assignment.

### 5. Settings & Customization
- **Language Dialect**: English, Hindi, and Hinglish.
- **Voice Core**: 4 acoustic profiles (Mark I, Prime, Nova, Echo) with pitch and speech rate calibration.
- **Personality Matrix**: Real-time sliders for Sarcasm Index, Professional Rigor, Helpfulness Factor, and Conciseness.
- **Wake Word**: "Hey JARVIS" foreground acoustic trigger with sensitivity slider.
- **HUD Theme**: Dark Neon (Cyan/Blue), Deep Space (Indigo/Slate), and Matrix (Emerald/Black).
- **Privacy & Enclave**: Permission status readouts and cache flushing.

---

## 🔑 Gemini API Key Configuration

To connect Google Gemini:
1. **AI Studio Secrets (Recommended)**: In AI Studio, open the **Secrets** panel and configure `GEMINI_API_KEY`. It is automatically injected at build time into `BuildConfig.GEMINI_API_KEY`.
2. **In-App Settings**: Navigate to **Settings -> Gemini API Kernel -> Configure Gemini API Key** and enter your key directly.
3. **Local Development (.env)**:
   ```bash
   GEMINI_API_KEY=YOUR_GEMINI_API_KEY_HERE
   ```

*Security*: The app masks the key in all UI logs and never commits sensitive secrets to source control.

---

## 🛠️ Building the Application

### Method 1: Local Android Studio
1. Open the project folder in **Android Studio Ladybug or newer**.
2. Sync Gradle dependencies.
3. Run on a connected Android device or emulator with API Level 24+ (Android 7.0 to 14+).

### Method 2: Command Line (Gradle)
```bash
# Build Debug APK
gradle :app:assembleDebug

# The APK will be generated at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Method 3: GitHub Actions CI/CD
A GitHub Actions workflow is provided at `.github/workflows/android-build.yml`.
When you push this repository to GitHub:
1. Navigate to the **Actions** tab in your repository.
2. The workflow will automatically set up JDK 17, Android SDK, and compile the APK.
3. Download the generated APK artifact named `jarvis-ai-assistant-debug-apk` directly from the workflow run.
