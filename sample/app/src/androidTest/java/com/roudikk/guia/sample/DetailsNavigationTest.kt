package com.roudikk.guia.sample

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import com.roudikk.guia.core.NavigationKey.Companion.tag
import com.roudikk.guia.sample.feature.details.DetailsBottomSheetKey
import com.roudikk.guia.sample.feature.details.navigation.DetailsKey
import com.roudikk.guia.sample.feature.home.navigation.HomeKey
import com.roudikk.guia.sample.utils.navigateDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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

        rule.onNodeWithText("Screen: Navigate").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_bottomSheet_opensBottomSheet() {
        rule.navigateDetails()

        rule.onNodeWithText("BottomSheet: Navigate").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertDoesNotExist()
    }

    @Test
    fun details_bottomSheet_toBottomSheet() {
        rule.navigateDetails()

        rule.onNodeWithText("BottomSheet: Navigate").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.onAllNodesWithText("BottomSheet: Navigate")[1].performClick()
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

        rule.onNodeWithText("BottomSheet: Navigate").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.onAllNodesWithText("Screen: Navigate")[1].performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertDoesNotExist()
    }

    @Test
    fun details_singleTopScreen() {
        rule.navigateDetails()

        rule.onNodeWithText("Screen: Navigate").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.onNodeWithText("Screen: Single Top").performClick()
        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_singleInstanceScreen_new() {
        rule.navigateDetails()

        repeat(10) { rule.onNodeWithText("Screen: Navigate").performClick() }
        rule.onNodeWithText("Screen: Single Instance (New)").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_singleInstanceKey_existing() {
        rule.navigateDetails()

        repeat(10) { rule.onNodeWithText("Screen: Navigate").performClick() }
        rule.onNodeWithText("Screen: Single Instance (Existing)").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_replace() {
        rule.navigateDetails()

        repeat(3) { rule.onNodeWithText("Screen: Navigate").performClick() }
        rule.onNodeWithText("Screen: Replace").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()

        repeat(4) { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
    }

    @Test
    fun details_sendResultHome() {
        rule.navigateDetails()

        rule.onNodeWithText("Send Result To Home").performClick()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertDoesNotExist()
    }

    @Test
    fun detailsBottomSheet_sendResultHome() {
        rule.navigateDetails()

        rule.onNodeWithText("BottomSheet: Navigate").performClick()
        rule.onNodeWithTag(tag<DetailsKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertIsDisplayed()

        rule.onAllNodesWithTag("details_list")[1]
            .performScrollToNode(hasText("Send Result To Home"))
        rule.onAllNodesWithText("Send Result To Home")[1].performClick()

        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()
        rule.onNodeWithTag(tag<DetailsKey>()).assertDoesNotExist()
        rule.onNodeWithTag(tag<DetailsBottomSheetKey>()).assertDoesNotExist()
    }
}
