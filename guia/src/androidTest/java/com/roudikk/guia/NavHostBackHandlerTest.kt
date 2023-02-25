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
import com.roudikk.guia.backstack.navhost.StackHistoryBackHandler
import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.extensions.push
import com.roudikk.guia.navhost.rememberNavHost
import com.roudikk.guia.navhost.to
import com.roudikk.guia.util.TestKey
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
            val backPressDispatcher = requireNotNull(LocalOnBackPressedDispatcherOwner.current)
                .onBackPressedDispatcher

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
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
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

    @Test
    fun stackHistoryBackHandler_popsHistory() {
        rule.setContent {
            var testStep by remember { mutableStateOf(1) }
            val backPressDispatcher = requireNotNull(LocalOnBackPressedDispatcherOwner.current)
                .onBackPressedDispatcher
            val configBuilder = remember<NavigatorConfigBuilder.() -> Unit> {
                { screen<TestKey> {} }
            }

            val testNavigator = rememberNavigator(initialKey = TestKey()) { configBuilder() }
            val testNavigator2 = rememberNavigator(initialKey = TestKey()) { configBuilder() }
            val testNavigator3 = rememberNavigator(initialKey = TestKey()) { configBuilder() }

            val navHost = rememberNavHost(
                initialKey = TestStackKey,
                entries = setOf(
                    TestStackKey to testNavigator,
                    TestStackKey2 to testNavigator2,
                    TestStackKey3 to testNavigator3
                )
            )

            navHost.StackHistoryBackHandler()

            LaunchedEffect(testStep) {
                when (testStep) {
                    1 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                        navHost.setActive(TestStackKey2)
                    }

                    2 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey2)
                        testStep = 3
                    }

                    3 -> {
                        backPressDispatcher.onBackPressed()
                        testStep = 4
                    }

                    4 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                        testNavigator.push(TestKey())
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                        navHost.setActive(TestStackKey3)
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey3)
                        navHost.setActive(TestStackKey2)
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey2)
                        testNavigator2.push(TestKey())
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey2)
                        navHost.setActive(TestStackKey)
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                        testStep = 5
                    }

                    5 -> {
                        backPressDispatcher.onBackPressed()
                        testStep = 6
                    }

                    6 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey2)
                        backPressDispatcher.onBackPressed()
                        testStep = 7
                    }

                    7 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey2)
                        backPressDispatcher.onBackPressed()
                        testStep = 8
                    }

                    8 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey3)
                        backPressDispatcher.onBackPressed()
                        testStep = 9
                    }

                    9 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                        backPressDispatcher.onBackPressed()
                        testStep = 10
                    }

                    10 -> {
                        assertThat(navHost.currentKey).isEqualTo(TestStackKey)
                    }
                }
            }
        }
    }
}
