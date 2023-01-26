package com.roudikk.guia.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.roudikk.guia.core.NavigationKey.Companion.tag
import com.roudikk.guia.sample.feature.dialogs.navigation.BlockingBottomSheetKey
import com.roudikk.guia.sample.feature.dialogs.navigation.BlockingDialogKey
import com.roudikk.guia.sample.feature.dialogs.navigation.CancelableDialogKey
import com.roudikk.guia.sample.feature.dialogs.navigation.DialogsKey
import com.roudikk.guia.sample.utils.navigateDialogsTab
import org.junit.Rule
import org.junit.Test

class DialogsNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun dialogs_cancelableDialog() {
        rule.navigateDialogsTab()
        rule.onNodeWithText("Cancelable Dialog").performClick()
        rule.onNodeWithTag(tag<CancelableDialogKey>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<CancelableDialogKey>()).assertDoesNotExist()
        rule.onNodeWithTag(tag<DialogsKey>()).assertIsDisplayed()
    }

    @Test
    fun dialogs_blockingDialog() {
        rule.navigateDialogsTab()
        rule.onNodeWithText("Blocking Dialog").performClick()
        rule.onNodeWithTag(tag<BlockingDialogKey>()).assertIsDisplayed()
        // Seems like hitting back right after opening dialog will dismiss it
        // instead give it some time to display first
        rule.mainClock.autoAdvance = false
        rule.mainClock.advanceTimeBy(100)
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<BlockingDialogKey>()).assertIsDisplayed()
    }

    @Test
    fun dialogs_dialogToDialog() {
        rule.navigateDialogsTab()
        rule.onNodeWithText("Dialog To Dialog").performClick()
        rule.onNodeWithTag(tag<BlockingDialogKey>()).assertIsDisplayed()
        rule.onNodeWithText("Next").performClick()
        rule.onNodeWithTag(tag<CancelableDialogKey>()).assertIsDisplayed()
        rule.onNodeWithText("Go back to root").performClick()
        rule.onNodeWithTag(tag<BlockingDialogKey>()).assertDoesNotExist()
        rule.onNodeWithTag(tag<CancelableDialogKey>()).assertDoesNotExist()
        rule.onNodeWithTag(tag<DialogsKey>()).assertIsDisplayed()
    }

    @Test
    fun dialogs_blockingBottomSheet() {
        rule.navigateDialogsTab()
        rule.onNodeWithText("Blocking Bottom Sheet").performClick()
        rule.onNodeWithTag(tag<BlockingBottomSheetKey>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<BlockingBottomSheetKey>()).assertIsDisplayed()
        rule.onNode(isToggleable()).performClick()
        rule.waitForIdle()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<BlockingBottomSheetKey>()).assertDoesNotExist()
        rule.onNodeWithTag(tag<DialogsKey>()).assertIsDisplayed()
    }
}
