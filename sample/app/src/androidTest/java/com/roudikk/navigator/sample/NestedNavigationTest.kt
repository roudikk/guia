package com.roudikk.navigator.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.roudikk.navigator.sample.feature.nested.api.NestedKey
import com.roudikk.navigator.sample.utils.navigateNestedTab
import org.junit.Rule
import org.junit.Test

class NestedNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun nestedScreen_add() {
        rule.navigateNestedTab()
        rule.onNodeWithTag(NestedKey.tagFor(1)).assertIsDisplayed()

        repeat(3) {
            rule.onNodeWithContentDescription("Add").performClick()
            rule.onNodeWithTag(NestedKey.tagFor(it + 2)).assertIsDisplayed()
        }
    }

    @Test
    fun nestedScreen_remove() {
        rule.navigateNestedTab()
        rule.onNodeWithTag(NestedKey.tagFor(1)).assertIsDisplayed()

        repeat(3) {
            rule.onNodeWithContentDescription("Add").performClick()
            rule.onNodeWithTag(NestedKey.tagFor(it + 2)).assertIsDisplayed()
        }

        repeat(3) {
            rule.onNodeWithContentDescription("Remove").performClick()
            rule.onNodeWithTag(NestedKey.tagFor(it + 2)).assertDoesNotExist()
        }

        rule.onNodeWithTag(NestedKey.tagFor(1)).assertIsDisplayed()
    }

    @Test
    fun nestedScreen_popToRoot() {
        rule.navigateNestedTab()
        rule.onNodeWithTag(NestedKey.tagFor(1)).assertIsDisplayed()

        repeat(3) {
            rule.onNodeWithContentDescription("Add").performClick()
            rule.onNodeWithTag(NestedKey.tagFor(it + 2)).assertIsDisplayed()
        }

        rule.onNodeWithText("Pop to root").performClick()
        rule.onNodeWithTag(NestedKey.tagFor(1)).assertIsDisplayed()
    }

    @Test
    fun nestedScreen_popToIndex() {
        rule.navigateNestedTab()
        rule.onNodeWithTag(NestedKey.tagFor(1)).assertIsDisplayed()

        repeat(6) {
            rule.onNodeWithContentDescription("Add").performClick()
            rule.onNodeWithTag(NestedKey.tagFor(it + 2)).assertIsDisplayed()
        }

        rule.onNodeWithText("Navigate to index").performTextInput("4")
        rule.onNodeWithContentDescription("Pop").performClick()
        rule.onNodeWithTag(NestedKey.tagFor(4)).assertIsDisplayed()
        rule.onAllNodesWithText("4")[1].performTextClearance()

        rule.onNodeWithText("Navigate to index").performTextInput("2")
        rule.onNodeWithContentDescription("Pop").performClick()
        rule.onNodeWithTag(NestedKey.tagFor(2)).assertIsDisplayed()
    }
}
