package com.roudikk.navigator.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.roudikk.navigator.core.NavigationNode.Companion.key
import com.roudikk.navigator.sample.ui.screens.dialogs.BlockingBottomSheet
import com.roudikk.navigator.sample.ui.screens.dialogs.BlockingDialog
import com.roudikk.navigator.sample.ui.screens.dialogs.CancelableDialog
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsScreen
import com.roudikk.navigator.sample.utils.navigateDialogsTab
import org.junit.Rule
import org.junit.Test

class DialogsNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun dialogs_cancelableDialog() {
        rule.navigateDialogsTab()
        rule.onNodeWithText("Cancelable Dialog").performClick()
        rule.onNodeWithTag(key<CancelableDialog>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<CancelableDialog>()).assertDoesNotExist()
        rule.onNodeWithTag(key<DialogsScreen>()).assertIsDisplayed()
    }

    @Test
    fun dialogs_blockingDialog() {
        rule.navigateDialogsTab()
        rule.onNodeWithText("Blocking Dialog").performClick()
        rule.onNodeWithTag(key<BlockingDialog>()).assertIsDisplayed()
        // Seems like hitting back right after opening dialog will dismiss it
        // instead give it some time to display first
        rule.mainClock.autoAdvance = false
        rule.mainClock.advanceTimeBy(100)
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<BlockingDialog>()).assertIsDisplayed()
    }

    @Test
    fun dialogs_dialogToDialog() {
        rule.navigateDialogsTab()
        rule.onNodeWithText("Dialog To Dialog").performClick()
        rule.onNodeWithTag(key<BlockingDialog>()).assertIsDisplayed()
        rule.onNodeWithText("Next").performClick()
        rule.onNodeWithTag(key<CancelableDialog>()).assertIsDisplayed()
        rule.onNodeWithText("Go back to root").performClick()
        rule.onNodeWithTag(key<BlockingDialog>()).assertDoesNotExist()
        rule.onNodeWithTag(key<CancelableDialog>()).assertDoesNotExist()
        rule.onNodeWithTag(key<DialogsScreen>()).assertIsDisplayed()
    }

    @Test
    fun dialogs_blockingBottomSheet() {
        rule.navigateDialogsTab()
        rule.onNodeWithText("Blocking Bottom Sheet").performClick()
        rule.onNodeWithTag(key<BlockingBottomSheet>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<BlockingBottomSheet>()).assertIsDisplayed()
        rule.onNode(isToggleable()).performClick()
        rule.waitForIdle()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<BlockingBottomSheet>()).assertDoesNotExist()
        rule.onNodeWithTag(key<DialogsScreen>()).assertIsDisplayed()
    }
}
