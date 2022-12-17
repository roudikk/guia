package com.roudikk.navigator.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.roudikk.navigator.core.NavigationKey.Companion.tag
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsKey
import com.roudikk.navigator.sample.ui.screens.home.HomeKey
import com.roudikk.navigator.sample.ui.screens.navigationtree.NavigationTreeKey
import com.roudikk.navigator.sample.ui.screens.nested.ParentNestedKey
import com.roudikk.navigator.sample.utils.navigateHome
import org.junit.Rule
import org.junit.Test

class BottomNavNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNav_navigateTabs() {
        rule.navigateHome()
        rule.onNodeWithTag(tag<HomeKey>()).assertIsDisplayed()

        rule.onNodeWithTag("tab_nested").performClick()
        rule.onNodeWithTag(tag<ParentNestedKey>()).assertIsDisplayed()

        rule.onNodeWithTag("tab_dialogs").performClick()
        rule.onNodeWithTag(tag<DialogsKey>()).assertIsDisplayed()

        rule.onNodeWithTag("tab_nav_tree").performClick()
        rule.onNodeWithTag(tag<NavigationTreeKey>()).assertIsDisplayed()
    }
}
