@file:OptIn(
    ExperimentalCoroutinesApi::class,
    DelicateCoroutinesApi::class,
    ExperimentalCoroutinesApi::class
)

package com.roudikk.compose_navigator

import android.os.Parcel
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import com.google.common.truth.Truth.assertThat
import com.roudikk.compose_navigator.animation.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class NavigatorTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    open class TestScreen : Screen {
        @Composable
        override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) = error("")
        override fun describeContents(): Int = error("")
        override fun writeToParcel(p0: Parcel?, p1: Int) = error("")
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @DisplayName(
        """
            When initialize is called with NavigationConfig SingleStack,
            Then the navigator is initialized with a single stack
        """
    )
    @Test
    fun `Initialize single stack navigator`() = runTest {
        val screen = object : TestScreen() {
            override val key: String = "TestScreen"
        }

        val navigator = Navigator()

        navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screen))

        val navigationState = navigator.stateFlow.value

        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.any {
            it.navigationNode == screen
        }).isTrue()
    }

    @DisplayName(
        """
            When initialize is called with NavigationConfig MultiStack, 
            Then the navigator is initialized with multiple stacks
        """
    )
    @Test
    fun `Initialize Multi Stack Navigator`() {
        val keyScreenPair = (0..2).map {
            NavigationKey() to object : NavigatorTest.TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigationStacks = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val navigator = Navigator()

        navigator.initialize(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStacks,
                initialStackKey = navigationStacks[1].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        val navigationState = navigator.stateFlow.value

        assertThat(navigationState.navigationStacks.size).isEqualTo(
            navigationStacks.size
        )

        navigationState.navigationStacks.forEachIndexed { index, navigationStack ->
            assertThat(navigationStack.destinations.size).isEqualTo(1)
            assertThat(navigationStack.key).isEqualTo(keyScreenPair[index].first)
            assertThat(navigationStack.destinations.any { it.navigationNode == keyScreenPair[index].second })
                .isTrue()
        }

        assertThat(navigationState.currentStack.key).isEqualTo(navigationStacks[1].key)
    }

    @DisplayName(
        """
            Given initial stack key does not exist in stack entries,
            When initialize is called with Multi stack config,
            then an exception is thrown
        """
    )
    @Test
    fun `Initialize Multi stack navigator with none existing initial stack key`() {
        val keyScreenPair = (0..2).map {
            NavigationKey() to object : NavigatorTest.TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigationStacks = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val navigator = Navigator()

        assertThrows<IllegalStateException> {
            navigator.initialize(
                navigationConfig = NavigationConfig.MultiStack(
                    entries = navigationStacks,
                    initialStackKey = DefaultNavigationKey,
                    backStackStrategy = BackStackStrategy.Default
                )
            )
        }
    }

    @DisplayName(
        """
            When navigate is called on a single stack navigator,
            Then the state is updated with the new destination and transitions
        """
    )
    @Test
    fun `Navigate single stack navigator`() = runTest {
        val navigator = Navigator()

        val screen = object : TestScreen() {
            override val key: String = "TestScreen"
        }

        val newScreen = object : TestScreen() {
            override val key: String = "NewTestScreen"
        }

        val navOptions = NavOptions()

        navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screen))
        navigator.navigate(newScreen, navOptions)

        val navigationState = navigator.stateFlow.value

        assertThat(navigator.stackHistory).containsExactly(
            StackHistoryEntry(DefaultNavigationKey, screen.key),
            StackHistoryEntry(DefaultNavigationKey, newScreen.key)
        )
        assertThat(navigationState.currentStackKey).isEqualTo(DefaultNavigationKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(2)
        assertThat(navigationState.currentStack.destinations.last().navigationNode)
            .isEqualTo(newScreen)
        assertThat(navigationState.currentStack.destinations.last().navOptions).isEqualTo(navOptions)
        assertThat(navigationState.transitionPair.enter).isEqualTo(navOptions.navTransition.enter)
        assertThat(navigationState.transitionPair.exit).isEqualTo(navOptions.navTransition.exit)
    }

    @DisplayName(
        """
            Given there's more than one destination in the backstack,
            When popBackStack is called on a single stack navigator,
            Then true is returned and the state is updated with the previous destination and transitions 
        """
    )
    @Test
    fun `Pop backstack with a single destination left`() = runTest {
        val navigator = Navigator()

        val screen = object : TestScreen() {
            override val key: String = "TestScreen"
        }

        val newScreen = object : TestScreen() {
            override val key: String = "NewTestScreen"
        }

        val navOptions = NavOptions()

        navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screen))
        navigator.navigate(newScreen, navOptions)

        assertThat(navigator.stackHistory).containsExactly(
            StackHistoryEntry(DefaultNavigationKey, screen.key),
            StackHistoryEntry(DefaultNavigationKey, newScreen.key)
        )

        assertThat(navigator.canGoBack()).isTrue()

        val popBackStack = navigator.popBackStack()

        val navigationState = navigator.stateFlow.value

        assertThat(popBackStack).isTrue()
        assertThat(navigator.stackHistory)
            .containsExactly(StackHistoryEntry(DefaultNavigationKey, screen.key))
        assertThat(navigationState.currentStackKey).isEqualTo(DefaultNavigationKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.last().navigationNode).isEqualTo(screen)
        assertThat(navigationState.currentStack.destinations.last().navOptions)
            .isNotEqualTo(DefaultNavTransition.enter to DefaultNavTransition.exit)
        assertThat(navigationState.transitionPair.enter).isEqualTo(navOptions.navTransition.popEnter)
        assertThat(navigationState.transitionPair.exit).isEqualTo(navOptions.navTransition.popExit)
    }

    @DisplayName(
        """
            Given there's only one destination in the backstack,
            When popBackStack is called on a single stack navigator,
            Then false is returned and the state is unchanged
        """
    )
    @Test
    fun `Pop backstack with no destinations left`() = runTest {
        val navigator = Navigator()

        val screen = object : TestScreen() {
            override val key: String = "TestScreen"
        }

        navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screen))

        val navigationState = navigator.stateFlow.value

        val popBackStack = navigator.popBackStack()

        val newNavigationState = navigator.stateFlow.value

        assertThat(popBackStack).isFalse()
        assertThat(navigator.stackHistory)
            .containsExactly(StackHistoryEntry(DefaultNavigationKey, screen.key))
        assertThat(navigationState).isEqualTo(newNavigationState)
        assertThat(navigationState.currentStackKey).isEqualTo(DefaultNavigationKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.last().navigationNode).isEqualTo(screen)
        assertThat(navigationState.currentStack.destinations.last().navOptions)
            .isNotEqualTo(DefaultNavTransition.enter to DefaultNavTransition.exit)
        assertThat(navigationState.transitionPair.enter).isEqualTo(DefaultNavTransition.enter)
        assertThat(navigationState.transitionPair.exit).isEqualTo(DefaultNavTransition.exit)
    }


    @TestFactory
    fun `Pop to existing key, inclusive true or false`(): List<DynamicTest> {
        return listOf(
            (false to false) to "the target destination",
            (true to false) to "the destination before the target destination",
            (false to true) to "the target destination",
            (true to true) to "the destination before the target destination",
        ).map { (pair, result) ->
            val (inclusive, useGenericPopTo) = pair
            dynamicTest(
                """
                    Given a key exists for target destination,
                    When popTo is called with given key and inclusive is $inclusive,
                    Then the new state's last destination is $result
                """.trimIndent()
            ) {

                val navigator = Navigator()

                val screens = (0..10).map {
                    if (useGenericPopTo && it == 3) {
                        TestScreen()
                    } else {
                        object : TestScreen() {
                            override val key: String = "TestScreen-$it"
                        }
                    }
                }

                val poppedToIndex = if (inclusive) 2 else 3

                navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screens[0]))

                (screens - screens[0]).forEach(navigator::navigate)

                assertThat(navigator.stackHistory).containsExactlyElementsIn(screens.map {
                    StackHistoryEntry(DefaultNavigationKey, it.key)
                })

                val popTo = if (useGenericPopTo) {
                    navigator.popTo<TestScreen>(inclusive = inclusive)
                } else {
                    navigator.popTo(screens[3].key, inclusive = inclusive)
                }

                assertThat(popTo).isTrue()

                val navigationState = navigator.stateFlow.value

                assertThat(navigator.stackHistory).containsExactlyElementsIn(
                    screens.subList(0, poppedToIndex + 1).map {
                        StackHistoryEntry(DefaultNavigationKey, it.key)
                    })
                assertThat(navigationState.currentStackKey).isEqualTo(DefaultNavigationKey)
                assertThat(navigationState.navigationStacks.size).isEqualTo(1)
                assertThat(navigationState.currentStack.destinations.size).isEqualTo(poppedToIndex + 1)
                assertThat(navigationState.currentStack.destinations.last().navigationNode)
                    .isEqualTo(screens[poppedToIndex])
            }
        }
    }

    @TestFactory
    fun `Pop to none existing key, inclusive true or false`(): List<DynamicTest> {
        return listOf(
            false to false,
            true to false,
            false to true,
            true to true
        ).map { (inclusive, useGenericPopTo) ->
            dynamicTest(
                """
                    Given a key doesn't exist for target destination,
                    When popTo is called with given key and inclusive is $inclusive,
                    Then the new state's last destination is unchanged
                """.trimIndent()
            ) {

                val navigator = Navigator()

                val screens = (0..10).map {
                    object : TestScreen() {
                        override val key: String = "TestScreen-$it"
                    }
                }

                navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screens[0]))

                (screens - screens[0]).forEach(navigator::navigate)

                assertThat(navigator.stackHistory).containsExactlyElementsIn(screens.map {
                    StackHistoryEntry(DefaultNavigationKey, it.key)
                })

                val popTo = if (useGenericPopTo) {
                    navigator.popTo<TestScreen>()
                } else {
                    navigator.popTo(key = "None existing key", inclusive = inclusive)
                }

                assertThat(popTo).isFalse()

                val navigationState = navigator.stateFlow.value

                assertThat(navigator.stackHistory).containsExactlyElementsIn(screens.map {
                    StackHistoryEntry(DefaultNavigationKey, it.key)
                })
                assertThat(navigationState.currentStackKey).isEqualTo(DefaultNavigationKey)
                assertThat(navigationState.navigationStacks.size).isEqualTo(1)
                assertThat(navigationState.currentStack.destinations.size).isEqualTo(11)
                assertThat(navigationState.currentStack.destinations.last().navigationNode)
                    .isEqualTo(screens[10])
            }
        }
    }

    @TestFactory
    fun `Pop to root, goes back to root`(): List<DynamicTest> {
        return listOf(
            1 to "single destination",
            10 to "many destinations"
        ).map { (destinationCount, countDescription) ->
            dynamicTest(
                """
                    When popToRoot is called with $countDescription left,
                    Then only the first destination is left.
                """.trimIndent()
            ) {

                val navigator = Navigator()

                val screens = (0 until destinationCount).map {
                    object : TestScreen() {
                        override val key: String = "TestScreen-$it"
                    }
                }

                navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screens[0]))

                (screens - screens[0]).forEach(navigator::navigate)

                assertThat(navigator.stackHistory).containsExactlyElementsIn(screens.map {
                    StackHistoryEntry(DefaultNavigationKey, it.key)
                })

                navigator.popToRoot()

                val navigationState = navigator.stateFlow.value

                assertThat(navigator.stackHistory).containsExactly(
                    StackHistoryEntry(DefaultNavigationKey, screens[0].key)
                )
                assertThat(navigationState.currentStackKey).isEqualTo(DefaultNavigationKey)
                assertThat(navigationState.navigationStacks.size).isEqualTo(1)
                assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
                assertThat(navigationState.currentStack.destinations.last().navigationNode)
                    .isEqualTo(screens[0])
            }
        }
    }

    @DisplayName(
        """
            When setRoot is called with a destination
            Then the new state contains only that new destination
        """
    )
    @Test
    fun `Set root with new destination`() {
        val navigator = Navigator()

        val screens = (0 until 10).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screens[0]))

        (screens - screens[0]).forEach(navigator::navigate)

        assertThat(navigator.stackHistory).containsExactlyElementsIn(screens.map {
            StackHistoryEntry(DefaultNavigationKey, it.key)
        })

        val newRootScreen = object : TestScreen() {
            override val key: String = "NewRootScreen"
        }

        val navOptions = NavOptions()
        navigator.setRoot(newRootScreen, navOptions)
        val navigationState = navigator.stateFlow.value

        assertThat(navigator.stackHistory).containsExactly(
            StackHistoryEntry(DefaultNavigationKey, newRootScreen.key)
        )
        assertThat(navigationState.currentStackKey).isEqualTo(DefaultNavigationKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.last().navigationNode)
            .isEqualTo(newRootScreen)
        assertThat(navigationState.transitionPair.enter).isEqualTo(navOptions.navTransition.enter)
        assertThat(navigationState.transitionPair.exit).isEqualTo(navOptions.navTransition.exit)
    }

    @TestFactory
    fun `Override back press on or off`(): List<DynamicTest> {
        return listOf(true, false).map { overrideBackPress ->
            dynamicTest(
                """
                    When overrideBackPress is called with $overrideBackPress
                    Then the new state is updated with the overrideBackPress $overrideBackPress
                """.trimIndent()
            ) {

                val navigator = Navigator()

                val testScreen = TestScreen()

                navigator.initialize(navigationConfig = NavigationConfig.SingleStack(testScreen))

                val navigationState = navigator.stateFlow.value

                assertThat(navigationState.overrideBackPress).isTrue()

                navigator.overrideBackPress(overrideBackPress)

                val newNavigatorState = navigator.stateFlow.value

                assertThat(newNavigatorState.overrideBackPress).isEqualTo(overrideBackPress)
            }
        }
    }

    @Test
    fun `Navigator results passing`() = runTest {
        val navigator = Navigator()

        val screens: MutableList<NavigationNode> = (0 until 10).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }.toMutableList()

        screens.add(TestScreen())

        navigator.initialize(navigationConfig = NavigationConfig.SingleStack(screens[0]))

        screens.forEach(navigator::navigate)

        navigator.sendResult(screens[0].key to "Hello!")
        assertThat(navigator.results(screens[0].key).first()).isEqualTo("Hello!")

        navigator.sendResult<TestScreen>(1)
        assertThat(navigator.results<TestScreen>().first()).isEqualTo(1)
    }

    @TestFactory
    fun `Navigate to stack, existing and none existing`(): List<DynamicTest> {
        return listOf(
            DefaultNavigationKey to "exists" to "nothing happens",
            NavigationKey() to "does not exist" to "an exception is thrown"
        ).map { (pair, result) ->
            val (key, existence) = pair
            dynamicTest(
                """
                    Given that a navigation key $existence
                    When navigateToStack is called
                    Then $result
                """.trimIndent()
            ) {
                val navigator = Navigator()
                navigator.initialize(navigationConfig = NavigationConfig.SingleStack(TestScreen()))

                if (key == DefaultNavigationKey) {
                    val navigationState = navigator.stateFlow.value

                    navigator.navigateToStack(key)

                    val newNavigationState = navigator.stateFlow.value

                    assertThat(navigationState).isEqualTo(newNavigationState)
                } else {
                    assertThrows<IllegalStateException> { navigator.navigateToStack(key) }
                }
            }
        }
    }

    @DisplayName(
        """
            When saving saving/restoring state of navigator,
            Then the state is properly saved and restored
        """
    )
    @Test
    fun `Save and restore state of navigator`() {
        val keyScreenPair = (0..3).map {
            NavigationKey() to object : NavigatorTest.TestScreen() {
                override val key: String = "BaseTestScreen-$it"
            }
        }

        val navigationStackEntries = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val screens = (0 until 10).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigator = Navigator()

        navigator.initialize(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        val newRootScreen = object : TestScreen() {
            override val key: String = "NewTestScreen"
        }

        navigator.navigate(screens[0])
        navigator.navigateToStack(navigationStackEntries[1].key)
        navigator.navigate(screens[1])
        navigator.navigate(screens[2])
        navigator.navigate(screens[2], navOptions = NavOptions(singleTop = true))
        navigator.navigate(screens[3])
        navigator.navigate(screens[4])
        navigator.popTo(screens[3].key)
        navigator.navigateToStack(navigationStackEntries[2].key)
        navigator.setRoot(newRootScreen)
        navigator.navigateToStack(navigationStackEntries[0].key)
        navigator.popToRoot()
        navigator.navigateToStack(navigationStackEntries[1].key)
        navigator.popBackStack()

        val navigationState = navigator.stateFlow.value

        val navigatorState = navigator.save()

        val newNavigator = Navigator()
        newNavigator.restore(navigatorState)

        assertThat(newNavigator.stateFlow.value).isEqualTo(navigationState)
    }

    @DisplayName(
        """
            When navigating between stacks in a multi stack navigator,
            Then the state is updated with new stack and destinations
        """
    )
    @Test
    fun `Navigate on a Multi stack navigator`() {
        val keyScreenPair = (0..3).map {
            NavigationKey() to object : NavigatorTest.TestScreen() {
                override val key: String = "BaseTestScreen-$it"
            }
        }

        val navigationStackEntries = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val screens = (0 until 10).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigator = Navigator()

        navigator.initialize(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        val newRootScreen = object : TestScreen() {
            override val key: String = "NewTestScreen"
        }

        val stackNavigationTransition = navigationSlideInHorizontally() to
                navigationSlideOutHorizontally()
        val screenTransition = navigationFadeIn() to navigationFadeOut()

        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[0].key)

        navigator.navigate(screens[0])
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        navigator.navigateToStack(navigationStackEntries[1].key, stackNavigationTransition)
        assertThat(navigator.stateFlow.value.transitionPair)
            .isEqualTo(stackNavigationTransition)
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[1].key)

        navigator.navigate(screens[1])
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        val customTransition = NavTransition(
            enter = navigationExpandIn(),
            exit = navigationShrinkOut()
        )
        navigator.navigate(screens[2], NavOptions(navTransition = customTransition))
        assertThat(navigator.stateFlow.value.transitionPair)
            .isEqualTo(customTransition.enter to customTransition.exit)

        navigator.navigate(screens[2], navOptions = NavOptions(singleTop = true))
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        navigator.navigate(screens[3])
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        navigator.navigate(screens[4])
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        navigator.popTo(screens[3].key)
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        navigator.navigateToStack(navigationStackEntries[2].key, stackNavigationTransition)
        assertThat(navigator.stateFlow.value.transitionPair)
            .isEqualTo(stackNavigationTransition)
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[2].key)

        navigator.setRoot(newRootScreen)
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        navigator.navigateToStack(navigationStackEntries[0].key, stackNavigationTransition)
        assertThat(navigator.stateFlow.value.transitionPair)
            .isEqualTo(stackNavigationTransition)
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[0].key)

        navigator.popToRoot()
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        navigator.navigateToStack(navigationStackEntries[1].key, stackNavigationTransition)
        assertThat(navigator.stateFlow.value.transitionPair)
            .isEqualTo(stackNavigationTransition)

        assertThat(navigator.canGoBack()).isTrue()
        navigator.popBackStack()
        assertThat(navigator.stateFlow.value.transitionPair).isEqualTo(screenTransition)

        val navigationState = navigator.stateFlow.value

        assertThat(navigationState.currentStackKey).isEqualTo(navigationStackEntries[1].key)
        assertThat(navigationState.navigationStacks.size).isEqualTo(4)

        assertThat(navigationState.navigationStacks[0].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[0].second)

        assertThat(navigationState.navigationStacks[1].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[1].second, screens[1], screens[2])

        assertThat(navigationState.navigationStacks[2].destinations.map { it.navigationNode })
            .containsExactly(newRootScreen)

        assertThat(navigationState.navigationStacks[3].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[3].second)
    }

    @DisplayName(
        """
            Given back stack strategy is BackToInitial,
            When navigating back from a stack with only a single destination,
            Then go back to initial stack
        """
    )
    @Test
    fun `Multi stack navigator back to initial stack strategy`() {
        val keyScreenPair = (0..3).map {
            NavigationKey() to object : NavigatorTest.TestScreen() {
                override val key: String = "BaseTestScreen-$it"
            }
        }

        val navigationStackEntries = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val screens = (0 until 4).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigator = Navigator()

        navigator.initialize(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.BackToInitialStack()
            )
        )

        navigator.navigate(screens[1])
        navigator.navigateToStack(navigationStackEntries[1].key)
        navigator.navigate(screens[2])
        navigator.navigate(screens[3])

        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[1].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()

        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[0].key)
        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()

        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[0].key)
        assertThat(navigator.canGoBack()).isFalse()
        assertThat(navigator.popBackStack()).isFalse()
    }

    @DisplayName(
        """
            Given back stack strategy is CrossStackHistory,
            When navigating back from a stack with only a single destination,
            Then go back to previous stack
        """
    )
    @Test
    fun `Multi stack navigator cross stack history stack strategy`() {
        val keyScreenPair = (0..3).map {
            NavigationKey() to object : NavigatorTest.TestScreen() {
                override val key: String = "BaseTestScreen-$it"
            }
        }

        val navigationStackEntries = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val screens = (0 until 4).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigator = Navigator()

        navigator.initialize(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.CrossStackHistory()
            )
        )

        navigator.navigate(screens[1])
        navigator.navigateToStack(navigationStackEntries[1].key)

        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[1].key)

        navigator.navigateToStack(navigationStackEntries[2].key)
        navigator.navigate(screens[2])
        navigator.navigate(screens[3])

        navigator.navigateToStack(navigationStackEntries[3].key)
        navigator.navigateToStack(navigationStackEntries[2].key)
        navigator.navigateToStack(navigationStackEntries[1].key)

        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[1].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[2].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[3].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[2].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[2].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[2].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[1].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[0].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackStack()).isTrue()
        assertThat(navigator.currentKey).isEqualTo(navigationStackEntries[0].key)

        assertThat(navigator.canGoBack()).isFalse()
        assertThat(navigator.popBackStack()).isFalse()
    }
}