package com.roudikk.navigator.sample.utils

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.roudikk.navigator.NavigationNode
import com.roudikk.navigator.sample.MainActivity
import com.roudikk.navigator.sample.ui.screens.details.DetailsScreen
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsScreen
import com.roudikk.navigator.sample.ui.screens.home.HomeScreen
import com.roudikk.navigator.sample.ui.screens.nested.ParentNestedScreen
import com.roudikk.navigator.sample.ui.screens.welcome.WelcomeScreen

// rule.mainClock.autoAdvance is used in these tests because Lottie prevents the rule from
// becoming idle since it's an infinite animation
fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateHome() {
    mainClock.autoAdvance = false
    onNodeWithTag(NavigationNode.key<WelcomeScreen>()).assertIsDisplayed()

    val button = onNodeWithText("Navigate Home")
    button.assertIsDisplayed()
    button.performClick()
    mainClock.autoAdvance = true
    onNodeWithTag(NavigationNode.key<HomeScreen>()).assertIsDisplayed()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateDetails() {
    navigateHome()
    onNodeWithContentDescription("Add Item").performClick()
    onNode(hasText("Item: ", substring = true)).performClick()
    onNodeWithTag(NavigationNode.key<DetailsScreen>()).assertIsDisplayed()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateNestedTab() {
    navigateHome()
    onNodeWithTag("tab_nested").performClick()
    onNodeWithTag(NavigationNode.key<ParentNestedScreen>()).assertIsDisplayed()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateDialogsTab() {
    navigateHome()
    onNodeWithTag("tab_dialogs").performClick()
    onNodeWithTag(NavigationNode.key<DialogsScreen>()).assertIsDisplayed()
}
