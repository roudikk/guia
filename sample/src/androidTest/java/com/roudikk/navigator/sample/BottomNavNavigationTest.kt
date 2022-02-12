package com.roudikk.navigator.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.roudikk.navigator.NavigationNode.Companion.key
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsScreen
import com.roudikk.navigator.sample.ui.screens.home.HomeScreen
import com.roudikk.navigator.sample.ui.screens.navigation_tree.NavigationTreeScreen
import com.roudikk.navigator.sample.ui.screens.nested.ParentNestedScreen
import com.roudikk.navigator.sample.utils.navigateHome
import org.junit.Rule
import org.junit.Test

class BottomNavNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNav_navigateTabs() {
        rule.navigateHome()

        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()

        rule.onNodeWithTag("tab_nested").performClick()

        rule.onNodeWithTag(key<ParentNestedScreen>()).assertIsDisplayed()

        rule.onNodeWithTag("tab_dialogs").performClick()

        rule.onNodeWithTag(key<DialogsScreen>()).assertIsDisplayed()

        rule.onNodeWithTag("tab_nav_tree").performClick()

        rule.onNodeWithTag(key<NavigationTreeScreen>()).assertIsDisplayed()
    }
}
