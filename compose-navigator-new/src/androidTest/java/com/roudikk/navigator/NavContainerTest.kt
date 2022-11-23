package com.roudikk.navigator

import android.os.Parcel
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.BackStackStrategy
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.BottomSheetOptions
import com.roudikk.navigator.core.NavigationConfig
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.StackKey
import org.junit.Rule
import org.junit.Test

class NavContainerTest {

    @get:Rule
    val rule = createComposeRule()

    open class TestScreen(val text: String) : Screen {
        override val key: String
            get() = text

        @Suppress("TestFunctionName")
        @Composable
        override fun Content() {
            Text(text = text)
        }

        override fun describeContents(): Int = error("")
        override fun writeToParcel(dest: Parcel, flags: Int) = error("")
    }

    open class TestBottomSheet(
        val text: String,
        override val bottomSheetOptions: BottomSheetOptions
    ) : BottomSheet {

        @Suppress("TestFunctionName")
        @Composable
        override fun Content() {
            Text(text = text)
        }

        override fun describeContents(): Int = error("")
        override fun writeToParcel(dest: Parcel, flags: Int) = error("")
    }

    @Test
    fun navHostInitialize_singleStack_showsFirstScreen() {
        val testScreen = TestScreen("Test Screen")

        rule.setContent {
            NavContainer(navigator = rememberNavigator(NavigationConfig.SingleStack(testScreen)))
        }

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()
    }

    @Test
    fun navHostInitialize_multiStack_showsInitialStackFirstScreen() {
        val stackEntries = createStackEntries()

        rule.setContent {
            NavContainer(
                navigator = rememberNavigator(
                    NavigationConfig.MultiStack(
                        entries = stackEntries,
                        initialStackKey = stackEntries[1].key,
                        backStackStrategy = BackStackStrategy.Default
                    )
                )
            )
        }

        rule.onNodeWithText(stackEntries[1].initialNavigationNode.key).assertIsDisplayed()
    }

    @Test
    fun navHost_singleStack_navigate() {
        val testScreen = TestScreen("TestScreen")
        val testScreen2 = TestScreen("TestScreen2")

        rule.setContent {
            val navigator = rememberNavigator(testScreen)
            NavContainer(navigator = navigator)
            navigator.navigate(testScreen2)
        }

        rule.onNodeWithText(testScreen2.text).assertIsDisplayed()
    }

    @Test
    fun navHost_singleStack_popBackStack() {
        val testScreen = TestScreen("TestScreen")
        val testScreen2 = TestScreen("TestScreen2")

        rule.setContent {
            val navigator = rememberNavigator(testScreen)

            NavContainer(navigator = navigator)

            navigator.navigate(testScreen2)
            assert(navigator.popBackStack())
            assert(!navigator.popBackStack())
        }

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()
    }

    @Test
    fun navHost_multiStack_navigate() {
        val testScreen = TestScreen("TestScreen")
        val testScreen2 = TestScreen("TestScreen2")

        val stackEntries = createStackEntries()

        val navigator = Navigator(
            NavigationConfig.MultiStack(
                entries = stackEntries,
                initialStackKey = stackEntries[0].key,
                backStackStrategy = BackStackStrategy.Default
            )
        )

        rule.setContent {
            NavContainer(navigator = navigator)
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

        val navigator = Navigator(NavigationConfig.SingleStack(testScreen))

        rule.setContent {
            NavContainer(navigator = navigator)
        }

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()

        navigator.navigate(bottomSheet)

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()
        rule.onNodeWithText(bottomSheet.text).assertIsDisplayed()

        rule.onNodeWithTag("BottomSheetContainer")
            .performTouchInput { click() }

        rule.onNodeWithText(bottomSheet.text).assertDoesNotExist()
    }

    @Test
    fun navHost_bottomSheet_dismissOnHiddenFalse_navigate() {
        val testScreen = TestScreen("TestScreen")
        val bottomSheet = TestBottomSheet(
            text = "TestBottomSheet",
            bottomSheetOptions = BottomSheetOptions(
                confirmStateChange = { false }
            )
        )
        val navigator = Navigator(NavigationConfig.SingleStack(testScreen))

        rule.setContent {
            NavContainer(navigator = navigator)
        }

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()

        navigator.navigate(bottomSheet)

        rule.onNodeWithText(testScreen.text).assertIsDisplayed()
        rule.onNodeWithText(bottomSheet.text).assertIsDisplayed()

        rule.onNodeWithTag("BottomSheetContainer")
            .performTouchInput { click() }

        rule.onNodeWithText(bottomSheet.text).assertIsDisplayed()
    }

    private fun createStackEntries(): List<NavigationConfig.MultiStack.NavigationStackEntry> {
        val keyScreenPair = (0..3).map {
            val key = "BaseTestScreen-$it"
            StackKey() to object : TestScreen(key) {
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
