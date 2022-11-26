package com.roudikk.navigator.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.roudikk.navigator.NavigationKey.Companion.tag
import com.roudikk.navigator.sample.ui.screens.details.DetailsBottomSheetKey
import com.roudikk.navigator.sample.ui.screens.details.DetailsKey
import com.roudikk.navigator.sample.ui.screens.home.HomeKey
import com.roudikk.navigator.sample.utils.navigateDetails
import org.junit.Rule
import org.junit.Test

class DetailsNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun details_navigateDetailsFromHome() {
        rule.navigateDetails()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
    }

    @Test
    fun details_newRandomItem_addsToStack() {
        rule.navigateDetails()

        rule.onNodeWithText("New random item").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_bottomSheet_opensBottomSheet() {
        rule.navigateDetails()

        rule.onNodeWithText("BottomSheet").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertDoesNotExist()
    }

    @Test
    fun details_bottomSheet_toBottomSheet() {
        rule.navigateDetails()

        rule.onNodeWithText("BottomSheet").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.onAllNodesWithText("BottomSheet")[1].performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertDoesNotExist()
    }

    @Test
    fun details_bottomSheet_toScreen() {
        rule.navigateDetails()

        rule.onNodeWithText("BottomSheet").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.onAllNodesWithText("New random item")[1].performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertDoesNotExist()
    }

    @Test
    fun details_singleTopScreen() {
        rule.navigateDetails()

        rule.onNodeWithText("New random item").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.onNodeWithText("Single top Screen").performClick()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_singleInstanceScreen_new() {
        rule.navigateDetails()

        repeat(10) { rule.onNodeWithText("New random item").performClick() }
        rule.onNodeWithText("Single Instance (New)").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_singleInstanceKey_existing() {
        rule.navigateDetails()

        repeat(10) { rule.onNodeWithText("New random item").performClick() }
        rule.onNodeWithText("Single Instance (Existing)").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_replace() {
        rule.navigateDetails()

        repeat(3) { rule.onNodeWithText("New random item").performClick() }
        rule.onNodeWithText("Navigate and pop last (Replace)").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        repeat(4) { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_sendResultHome() {
        rule.navigateDetails()

        rule.onNodeWithText("Send result back to home").performClick()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertDoesNotExist()
    }

    @Test
    fun detailsBottomSheet_sendResultHome() {
        rule.navigateDetails()

        rule.onNodeWithText("BottomSheet").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.onAllNodesWithText("Send result back to home")[1].performClick()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertDoesNotExist()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertDoesNotExist()
    }
}
