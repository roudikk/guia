@file:Suppress("TestFunctionName")

package com.roudikk.navigator

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import com.roudikk.navigator.core.BackStackStrategy
import com.roudikk.navigator.core.EmptyNavigationNode
import com.roudikk.navigator.core.NavigationConfig
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.StackKey
import kotlinx.parcelize.Parcelize
import org.junit.Rule
import org.junit.Test

class RememberNavigatorTest {

    @Parcelize
    open class TestScreen : Screen {

        @Composable
        override fun Content() = Unit
    }

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun rememberNavigator_singleStack_remembersState() {
        val stateRestorationTester = StateRestorationTester(rule)
        var initialized = false
        val screen1 = TestScreen()
        val screen2 = object : TestScreen() {
            override val key: String = "TestScreen-2"
        }
        stateRestorationTester.setContent {
            println("Setting content")
            val navigator = rememberNavigator()
            if (!initialized) {
                navigator.navigate(screen1)
                navigator.navigate(screen2)
                initialized = true
            } else {
                assert(
                    navigator.currentState.currentStack.destinations.map { it.navigationNode } ==
                        listOf(EmptyNavigationNode, screen1, screen2)
                )
            }
        }
        stateRestorationTester.emulateSavedInstanceStateRestore()
    }

    @Test
    fun rememberNavigator_multiStack_remembersState() {
        val stateRestorationTester = StateRestorationTester(rule)
        var initialized = false
        val stackEntries = (0 until 3).map {
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = StackKey(),
                initialNavigationNode = object : TestScreen() {
                    override val key: String = "BaseTestScreen-$it"
                }
            )
        }
        val screens = (0 until 10).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }
        stateRestorationTester.setContent {
            println("Setting content")
            val navigator = rememberNavigator(
                NavigationConfig.MultiStack(
                    entries = stackEntries,
                    initialStackKey = stackEntries[0].key,
                    backStackStrategy = BackStackStrategy.CrossStackHistory
                )
            )
            if (!initialized) {
                (0 until 3).map { screens[it] }.forEach(navigator::navigate)
                navigator.navigateToStack(stackEntries[1].key)
                (3 until 6).map { screens[it] }.forEach(navigator::navigate)
                navigator.navigateToStack(stackEntries[2].key)
                (6 until 10).map { screens[it] }.forEach(navigator::navigate)
                navigator.navigateToStack(stackEntries[1].key)
                initialized = true
            } else {
                assert(navigator.currentState.currentStackKey == stackEntries[1].key)
                assert(
                    navigator.currentState.navigationStacks[0].destinations.map { it.navigationNode } ==
                        listOf(
                            stackEntries[0].initialNavigationNode,
                            screens[0], screens[1], screens[2]
                        )
                )
                assert(
                    navigator.currentState.navigationStacks[1].destinations.map { it.navigationNode } ==
                        listOf(
                            stackEntries[1].initialNavigationNode,
                            screens[3], screens[4], screens[5]
                        )
                )
                assert(
                    navigator.currentState.navigationStacks[2].destinations.map { it.navigationNode } ==
                        listOf(
                            stackEntries[2].initialNavigationNode,
                            screens[6], screens[7], screens[8], screens[9]
                        )
                )
                navigator.popBackStack()
                assert(navigator.currentState.currentStackKey == stackEntries[2].key)
                repeat(4) {
                    navigator.popBackStack()
                    assert(navigator.currentState.currentStackKey == stackEntries[2].key)
                }
                navigator.popBackStack()
                assert(navigator.currentState.currentStackKey == stackEntries[1].key)
                repeat(3) {
                    navigator.popBackStack()
                    assert(navigator.currentState.currentStackKey == stackEntries[1].key)
                }
                navigator.popBackStack()
                assert(navigator.currentState.currentStackKey == stackEntries[0].key)
                repeat(3) {
                    navigator.popBackStack()
                    assert(navigator.currentState.currentStackKey == stackEntries[0].key)
                }
            }
        }
        stateRestorationTester.emulateSavedInstanceStateRestore()
    }
}
