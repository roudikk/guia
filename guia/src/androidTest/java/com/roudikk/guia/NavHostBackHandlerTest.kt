package com.roudikk.guia

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth.assertThat
import com.roudikk.guia.backstack.navhost.DefaultStackBackHandler
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.navhost.rememberNavHost
import com.roudikk.guia.navhost.to
import com.roudikk.guia.util.TestStackKey
import com.roudikk.guia.util.TestStackKey2
import com.roudikk.guia.util.TestStackKey3
import org.junit.Rule
import org.junit.Test

class NavHostBackHandlerTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun defaultBackHandler_popsToDefault() {
        rule.setContent {
            var testStep by remember { mutableStateOf(1) }
            val backPressDispatcher = LocalOnBackPressedDispatcherOwner
                .current!!.onBackPressedDispatcher

            val testNavigator = rememberNavigator()
            val testNavigator2 = rememberNavigator()
            val testNavigator3 = rememberNavigator()

            val navHost = rememberNavHost(
                initialKey = TestStackKey,
                entries = setOf(
                    TestStackKey to testNavigator,
                    TestStackKey2 to testNavigator2,
                    TestStackKey3 to testNavigator3
                )
            )

            navHost.DefaultStackBackHandler(stackKey = TestStackKey)

            LaunchedEffect(testStep) {
                when (testStep) {
                    1 -> {
                        assert(navHost.currentKey == TestStackKey)
                        navHost.setActive(TestStackKey2)
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey2)
                        testStep = 2
                    }

                    2 -> {
                        backPressDispatcher.onBackPressed()
                        testStep = 3
                    }

                    3 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                        backPressDispatcher.onBackPressed()
                        testStep = 4
                    }

                    4 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                        navHost.setActive(TestStackKey2)
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey2)
                        testStep = 5
                    }

                    5 -> {
                        navHost.setActive(TestStackKey3)
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey3)
                        testStep = 6
                    }

                    6 -> {
                        backPressDispatcher.onBackPressed()
                        testStep = 7
                    }

                    7 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                    }
                }
            }
        }
    }
}
