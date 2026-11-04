package com.example.ai

sealed class JarvisIntent {
    data class OpenApp(val appName: String, val packageName: String?) : JarvisIntent()
    data class SearchShopping(val retailer: String, val productQuery: String) : JarvisIntent()
    data class SearchYouTube(val query: String) : JarvisIntent()
    data class MakeCall(val target: String) : JarvisIntent()
    data class CheckWeather(val location: String? = null) : JarvisIntent()
    data object PlayMusic : JarvisIntent()
    data object RunDiagnostics : JarvisIntent()
    data class AcademicHomework(val query: String) : JarvisIntent()
    data class GeneralChat(val query: String) : JarvisIntent()
}

class IntentClassifier {

    fun classify(input: String): JarvisIntent {
        val trimmed = input.trim()
        val lower = trimmed.lowercase()

        // Shopping commands
        if (containsAny(lower, "amazon", "flipkart", "meesho") &&
            containsAny(lower, "search", "find", "buy", "price", "order", "dhundo", "kharido")
        ) {
            val retailer = when {
                "flipkart" in lower -> "flipkart"
                "meesho" in lower -> "meesho"
                else -> "amazon"
            }
            val product = extractProductQuery(lower, retailer)
            return JarvisIntent.SearchShopping(retailer, product)
        }

        // App launches
        if (lower.startsWith("open ") || lower.startsWith("launch ") || lower.startsWith("kholo ") || lower.startsWith("start ")) {
            val appQuery = lower.removePrefix("open ")
                .removePrefix("launch ")
                .removePrefix("kholo ")
                .removePrefix("start ")
                .trim()

            return resolveAppIntent(appQuery)
        }

        // YouTube search / playback
        if ("youtube" in lower && containsAny(lower, "search", "play", "video", "dikhao", "chalao")) {
            val ytQuery = lower.replace("search", "")
                .replace("on youtube", "")
                .replace("in youtube", "")
                .replace("youtube", "")
                .replace("play", "")
                .replace("video", "")
                .trim()
            return JarvisIntent.SearchYouTube(ytQuery.ifBlank { "trending technology" })
        }

        // Call commands
        if (lower.startsWith("call ") || lower.startsWith("dial ") || "call lagao" in lower || "call karo" in lower) {
            val target = lower.removePrefix("call ")
                .removePrefix("dial ")
                .replace("ko call lagao", "")
                .replace("ko call karo", "")
                .trim()
            return JarvisIntent.MakeCall(target)
        }

        // Diagnostics
        if (containsAny(lower, "diagnostic", "diagnostics", "system status", "reactor", "shield")) {
            return JarvisIntent.RunDiagnostics
        }

        // Weather
        if (containsAny(lower, "weather", "temperature", "mausam")) {
            return JarvisIntent.CheckWeather()
        }

        // Music
        if (containsAny(lower, "play music", "music chalao", "gaana", "songs")) {
            return JarvisIntent.PlayMusic
        }

        // Homework
        if (containsAny(lower, "solve", "math", "calculus", "homework", "equation", "integral", "derivative")) {
            return JarvisIntent.AcademicHomework(trimmed)
        }

        // Default to conversational AI
        return JarvisIntent.GeneralChat(trimmed)
    }

    private fun resolveAppIntent(appQuery: String): JarvisIntent {
        return when {
            "youtube" in appQuery -> JarvisIntent.OpenApp("YouTube", "com.google.android.youtube")
            "whatsapp" in appQuery -> JarvisIntent.OpenApp("WhatsApp", "com.whatsapp")
            "instagram" in appQuery || "insta" in appQuery -> JarvisIntent.OpenApp("Instagram", "com.instagram.android")
            "chrome" in appQuery || "browser" in appQuery -> JarvisIntent.OpenApp("Chrome", "com.android.chrome")
            "amazon" in appQuery -> JarvisIntent.OpenApp("Amazon", "com.amazon.mShop.android.shopping")
            "flipkart" in appQuery -> JarvisIntent.OpenApp("Flipkart", "com.flipkart.android")
            "meesho" in appQuery -> JarvisIntent.OpenApp("Meesho", "com.meesho.supply")
            "spotify" in appQuery || "music" in appQuery -> JarvisIntent.OpenApp("Spotify", "com.spotify.music")
            "settings" in appQuery -> JarvisIntent.OpenApp("Settings", "com.android.settings")
            "play store" in appQuery || "store" in appQuery -> JarvisIntent.OpenApp("Play Store", "com.android.vending")
            else -> JarvisIntent.OpenApp(appQuery.replaceFirstChar { it.uppercase() }, null)
        }
    }

    private fun extractProductQuery(input: String, retailer: String): String {
        var clean = input.replace(retailer, "")
            .replace("search for", "")
            .replace("search", "")
            .replace("find a", "")
            .replace("find", "")
            .replace("on", "")
            .replace("in", "")
            .replace("me", "")
            .replace("par", "")
            .replace("dhundo", "")
            .trim()
        if (clean.isBlank()) clean = "electronics"
        return clean
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { it in text }
    }
}
