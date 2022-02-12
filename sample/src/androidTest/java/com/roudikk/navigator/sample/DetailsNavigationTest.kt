package com.roudikk.navigator.sample

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.roudikk.navigator.NavigationNode.Companion.key
import com.roudikk.navigator.sample.ui.screens.details.DetailsBottomSheet
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
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_bottomSheet_opensBottomSheet() {
        rule.navigateDetails()
        rule.onNodeWithText("Bottom Sheet").performClick()
        rule.onNodeWithTag(key<DetailsBottomSheet>()).assertIsDisplayed()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_bottomSheet_toBottomSheet() {
        rule.navigateDetails()
        rule.onNodeWithText("Bottom Sheet").performClick()
        rule.onNodeWithTag(key<DetailsBottomSheet>()).assertIsDisplayed()
        rule.onAllNodesWithText("Bottom Sheet")[1].performClick()
        rule.onNodeWithTag(key<DetailsBottomSheet>()).assertIsDisplayed()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<DetailsBottomSheet>()).assertIsDisplayed()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_bottomSheet_toScreen() {
        rule.navigateDetails()
        rule.onNodeWithText("Bottom Sheet").performClick()
        rule.onNodeWithTag(key<DetailsBottomSheet>()).assertIsDisplayed()
        rule.onAllNodesWithText("New random item")[1].performClick()
        rule.onNodeWithTag(key<DetailsBottomSheet>()).assertDoesNotExist()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_singleTopScreen() {
        rule.navigateDetails()
        rule.onNodeWithText("New random item").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.onNodeWithText("Single top Screen").performClick()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_singleInstanceScreen() {
        rule.navigateDetails()
        repeat(10) { rule.onNodeWithText("New random item").performClick() }
        rule.onNodeWithText("Single Instance").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }

    @Test
    fun details_popLast() {
        rule.navigateDetails()
        repeat(3) { rule.onNodeWithText("New random item").performClick() }
        rule.onNodeWithText("Navigate and pop last").performClick()
        repeat(3) { rule.activity.onBackPressed() }
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
        rule.onNodeWithText("Bottom Sheet").performClick()
        rule.onAllNodesWithText("Send result back to home")[1].performClick()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertDoesNotExist()
        rule.onNodeWithTag(key<DetailsBottomSheet>()).assertDoesNotExist()
    }
}
