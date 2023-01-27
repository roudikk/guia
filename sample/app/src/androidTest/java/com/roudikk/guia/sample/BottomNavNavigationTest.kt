package com.roudikk.guia.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.roudikk.guia.core.NavigationKey.Companion.tag
import com.roudikk.guia.sample.feature.custom.navigation.ViewPagerRootKey
import com.roudikk.guia.sample.feature.dialogs.navigation.DialogsKey
import com.roudikk.guia.sample.feature.home.navigation.HomeKey
import com.roudikk.guia.sample.feature.nested.navigation.ParentNestedKey
import com.roudikk.guia.sample.utils.navigateHome
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

        rule.onNodeWithTag("tab_custom").performClick()
        rule.onNodeWithTag(tag<ViewPagerRootKey>()).assertIsDisplayed()
    }
}
