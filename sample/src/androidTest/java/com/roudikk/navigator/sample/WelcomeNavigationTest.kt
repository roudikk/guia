package com.roudikk.navigator.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.roudikk.navigator.NavigationKey.Companion.tag
import com.roudikk.navigator.sample.ui.screens.home.HomeKey
import com.roudikk.navigator.sample.ui.screens.welcome.WelcomeKey
import org.junit.Rule
import org.junit.Test

// rule.mainClock.autoAdvance is used in these tests because Lottie prevents the rule from
// becoming idle since it's an infinite animation
class WelcomeNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun welcomeScreen_navigateHome_canGoBack() {
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag(tag<WelcomeKey>()).assertIsDisplayed()

        val button = rule.onNodeWithText("Navigate Home")
        button.assertIsDisplayed()
        button.performClick()

        rule.mainClock.autoAdvance = true
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()

        rule.mainClock.autoAdvance = false
        rule.mainClock.advanceTimeBy(100)
        rule.onNodeWithTag(tag<WelcomeKey>()).assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_setRootHome_cantGoBack() {
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag(tag<WelcomeKey>()).assertIsDisplayed()

        val button = rule.onNodeWithText("Set Root Home")
        button.assertIsDisplayed()
        button.performClick()

        rule.mainClock.autoAdvance = true
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()

        try {
            rule.activity.onBackPressedDispatcher.onBackPressed()
            assert(false)
        } catch (_: Exception) {
            assert(true)
        }
    }
}
