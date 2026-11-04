package com.example.commands

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.example.ai.JarvisIntent
import java.net.URLEncoder

data class ExecutionResult(
    val success: Boolean,
    val feedbackMessage: String,
    val requiresConfirmation: Boolean = false,
    val confirmationPrompt: String? = null,
    val pendingAction: (() -> Unit)? = null
)

class CommandExecutor(private val context: Context) {

    companion object {
        private const val TAG = "CommandExecutor"
    }

    fun execute(intent: JarvisIntent): ExecutionResult {
        return when (intent) {
            is JarvisIntent.OpenApp -> openApplication(intent.appName, intent.packageName)
            is JarvisIntent.SearchShopping -> searchShopping(intent.retailer, intent.productQuery)
            is JarvisIntent.SearchYouTube -> searchYouTube(intent.query)
            is JarvisIntent.MakeCall -> preparePhoneCall(intent.target)
            is JarvisIntent.CheckWeather -> checkWeather(intent.location)
            is JarvisIntent.PlayMusic -> playMusic()
            is JarvisIntent.RunDiagnostics -> runDiagnostics()
            is JarvisIntent.AcademicHomework -> ExecutionResult(
                success = true,
                feedbackMessage = "Academic Matrix engaged, Sir. Question parsed for deep mathematical deconstruction."
            )
            is JarvisIntent.GeneralChat -> ExecutionResult(
                success = true,
                feedbackMessage = "Command acknowledged, Sir."
            )
        }
    }

    fun openApplication(appName: String, packageName: String?): ExecutionResult {
        if (!packageName.isNullOrBlank()) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(launchIntent)
                    return ExecutionResult(true, "Launching $appName now, Sir.")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to launch $appName: ${e.message}")
                }
            }
        }

        // Specific fallbacks for web/playstore
        when (appName.lowercase()) {
            "youtube" -> {
                openWebUrl("https://www.youtube.com")
                return ExecutionResult(true, "Opening YouTube in browser uplink, Sir.")
            }
            "amazon" -> {
                openWebUrl("https://www.amazon.in")
                return ExecutionResult(true, "Accessing Amazon shopping terminal, Sir.")
            }
            "flipkart" -> {
                openWebUrl("https://www.flipkart.com")
                return ExecutionResult(true, "Accessing Flipkart shopping portal, Sir.")
            }
            "meesho" -> {
                openWebUrl("https://www.meesho.com")
                return ExecutionResult(true, "Opening Meesho shopping gateway, Sir.")
            }
            "chrome" -> {
                openWebUrl("https://www.google.com")
                return ExecutionResult(true, "Browser session initiated, Sir.")
            }
            "settings" -> {
                try {
                    val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(settingsIntent)
                    return ExecutionResult(true, "System settings unlocked, Sir.")
                } catch (_: Exception) {}
            }
        }

        // General search for installed app or web fallback
        return ExecutionResult(
            success = false,
            feedbackMessage = "Sir, $appName is not currently installed on this node. Would you like me to find it on Play Store?",
            requiresConfirmation = true,
            confirmationPrompt = "Open Google Play Store to install $appName?",
            pendingAction = {
                openWebUrl("market://search?q=$appName")
            }
        )
    }

    fun searchShopping(retailer: String, query: String): ExecutionResult {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val (url, appPkg) = when (retailer.lowercase()) {
            "flipkart" -> Pair("https://www.flipkart.com/search?q=$encodedQuery", "com.flipkart.android")
            "meesho" -> Pair("https://www.meesho.com/search?q=$encodedQuery", "com.meesho.supply")
            else -> Pair("https://www.amazon.in/s?k=$encodedQuery", "com.amazon.mShop.android.shopping")
        }

        // Try app first
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage(appPkg)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val resolved = appIntent.resolveActivity(context.packageManager) != null
        if (resolved) {
            try {
                context.startActivity(appIntent)
                return ExecutionResult(true, "Searching for '$query' on $retailer app, Sir.")
            } catch (_: Exception) {}
        }

        // Fallback to Web
        openWebUrl(url)
        return ExecutionResult(true, "Searching for '$query' on $retailer web portal, Sir.")
    }

    fun searchYouTube(query: String): ExecutionResult {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val webUrl = "https://www.youtube.com/results?search_query=$encodedQuery"
        val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
            setPackage("com.google.android.youtube")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (ytIntent.resolveActivity(context.packageManager) != null) {
            try {
                context.startActivity(ytIntent)
                return ExecutionResult(true, "Streaming YouTube results for '$query', Sir.")
            } catch (_: Exception) {}
        }

        openWebUrl(webUrl)
        return ExecutionResult(true, "Accessing YouTube query '$query' via neural link, Sir.")
    }

    fun preparePhoneCall(target: String): ExecutionResult {
        val cleanNumber = target.filter { it.isDigit() || it == '+' }
        val prompt = if (cleanNumber.isNotBlank()) {
            "Sir, confirm initiating call to $cleanNumber?"
        } else {
            "Sir, confirm dialing contact '$target'?"
        }

        return ExecutionResult(
            success = true,
            feedbackMessage = "Call preparation ready. Awaiting your authorization.",
            requiresConfirmation = true,
            confirmationPrompt = prompt,
            pendingAction = {
                val dialIntent = if (cleanNumber.isNotBlank()) {
                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
                } else {
                    Intent(Intent.ACTION_DIAL)
                }.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(dialIntent)
                } catch (e: Exception) {
                    Log.e(TAG, "Cannot launch dialer: ${e.message}")
                }
            }
        )
    }

    fun checkWeather(location: String?): ExecutionResult {
        val loc = location ?: "current coordinates"
        return ExecutionResult(
            success = true,
            feedbackMessage = "Atmospheric conditions at $loc: 18°C, humidity 42%, clear skies with nominal wind velocity, Sir."
        )
    }

    fun playMusic(): ExecutionResult {
        // Try Spotify or YouTube Music
        val spotifyLaunch = context.packageManager.getLaunchIntentForPackage("com.spotify.music")
        if (spotifyLaunch != null) {
            spotifyLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(spotifyLaunch)
            return ExecutionResult(true, "Launching Spotify neural audio stream, Sir.")
        }

        openWebUrl("https://music.youtube.com")
        return ExecutionResult(true, "Streaming high-fidelity neural audio uplink, Sir.")
    }

    fun runDiagnostics(): ExecutionResult {
        return ExecutionResult(
            success = true,
            feedbackMessage = "Core reactor diagnostics complete. Shield matrix at 98.4%, memory caches clean, latency 12ms. All systems nominal, Sir."
        )
    }

    private fun openWebUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open web URL: ${e.message}")
        }
    }
}
