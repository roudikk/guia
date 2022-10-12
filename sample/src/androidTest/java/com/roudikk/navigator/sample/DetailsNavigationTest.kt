package com.roudikk.navigator.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.roudikk.navigator.core.NavigationNode.Companion.bottomSheetKey
import com.roudikk.navigator.core.NavigationNode.Companion.key
import com.roudikk.navigator.sample.ui.screens.details.DetailsScreen
import com.roudikk.navigator.sample.ui.screens.home.HomeScreen
import com.roudikk.navigator.sample.utils.navigateDetails
import org.junit.Rule
import org.junit.Test

class DetailsNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun details_navigateDetailsFromHome() {
        rule.navigateDetails()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_newRandomItem_addsToStack() {
        rule.navigateDetails()
        rule.onNodeWithText("New random item").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_bottomSheet_opensBottomSheet() {
        rule.navigateDetails()
        rule.onNodeWithText("BottomSheet").performClick()
        rule.onNodeWithTag(bottomSheetKey<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(bottomSheetKey<DetailsScreen>()).assertDoesNotExist()
    }

    @Test
    fun details_bottomSheet_toBottomSheet() {
        rule.navigateDetails()
        rule.onNodeWithText("BottomSheet").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(bottomSheetKey<DetailsScreen>()).assertIsDisplayed()
        rule.onAllNodesWithText("BottomSheet")[1].performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(bottomSheetKey<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(bottomSheetKey<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(bottomSheetKey<DetailsScreen>()).assertDoesNotExist()
    }

    @Test
    fun details_bottomSheet_toScreen() {
        rule.navigateDetails()
        rule.onNodeWithText("BottomSheet").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onAllNodesWithText("New random item")[1].performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(bottomSheetKey<DetailsScreen>()).assertDoesNotExist()
    }

    @Test
    fun details_singleTopScreen() {
        rule.navigateDetails()
        rule.onNodeWithText("New random item").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithText("Single top Screen").performClick()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_singleInstanceScreen_new() {
        rule.navigateDetails()
        repeat(10) { rule.onNodeWithText("New random item").performClick() }
        rule.onNodeWithText("Single Instance (New)").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_singleInstanceScreen_existing() {
        rule.navigateDetails()
        repeat(10) { rule.onNodeWithText("New random item").performClick() }
        rule.onNodeWithText("Single Instance (Existing)").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_replace() {
        rule.navigateDetails()
        repeat(3) { rule.onNodeWithText("New random item").performClick() }
        rule.onNodeWithText("Navigate and pop last (Replace)").performClick()
        repeat(4) { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_sendResultHome() {
        rule.navigateDetails()
        rule.onNodeWithText("Send result back to home").performClick()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertDoesNotExist()
    }

    @Test
    fun detailsBottomSheet_sendResultHome() {
        rule.navigateDetails()
        rule.onNodeWithText("BottomSheet").performClick()
        rule.onAllNodesWithText("Send result back to home")[1].performClick()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(bottomSheetKey<DetailsScreen>()).assertDoesNotExist()
        rule.onNodeWithTag(key<DetailsScreen>()).assertDoesNotExist()
    }
}
