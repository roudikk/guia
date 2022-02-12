package com.roudikk.navigator.sample

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.roudikk.navigator.sample.ui.screens.nested.NestedScreen
import com.roudikk.navigator.sample.utils.navigateNestedTab
import org.junit.Rule
import org.junit.Test

class NestedNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun nestedScreen_add() {
        rule.navigateNestedTab()
        rule.onNodeWithTag(NestedScreen.keyFor(1)).assertIsDisplayed()

        repeat(3) {
            rule.onNodeWithContentDescription("Add").performClick()
            rule.onNodeWithTag(NestedScreen.keyFor(it + 2)).assertIsDisplayed()
        }
    }

    @Test
    fun nestedScreen_remove() {
        rule.navigateNestedTab()
        rule.onNodeWithTag(NestedScreen.keyFor(1)).assertIsDisplayed()

        repeat(3) {
            rule.onNodeWithContentDescription("Add").performClick()
            rule.onNodeWithTag(NestedScreen.keyFor(it + 2)).assertIsDisplayed()
        }

        repeat(3) {
            rule.onNodeWithContentDescription("Remove").performClick()
            rule.onNodeWithTag(NestedScreen.keyFor(it + 2)).assertDoesNotExist()
        }

        rule.onNodeWithTag(NestedScreen.keyFor(1)).assertIsDisplayed()
    }

    @Test
    fun nestedScreen_popToRoot() {
        rule.navigateNestedTab()
        rule.onNodeWithTag(NestedScreen.keyFor(1)).assertIsDisplayed()

        repeat(3) {
            rule.onNodeWithContentDescription("Add").performClick()
            rule.onNodeWithTag(NestedScreen.keyFor(it + 2)).assertIsDisplayed()
        }

        rule.onNodeWithText("Pop to root").performClick()
        rule.onNodeWithTag(NestedScreen.keyFor(1)).assertIsDisplayed()
    }

    @Test
    fun nestedScreen_popToIndex() {
        rule.navigateNestedTab()
        rule.onNodeWithTag(NestedScreen.keyFor(1)).assertIsDisplayed()

        repeat(6) {
            rule.onNodeWithContentDescription("Add").performClick()
            rule.onNodeWithTag(NestedScreen.keyFor(it + 2)).assertIsDisplayed()
        }

        rule.onNodeWithText("Pop to index").performTextInput("4")
        rule.onNodeWithContentDescription("Pop").performClick()
        rule.onNodeWithTag(NestedScreen.keyFor(4)).assertIsDisplayed()
        rule.onAllNodesWithText("4")[1].performTextClearance()

        rule.onNodeWithText("Pop to index").performTextInput("2")
        rule.onNodeWithContentDescription("Pop").performClick()
        rule.onNodeWithTag(NestedScreen.keyFor(2)).assertIsDisplayed()
    }
}
