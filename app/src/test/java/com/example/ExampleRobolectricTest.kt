package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.IntentClassifier
import com.example.ai.JarvisIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("JARVIS", appName)
    }

    @Test
    fun `test intent classification`() {
        val classifier = IntentClassifier()
        val intent = classifier.classify("Open YouTube")
        assertTrue(intent is JarvisIntent.OpenApp)
        assertEquals("YouTube", (intent as JarvisIntent.OpenApp).appName)

        val shopping = classifier.classify("Search for wireless earbuds on Amazon")
        assertTrue(shopping is JarvisIntent.SearchShopping)
    }
}
