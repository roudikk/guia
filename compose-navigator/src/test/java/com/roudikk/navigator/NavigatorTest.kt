import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.animation.NavigationTransition
import com.roudikk.navigator.animation.to
import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.entries
import com.roudikk.navigator.core.entry
import com.roudikk.navigator.core.navigationNode
import com.roudikk.navigator.core.transition
import com.roudikk.navigator.util.TestKey
import com.roudikk.navigator.util.TestKey2
import com.roudikk.navigator.util.TestKey3
import com.roudikk.navigator.util.TestNavigationKey
import com.roudikk.navigator.util.entryForKey
import com.roudikk.navigator.util.testNavigator
import org.junit.Assert.assertThrows
import org.junit.Test

class NavigatorTest {

    @Test
    fun initialize_containsInitialKey() {
        val navigationKey = TestNavigationKey()
        val navigator = testNavigator(navigationKey)

        assertThat(navigator.backstackKeys).isEqualTo(listOf(navigationKey))
        assertThat(navigator.backstack.size).isEqualTo(1)
        assertThat(navigator.backstack.any { it.navigationKey == navigationKey }).isTrue()
    }

    @Test
    fun setBackstack_updatesState() {
        val navigationKey = TestNavigationKey()
        val navigator = testNavigator(navigationKey)

        val newKeys = (0 until 3).map { TestNavigationKey() }

        navigator.setBackstack(newKeys.entries())

        assertThat(navigator.backstackKeys).isEqualTo(newKeys)
        assertThat(navigator.backstack.size).isEqualTo(3)
        assertThat(navigator.backstack.all { newKeys.contains(it.navigationKey) }).isTrue()
    }

    @Test
    fun navigationConfig_presentations_returnsNode() {
        val testKey = TestKey()
        val navigationKey = TestNavigationKey()
        val navigator = testNavigator(
            navigationKey = navigationKey,
            navigatorConfig = NavigatorConfigBuilder()
                .apply {
                    screen<TestNavigationKey> { }
                }
                .build()
        )

        navigator.setBackstack(listOf(navigationKey, testKey).entries())

        val navigationNode = navigator.navigationNode(navigator.entryForKey(navigationKey))
        assertThat(navigationNode).isInstanceOf(Screen::class.java)
        assertThat(navigator.navigationNode(navigator.entryForKey(navigationKey)))
            .isEqualTo(navigationNode)
        assertThrows(IllegalStateException::class.java) {
            navigator.navigationNode(navigator.entryForKey(testKey))
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Test
    fun navigationConfig_transitions_returnsProperTransition() {
        val testKey = TestKey()
        val testKey2 = TestKey2()
        val testKey3 = TestKey3()
        val navigationKey = TestNavigationKey()
        val navigationKeyTransition = NavigationTransition(
            enterExit = fadeIn() to fadeOut(),
            popEnterExit = fadeIn() to fadeOut()
        )
        val testKeyTransition = NavigationTransition(
            enterExit = scaleIn() to scaleOut(),
            popEnterExit = scaleIn() to scaleOut()
        )
        val navigator = testNavigator(
            navigationKey = navigationKey,
            navigatorConfig = NavigatorConfigBuilder()
                .apply {
                    screen<TestNavigationKey> { }
                    screen<TestKey> { }
                    screen<TestKey2> { }
                    screen<TestKey3> { }
                    transition<TestNavigationKey> { -> navigationKeyTransition }
                    transition<TestKey> { -> testKeyTransition }
                    defaultTransition { _, newKey, _ ->
                        if (newKey is TestKey2) {
                            EnterExitTransition.None
                        } else {
                            fadeIn() to fadeOut()
                        }
                    }
                }
                .build()
        )

        navigator.setBackstack(navigator.backstack + testKey.entry())
        assertThat(navigator.transition<Screen>().enter).isEqualTo(testKeyTransition.enterExit.enter)
        assertThat(navigator.transition<Screen>().exit).isEqualTo(testKeyTransition.enterExit.exit)

        navigator.setBackstack(navigator.backstack.dropLast(1))
        assertThat(navigator.transition<Screen>().enter).isEqualTo(navigationKeyTransition.popEnterExit.enter)
        assertThat(navigator.transition<Screen>().exit).isEqualTo(navigationKeyTransition.popEnterExit.exit)

        navigator.setBackstack(navigator.backstack + testKey2.entry())
        assertThat(navigator.transition<Screen>().enter).isEqualTo(EnterTransition.None)
        assertThat(navigator.transition<Screen>().exit).isEqualTo(ExitTransition.None)

        navigator.setBackstack(navigator.backstack + testKey3.entry())
        assertThat(navigator.transition<Screen>().enter).isEqualTo(fadeIn())
        assertThat(navigator.transition<Screen>().exit).isEqualTo(fadeOut())
    }
}
