@file:OptIn(
    ExperimentalCoroutinesApi::class,
    DelicateCoroutinesApi::class,
    ExperimentalCoroutinesApi::class
)

package com.roudikk.navigator

import android.annotation.SuppressLint
import android.os.Parcel
import androidx.compose.runtime.Composable
import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.animation.NavEnterTransition
import com.roudikk.navigator.animation.NavExitTransition
import com.roudikk.navigator.animation.NavTransition
import com.roudikk.navigator.animation.to
import com.roudikk.navigator.animation.transitions.navExpandIn
import com.roudikk.navigator.animation.transitions.navFadeIn
import com.roudikk.navigator.animation.transitions.navFadeOut
import com.roudikk.navigator.animation.transitions.navShrinkOut
import com.roudikk.navigator.animation.transitions.navSlideInHorizontally
import com.roudikk.navigator.animation.transitions.navSlideOutHorizontally
import com.roudikk.navigator.core.BackStackStrategy
import com.roudikk.navigator.core.DefaultStackKey
import com.roudikk.navigator.core.NavHistoryEntry
import com.roudikk.navigator.core.NavigationConfig
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.StackKey
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
        @Suppress("TestFunctionName")
        @SuppressLint("ComposableNaming")
        @Composable
        override fun Content() = error("")
        override fun describeContents(): Int = error("")
        override fun writeToParcel(dest: Parcel, flags: Int) = error("")
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

        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screen
            )
        )

        val navigationState = navigator.currentState

        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
        assertThat(
            navigationState.currentStack.destinations.any {
                it.navigationNode == screen
            }
        ).isTrue()
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
            StackKey() to object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigationStacks = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val navigator = Navigator(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStacks,
                initialStackKey = navigationStacks[1].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        val navigationState = navigator.currentState

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
            StackKey() to object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigationStacks = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        assertThrows<IllegalStateException> {
            Navigator(
                navigationConfig = NavigationConfig.MultiStack(
                    entries = navigationStacks,
                    initialStackKey = DefaultStackKey,
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

        val screen = object : TestScreen() {
            override val key: String = "TestScreen"
        }

        val newScreen = object : TestScreen() {
            override val key: String = "NewTestScreen"
        }

        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screen
            )
        )

        val transition = NavTransition.None

        navigator.navigate(newScreen, transition)

        val navigationState = navigator.currentState
        val destinations = navigationState.currentStack.destinations

        assertThat(navigator.navHistory).containsExactly(
            NavHistoryEntry(DefaultStackKey, screen.key, destinations[0].id),
            NavHistoryEntry(DefaultStackKey, newScreen.key, destinations[1].id)
        )
        assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(2)
        assertThat(navigationState.currentStack.destinations.last().navigationNode)
            .isEqualTo(newScreen)
        assertThat(navigationState.currentStack.destinations.last().transition).isEqualTo(transition)
        assertThat(navigationState.transition.enter).isEqualTo(NavTransition.None.enter)
        assertThat(navigationState.transition.exit).isEqualTo(NavTransition.None.exit)
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

        val screen = object : TestScreen() {
            override val key: String = "TestScreen"
        }

        val newScreen = object : TestScreen() {
            override val key: String = "NewTestScreen"
        }

        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screen
            )
        )

        val transition = NavTransition.None

        navigator.navigate(newScreen, transition)
        var destinations = navigator.currentState.currentStack.destinations

        assertThat(navigator.navHistory).containsExactly(
            NavHistoryEntry(DefaultStackKey, screen.key, destinations[0].id),
            NavHistoryEntry(DefaultStackKey, newScreen.key, destinations[1].id)
        )

        assertThat(navigator.canGoBack()).isTrue()

        val popBackStack = navigator.popBackstack()

        val navigationState = navigator.currentState
        destinations = navigationState.currentStack.destinations

        assertThat(popBackStack).isTrue()
        assertThat(navigator.navHistory)
            .containsExactly(NavHistoryEntry(DefaultStackKey, screen.key, destinations[0].id))
        assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.last().navigationNode).isEqualTo(screen)
        assertThat(navigationState.currentStack.destinations.last().transition.popEnterExit)
            .isEqualTo(NavTransition.None.enter to NavTransition.None.exit)
        assertThat(navigationState.transition.enter).isEqualTo(NavTransition.None.enter)
        assertThat(navigationState.transition.exit).isEqualTo(NavTransition.None.exit)
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

        val screen = object : TestScreen() {
            override val key: String = "TestScreen"
        }

        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screen
            )
        )

        val navigationState = navigator.currentState

        val popBackStack = navigator.popBackstack()

        val newNavigationState = navigator.currentState
        val destinations = newNavigationState.currentStack.destinations

        assertThat(popBackStack).isFalse()
        assertThat(navigator.navHistory)
            .containsExactly(NavHistoryEntry(DefaultStackKey, screen.key, destinations[0].id))
        assertThat(navigationState).isEqualTo(newNavigationState)
        assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.last().navigationNode).isEqualTo(screen)
        assertThat(navigationState.currentStack.destinations.last().transition.popEnterExit)
            .isEqualTo(NavTransition.None.enter to NavTransition.None.exit)
        assertThat(navigationState.transition.enter).isEqualTo(NavTransition.None.enter)
        assertThat(navigationState.transition.exit).isEqualTo(NavTransition.None.exit)
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

                val screens = (0..10).map {
                    if (useGenericPopTo && it == 3) {
                        TestScreen()
                    } else {
                        object : TestScreen() {
                            override val key: String = "TestScreen-$it"
                        }
                    }
                }

                val navigator = Navigator(
                    navigationConfig = NavigationConfig.SingleStack(
                        initialNavigationNode = screens[0]
                    )
                )

                val poppedToIndex = if (inclusive) 2 else 3

                (screens - screens[0]).forEach(navigator::navigate)

                var destinations = navigator.currentState.currentStack.destinations

                assertThat(navigator.navHistory).containsExactlyElementsIn(
                    screens.mapIndexed { index, screen ->
                        NavHistoryEntry(DefaultStackKey, screen.key, destinations[index].id)
                    }
                )

                val popTo = if (useGenericPopTo) {
                    navigator.popTo<TestScreen>(inclusive = inclusive)
                } else {
                    navigator.popTo(screens[3].key, inclusive = inclusive)
                }

                assertThat(popTo).isTrue()

                val navigationState = navigator.currentState
                destinations = navigationState.currentStack.destinations

                assertThat(navigator.navHistory).containsExactlyElementsIn(
                    screens.subList(0, poppedToIndex + 1).mapIndexed { index, screen ->
                        NavHistoryEntry(DefaultStackKey, screen.key, destinations[index].id)
                    }
                )
                assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
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
                val screens = (0..10).map {
                    object : TestScreen() {
                        override val key: String = "TestScreen-$it"
                    }
                }

                val navigator = Navigator(
                    navigationConfig = NavigationConfig.SingleStack(
                        initialNavigationNode = screens[0]
                    )
                )

                (screens - screens[0]).forEach(navigator::navigate)

                val destinations = navigator.currentState.currentStack.destinations

                assertThat(navigator.navHistory).containsExactlyElementsIn(
                    screens.mapIndexed { index, screen ->
                        NavHistoryEntry(DefaultStackKey, screen.key, destinations[index].id)
                    }
                )

                val popTo = if (useGenericPopTo) {
                    navigator.popTo<TestScreen>()
                } else {
                    navigator.popTo(key = "None existing key", inclusive = inclusive)
                }

                assertThat(popTo).isFalse()

                val navigationState = navigator.currentState

                assertThat(navigator.navHistory).containsExactlyElementsIn(
                    screens.mapIndexed { index, screen ->
                        NavHistoryEntry(DefaultStackKey, screen.key, destinations[index].id)
                    }
                )
                assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
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

                Navigator(
                    navigationConfig = NavigationConfig.SingleStack(
                        initialNavigationNode = screens[0]
                    )
                )

                (screens - screens[0]).forEach(navigator::navigate)

                var destinations = navigator.currentState.currentStack.destinations

                assertThat(navigator.navHistory).containsExactlyElementsIn(
                    screens.mapIndexed { index, screen ->
                        NavHistoryEntry(DefaultStackKey, screen.key, destinations[index].id)
                    }
                )

                navigator.popToRoot()

                val navigationState = navigator.currentState
                destinations = navigationState.currentStack.destinations

                assertThat(navigator.navHistory).containsExactly(
                    NavHistoryEntry(DefaultStackKey, screens[0].key, destinations[0].id)
                )
                assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
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
        val screens = (0 until 10).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screens[0]
            )
        )

        (screens - screens[0]).forEach(navigator::navigate)

        var destinations = navigator.currentState.currentStack.destinations

        assertThat(navigator.navHistory).containsExactlyElementsIn(
            screens.mapIndexed { index, screen ->
                NavHistoryEntry(DefaultStackKey, screen.key, destinations[index].id)
            }
        )

        val newRootScreen = object : TestScreen() {
            override val key: String = "NewRootScreen"
        }

        val transition = NavTransition(
            enter = navFadeIn(),
            exit = navFadeOut(),
            popEnter = navFadeIn(),
            popExit = navFadeOut()
        )

        navigator.setRoot(newRootScreen, transition)

        val navigationState = navigator.currentState
        destinations = navigationState.currentStack.destinations

        assertThat(navigator.navHistory).containsExactly(
            NavHistoryEntry(DefaultStackKey, newRootScreen.key, destinations[0].id)
        )
        assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.last().navigationNode)
            .isEqualTo(newRootScreen)
        assertThat(navigationState.transition.enter).isEqualTo(transition.enter)
        assertThat(navigationState.transition.exit).isEqualTo(transition.exit)
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

                Navigator(
                    navigationConfig = NavigationConfig.SingleStack(
                        initialNavigationNode = testScreen
                    )
                )

                val navigationState = navigator.currentState

                assertThat(navigationState.overrideBackPress).isTrue()

                navigator.overrideBackPress(overrideBackPress)

                val newNavigatorState = navigator.currentState

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

        Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screens[0]
            )
        )

        screens.forEach(navigator::navigate)

        navigator.sendResult(screens[0].key to "Hello!")
        assertThat(navigator.results(screens[0].key).first()).isEqualTo("Hello!")

        navigator.sendResult<TestScreen>(1)
        assertThat(navigator.results<TestScreen>().first()).isEqualTo(1)
    }

    @Test
    fun `Navigate to stack, single stack, throws exception`() {
        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = TestScreen()
            )
        )
        assertThrows<IllegalArgumentException> { navigator.navigateToStack(StackKey()) }
    }

    @Test
    fun `Navigate to stack, multi stack, invalid stack key`() {
        val keyScreenPair = (0..3).map {
            StackKey() to object : TestScreen() {
                override val key: String = "BaseTestScreen-$it"
            }
        }

        val navigationStackEntries = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val navigator = Navigator(
            NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        assertThrows<IllegalStateException> { navigator.navigateToStack(StackKey()) }
    }

    @Test
    fun `Navigate to stack, multi stack, existing stack key`() {
        val keyScreenPair = (0..3).map {
            StackKey() to object : TestScreen() {
                override val key: String = "BaseTestScreen-$it"
            }
        }

        val navigationStackEntries = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val navigator = Navigator(
            NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        navigator.navigateToStack(keyScreenPair[1].first)
        assertThat(navigator.currentStackKey).isEqualTo(keyScreenPair[1].first)
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
            StackKey() to object : TestScreen() {
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

        val navigator = Navigator(
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
        navigator.singleTop(screens[2])
        navigator.navigate(screens[3])
        navigator.navigate(screens[4])
        navigator.popTo(screens[3].key)
        navigator.navigateToStack(navigationStackEntries[2].key)
        navigator.setRoot(newRootScreen)
        navigator.navigateToStack(navigationStackEntries[0].key)
        navigator.popToRoot()
        navigator.navigateToStack(navigationStackEntries[1].key)
        navigator.popBackstack()

        val navigationState = navigator.currentState

        val navigatorState = navigator.save()

        val newNavigator = Navigator(navigatorState.navigationConfig)
        newNavigator.restore(navigatorState)

        assertThat(newNavigator.currentState).isEqualTo(navigationState)
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
            StackKey() to object : TestScreen() {
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

        val navigator = Navigator(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        val newRootScreen = object : TestScreen() {
            override val key: String = "NewTestScreen"
        }

        val stackNavigationTransition = navSlideInHorizontally() to navSlideOutHorizontally()
        val screenTransition = NavTransition(
            enter = navFadeIn(),
            exit = navFadeOut(),
            popEnter = navFadeIn(),
            popExit = navFadeOut()
        )
        val screenEnterExitTransition = navFadeIn() to navFadeOut()

        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[0].key)

        navigator.navigate(screens[0])
        assertThat(navigator.currentState.transition).isEqualTo(
            NavTransition.None.enter to NavTransition.None.exit
        )

        navigator.navigateToStack(navigationStackEntries[1].key, stackNavigationTransition)
        assertThat(navigator.currentState.transition)
            .isEqualTo(stackNavigationTransition)
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[1].key)

        navigator.navigate(screens[1], transition = screenTransition)
        assertThat(navigator.currentState.transition).isEqualTo(screenEnterExitTransition)

        val customTransition = NavTransition(
            enter = navExpandIn(),
            exit = navShrinkOut(),
            popEnter = navFadeIn(),
            popExit = navFadeOut()
        )
        navigator.navigate(screens[2], transition = customTransition)
        assertThat(navigator.currentState.transition)
            .isEqualTo(customTransition.enter to customTransition.exit)

        navigator.singleInstance(screens[2], useExistingInstance = false)
        assertThat(navigator.currentState.transition)
            .isEqualTo(NavEnterTransition.None to NavExitTransition.None)

        navigator.navigate(screens[3], transition = screenTransition)
        assertThat(navigator.currentState.transition).isEqualTo(screenEnterExitTransition)

        navigator.navigate(screens[4], transition = screenTransition)
        assertThat(navigator.currentState.transition).isEqualTo(screenEnterExitTransition)

        navigator.navigate(screens[3], transition = screenTransition)
        assertThat(navigator.currentState.transition).isEqualTo(screenEnterExitTransition)

        navigator.navigateToStack(navigationStackEntries[2].key, stackNavigationTransition)
        assertThat(navigator.currentState.transition)
            .isEqualTo(stackNavigationTransition)
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[2].key)

        navigator.setRoot(newRootScreen)
        assertThat(navigator.currentState.transition).isEqualTo(
            NavTransition.None.enter to NavTransition.None.exit
        )

        navigator.navigateToStack(navigationStackEntries[0].key, stackNavigationTransition)
        assertThat(navigator.currentState.transition)
            .isEqualTo(stackNavigationTransition)
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[0].key)

        navigator.popToRoot()
        assertThat(navigator.currentState.transition).isEqualTo(
            NavTransition.None.enter to NavTransition.None.exit
        )

        navigator.navigateToStack(navigationStackEntries[1].key, stackNavigationTransition)
        assertThat(navigator.currentState.transition)
            .isEqualTo(stackNavigationTransition)

        assertThat(navigator.canGoBack()).isTrue()
        navigator.popTo(screens[2].key, inclusive = false)
        assertThat(navigator.currentState.transition).isEqualTo(screenEnterExitTransition)

        val navigationState = navigator.currentState

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
            StackKey() to object : TestScreen() {
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

        val navigator = Navigator(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.BackToInitialStack
            )
        )

        navigator.navigate(screens[1])
        navigator.navigateToStack(navigationStackEntries[1].key)
        navigator.navigate(screens[2])
        navigator.navigate(screens[3])

        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[1].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()

        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[0].key)
        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()

        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[0].key)
        assertThat(navigator.canGoBack()).isFalse()
        assertThat(navigator.popBackstack()).isFalse()
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
            StackKey() to object : TestScreen() {
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

        val navigator = Navigator(
            navigationConfig = NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.CrossStackHistory
            )
        )

        navigator.navigate(screens[1])
        navigator.navigateToStack(navigationStackEntries[1].key)

        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[1].key)

        navigator.navigateToStack(navigationStackEntries[2].key)
        navigator.navigate(screens[2])
        navigator.navigate(screens[3])

        navigator.navigateToStack(navigationStackEntries[3].key)
        navigator.navigateToStack(navigationStackEntries[2].key)
        navigator.navigateToStack(navigationStackEntries[1].key)

        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[1].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[2].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[3].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[2].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[2].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[2].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[1].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[0].key)

        assertThat(navigator.canGoBack()).isTrue()
        assertThat(navigator.popBackstack()).isTrue()
        assertThat(navigator.currentStackKey).isEqualTo(navigationStackEntries[0].key)

        assertThat(navigator.canGoBack()).isFalse()
        assertThat(navigator.popBackstack()).isFalse()
    }

    @DisplayName(
        """
            Given single stack navigator
            When calling replaceLast
            Then the last navigation node should be replaced
        """
    )
    @Test
    fun `replaceLast replaces last navigation node`() {
        val screens = (0 until 3).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screens[0]
            )
        )
        (screens - screens[0]).forEach(navigator::navigate)

        val screen = TestScreen()
        navigator.replaceLast(screen)

        assertThat(
            navigator.navHistory.all {
                listOf(screens[0].key, screens[1].key, screen.key)
                    .contains(it.navigationNodeKey)
            }
        ).isTrue()

        assertThat(
            navigator.navHistory.any {
                it.navigationNodeKey == screens[2].key
            }
        ).isFalse()

        val navigationState = navigator.currentState

        assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(3)
        assertThat(navigationState.currentStack.destinations.last().navigationNode)
            .isEqualTo(screen)

        navigator.popToRoot()
        navigator.replaceLast(screen)

        assertThat(navigator.navHistory.size).isEqualTo(1)
        assertThat(navigator.navHistory[0].navigationNodeKey)
            .isEqualTo(screen.key)
    }

    @DisplayName(
        """
            Given single stack navigator
            When calling replaceUpTo
            Then all navigation nodes not matching predicate should be removed
        """
    )
    @Test
    fun `replaceUpTo replaces nodes matching predicate`() {
        val screens = (0 until 5).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screens[0]
            )
        )
        (screens - screens[0]).forEach(navigator::navigate)

        val screen = TestScreen()
        navigator.replaceUpTo(screen, inclusive = false) { it.key == "TestScreen-1" }

        assertThat(
            navigator.navHistory.all {
                listOf(screens[0].key, screens[1].key, screen.key)
                    .contains(it.navigationNodeKey)
            }
        ).isTrue()

        assertThat(
            navigator.navHistory.none {
                listOf(screens[2].key, screens[3].key, screens[4].key)
                    .contains(it.navigationNodeKey)
            }
        ).isTrue()

        val navigationState = navigator.currentState

        assertThat(navigationState.currentStackKey).isEqualTo(DefaultStackKey)
        assertThat(navigationState.navigationStacks.size).isEqualTo(1)
        assertThat(navigationState.currentStack.destinations.size).isEqualTo(3)
        assertThat(navigationState.currentStack.destinations.last().navigationNode)
            .isEqualTo(screen)

        navigator.navigate(screens[2])
        navigator.navigate(screens[3])
        navigator.replaceUpTo<TestScreen>(screens[4], inclusive = true)

        assertThat(
            navigator.navHistory.all {
                listOf(screens[0].key, screens[1].key, screens[4].key)
                    .contains(it.navigationNodeKey)
            }
        ).isTrue()

        assertThat(
            navigator.navHistory.none {
                listOf(screens[2].key, screens[3].key, screen)
                    .contains(it.navigationNodeKey)
            }
        ).isTrue()
    }

    @DisplayName(
        """
            Given single stack navigator
            AND there is an existing navigation node
            When calling moveToTop
            Then navigation node should go to top and return true
            GIVEN there is no existing navigation node
            When calling moveToTop
            Then return false
        """
    )
    @Test
    fun `moveToTop moves existing destination to top`() {
        val screens = (0 until 5).map {
            object : TestScreen() {
                override val key: String = "TestScreen-$it"
            }
        }

        val navigator = Navigator(
            navigationConfig = NavigationConfig.SingleStack(
                initialNavigationNode = screens[0]
            )
        )

        screens.subList(1, 3).forEach(navigator::navigate)

        assertThat(navigator.moveToTop<TestScreen>()).isFalse()
        assertThat(navigator.moveToTop { it == screens[3] }).isFalse()
        assertThat(navigator.moveToTop { it == screens[1] }).isTrue()
        assertThat(navigator.currentState.currentStack.destinations.map { it.navigationNode })
            .containsExactly(screens[0], screens[2], screens[1])

        val screen = TestScreen()
        navigator.navigate(screen)
        navigator.navigate(screens[3])
        navigator.navigate(screens[4])
        assertThat(navigator.moveToTop<TestScreen>()).isTrue()
        assertThat(navigator.currentState.currentStack.destinations.map { it.navigationNode })
            .containsExactly(screens[0], screens[2], screens[1], screens[3], screens[4], screen)

        val existingKeyScreen = object : TestScreen() {
            override val key: String = "TestScreen-1"
        }
        navigator.navigate(existingKeyScreen)
        assertThat(navigator.moveToTop(matchLast = false) { it.key == "TestScreen-1" }).isTrue()

        assertThat(navigator.currentState.currentStack.destinations.map { it.navigationNode })
            .containsExactly(
                screens[0],
                screens[2],
                screens[3],
                screens[4],
                screen,
                existingKeyScreen,
                screens[1]
            )
    }

    @Test
    fun `Single Stack Navigator singleInstance uses a single instance of a navigation node`() {
        val screens = (0 until 5).map {
            object : TestScreen() {
                override val key: String = "key-$it"
            }
        }

        val navigator = Navigator(navigationConfig = NavigationConfig.SingleStack(screens[0]))
        (screens - screens[0]).forEach(navigator::navigate)

        val screen = object : TestScreen() {
            override val key: String = "key-2"
        }
        navigator.singleInstance(screen, useExistingInstance = false)

        assertThat(navigator.currentState.navigationStacks.size).isEqualTo(1)
        assertThat(navigator.currentState.currentStack.destinations.map { it.navigationNode })
            .containsExactly(screens[0], screens[1], screens[3], screens[4], screen)

        val existingScreen = object : TestScreen() {
            override val key: String = "key-1"
        }
        navigator.singleInstance(existingScreen, useExistingInstance = true)
        assertThat(navigator.currentState.currentStack.destinations.map { it.navigationNode })
            .containsExactly(screens[0], screens[3], screens[4], screen, screens[1])
    }

    @Test
    fun `Multi Stack Navigator singleInstance uses a single instance of a navigation node`() {
        val keyScreenPair = (0..3).map {
            StackKey() to object : TestScreen() {
                override val key: String = "BaseTestScreen-$it"
            }
        }

        val navigationStackEntries = keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }

        val navigator = Navigator(
            NavigationConfig.MultiStack(
                entries = navigationStackEntries,
                initialStackKey = navigationStackEntries[0].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        val screens = (0 until 5).map {
            object : TestScreen() {
                override val key: String = "key-$it"
            }
        }
        screens.forEach(navigator::navigate)

        val screen = object : TestScreen() {
            override val key: String = "key-2"
        }
        navigator.navigateToStack(keyScreenPair[1].first)
        navigator.navigate(screen)
        navigator.navigateToStack(keyScreenPair[0].first)
        navigator.singleInstance(screen, useExistingInstance = false)

        assertThat(navigator.currentState.navigationStacks.size).isEqualTo(4)
        assertThat(navigator.currentState.currentStack.destinations.map { it.navigationNode })
            .containsExactly(
                keyScreenPair[0].second, screens[0], screens[1], screens[3], screens[4], screen
            )
        assertThat(navigator.currentState.navigationStacks[1].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[1].second, screen)
        assertThat(navigator.currentState.navigationStacks[2].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[2].second)
        assertThat(navigator.currentState.navigationStacks[3].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[3].second)

        val existingScreen = object : TestScreen() {
            override val key: String = "key-1"
        }
        navigator.singleInstance(existingScreen, useExistingInstance = true)
        (keyScreenPair - keyScreenPair[0]).map { it.first }.forEach {
            navigator.navigateToStack(it)
            navigator.navigate(existingScreen)
        }
        navigator.navigateToStack(keyScreenPair[0].first)
        assertThat(navigator.currentState.currentStack.destinations.map { it.navigationNode })
            .containsExactly(
                keyScreenPair[0].second, screens[0], screens[3], screens[4], screen, screens[1]
            )
        assertThat(navigator.currentState.navigationStacks[1].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[1].second, screen, existingScreen)
        assertThat(navigator.currentState.navigationStacks[2].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[2].second, existingScreen)
        assertThat(navigator.currentState.navigationStacks[3].destinations.map { it.navigationNode })
            .containsExactly(keyScreenPair[3].second, existingScreen)
    }

    @Test
    fun `Navigator overrideBackPress updates back press state`() {
        val navigator = Navigator()
        navigator.overrideBackPress(true)
        assertThat(navigator.currentState.overrideBackPress).isTrue()
        navigator.overrideBackPress(false)
        assertThat(navigator.currentState.overrideBackPress).isFalse()
    }
}
