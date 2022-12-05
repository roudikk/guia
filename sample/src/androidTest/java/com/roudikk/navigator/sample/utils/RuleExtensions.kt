package com.roudikk.navigator.sample.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.roudikk.navigator.core.NavigationKey.Companion.tag
import com.roudikk.navigator.sample.MainActivity
import com.roudikk.navigator.sample.ui.screens.details.DetailsKey
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsKey
import com.roudikk.navigator.sample.ui.screens.home.HomeKey
import com.roudikk.navigator.sample.ui.screens.nested.ParentNestedKey
import com.roudikk.navigator.sample.ui.screens.welcome.WelcomeKey

// rule.mainClock.autoAdvance is used in these tests because Lottie prevents the rule from
// becoming idle since it's an infinite animation
fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateHome() {
    mainClock.autoAdvance = false
    onNodeWithTag(tag<WelcomeKey>()).assertIsDisplayed()

    val button = onNodeWithText("Navigate Home")
    button.assertIsDisplayed()
    button.performClick()
    mainClock.autoAdvance = true
    onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateDetails() {
    navigateHome()
    onNodeWithContentDescription("Add Item").performClick()
    onNode(hasText("Item: ", substring = true)).performClick()
    onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateNestedTab() {
    navigateHome()
    onNodeWithTag("tab_nested").performClick()
    onNodeWithTag(tag<ParentNestedKey>()).assertIsDisplayed()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateDialogsTab() {
    navigateHome()
    onNodeWithTag("tab_dialogs").performClick()
    onNodeWithTag(tag<DialogsKey>()).assertIsDisplayed()
}
