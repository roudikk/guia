@file:OptIn(ExperimentalNavigatorApi::class)

package com.roudikk.navigator

import android.os.Parcel
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import kotlin.to

class NavContainerTest {

    @get:Rule
    val rule = createComposeRule()

    open class TestScreen(val text: String) : Screen {
        override val key: String
            get() = text

        @Composable
        override fun AnimatedVisibilityScope.Content() {
            Text(text = text)
        }

        override fun describeContents(): Int = error("")
        override fun writeToParcel(p0: Parcel?, p1: Int) = error("")
    }

    open class TestBottomSheet(
        val text: String,
        override val bottomSheetOptions: BottomSheetOptions
    ) : BottomSheet {

        @Composable
        override fun AnimatedVisibilityScope.Content() {
            Text(text = text)
        }

        override fun describeContents(): Int = error("")
        override fun writeToParcel(p0: Parcel?, p1: Int) = error("")
    }

    @Test
    fun navHostInitialize_singleStack_showsFirstScreen() {
        val testScreen = TestScreen("Test Screen")

        rule.setContent {
            NavHost(
                Navigator.defaultKey to NavigationConfig.SingleStack(testScreen)
            ) {

                NavContainer()
            }
        }

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()
    }

    @Test
    fun navHostInitialize_multiStack_showsInitialStackFirstScreen() {
        val stackEntries = createStackEntries()

        rule.setContent {
            NavHost(
                Navigator.defaultKey to NavigationConfig.MultiStack(
                    entries = stackEntries,
                    initialStackKey = stackEntries[1].key,
                    backStackStrategy = BackStackStrategy.Default
                )
            ) {

                NavContainer()
            }
        }

        rule.onNodeWithText(stackEntries[1].initialNavigationNode.key).assertIsDisplayed()
    }

    @Test
    fun navHost_singleStack_navigate() {
        val testScreen = TestScreen("TestScreen")
        val testScreen2 = TestScreen("TestScreen2")

        rule.setContent {
            NavHost(
                Navigator.defaultKey to NavigationConfig.SingleStack(testScreen)
            ) {

                NavContainer()

                findNavigator(Navigator.defaultKey).navigate(testScreen2)
            }
        }

        rule.onNodeWithText(testScreen2.text).assertIsDisplayed()
    }

    @Test
    fun navHost_singleStack_popBackStack() {
        val testScreen = TestScreen("TestScreen")
        val testScreen2 = TestScreen("TestScreen2")

        rule.setContent {
            NavHost(
                Navigator.defaultKey to NavigationConfig.SingleStack(testScreen)
            ) {

                NavContainer()

                val navigator = findNavigator(Navigator.defaultKey)
                navigator.navigate(testScreen2)
                assert(navigator.popBackStack())
                assert(!navigator.popBackStack())
            }
        }

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()
    }

    @Test
    fun navHost_multiStack_navigate() {
        val testScreen = TestScreen("TestScreen")
        val testScreen2 = TestScreen("TestScreen2")

        val stackEntries = createStackEntries()

        lateinit var navigator: Navigator

        rule.setContent {
            NavHost(
                Navigator.defaultKey to NavigationConfig.MultiStack(
                    entries = stackEntries,
                    initialStackKey = stackEntries[0].key,
                    backStackStrategy = BackStackStrategy.Default
                )
            ) {
                navigator = findNavigator(Navigator.defaultKey)

                NavContainer()
            }
        }

        rule.onNodeWithText(stackEntries[0].initialNavigationNode.key).assertIsDisplayed()

        navigator.navigate(testScreen)

        rule.onNodeWithText(testScreen.key).assertIsDisplayed()

        navigator.navigateToStack(stackEntries[1].key)

        rule.onNodeWithText(stackEntries[1].initialNavigationNode.key).assertIsDisplayed()

        navigator.navigate(testScreen2)

        rule.onNodeWithText(testScreen2.key).assertIsDisplayed()
    }

    @Test
    fun navHost_bottomSheet_dismissOnHidden_navigate() {
        val testScreen = TestScreen("TestScreen")
        val bottomSheet = TestBottomSheet(
            text = "TestBottomSheet",
            bottomSheetOptions = BottomSheetOptions()
        )

        lateinit var navigator: Navigator

        rule.setContent {
            NavHost(
                Navigator.defaultKey to NavigationConfig.SingleStack(testScreen)
            ) {
                navigator = findNavigator(Navigator.defaultKey)

                NavContainer()
            }
        }

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()

        navigator.navigate(bottomSheet)

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()
        rule.onNodeWithText(bottomSheet.text).assertIsDisplayed()

        rule.onNodeWithTag("NavContainerBottomSheet")
            .performTouchInput { swipeDown() }

        rule.onNodeWithText(bottomSheet.text).assertDoesNotExist()
    }

    @Test
    fun navHost_bottomSheet_dismissOnHiddenFalse_navigate() {
        val testScreen = TestScreen("TestScreen")
        val bottomSheet = TestBottomSheet(
            text = "TestBottomSheet",
            bottomSheetOptions = BottomSheetOptions(dismissOnHidden = false)
        )

        lateinit var navigator: Navigator

        rule.setContent {
            NavHost(
                Navigator.defaultKey to NavigationConfig.SingleStack(testScreen)
            ) {
                navigator = findNavigator(Navigator.defaultKey)

                NavContainer()
            }
        }

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()

        navigator.navigate(bottomSheet)

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()
        rule.onNodeWithText(bottomSheet.text).assertIsDisplayed()

        rule.onNodeWithTag("NavContainerBottomSheet")
            .performTouchInput { swipeDown() }

        rule.onNodeWithText(bottomSheet.text).assertIsDisplayed()
    }

    private fun createStackEntries(): List<NavigationConfig.MultiStack.NavigationStackEntry> {
        val keyScreenPair = (0..3).map {
            val key = "BaseTestScreen-$it"
            NavigationKey() to object : TestScreen(key) {
                override val key: String = key
            }
        }

        return keyScreenPair.map { (key, screen) ->
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = key,
                initialNavigationNode = screen
            )
        }
    }
}