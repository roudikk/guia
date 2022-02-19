package com.roudikk.navigator.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.roudikk.navigator.core.NavigationNode.Companion.key
import com.roudikk.navigator.sample.ui.screens.home.HomeScreen
import com.roudikk.navigator.sample.ui.screens.welcome.WelcomeScreen
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
        rule.onNodeWithTag(key<WelcomeScreen>()).assertIsDisplayed()

        val button = rule.onNodeWithText("Navigate Home")
        button.assertIsDisplayed()
        button.performClick()

        rule.mainClock.autoAdvance = true
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
        rule.activity.onBackPressed()

        rule.mainClock.autoAdvance = false
        rule.mainClock.advanceTimeBy(100)
        rule.onNodeWithTag(key<WelcomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_setRootHome_cantGoBack() {
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag(key<WelcomeScreen>()).assertIsDisplayed()

        val button = rule.onNodeWithText("Set Root Home")
        button.assertIsDisplayed()
        button.performClick()

        rule.mainClock.autoAdvance = true
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()

        try {
            rule.activity.onBackPressed()
            assert(false)
        } catch (exception: Exception) {
            assert(true)
        }
    }
}
