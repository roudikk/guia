import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.core.entries
import com.roudikk.navigator.util.TestNavigationKey
import com.roudikk.navigator.util.navigatorWithKey
import org.junit.Test

class NavigatorTest {

    @Test
    fun initialize_containsInitialKey() {
        val navigationKey = TestNavigationKey()
        val navigator = navigatorWithKey(navigationKey)

        assertThat(navigator.backStackKeys).isEqualTo(listOf(navigationKey))
        assertThat(navigator.backStack.size).isEqualTo(1)
        assertThat(navigator.backStack.any { it.navigationKey == navigationKey }).isTrue()
    }

    @Test
    fun setBackstack_updatesState() {
        val navigationKey = TestNavigationKey()
        val navigator = navigatorWithKey(navigationKey)

        val newKeys = (0 until 3).map { TestNavigationKey() }

        navigator.setBackstack(newKeys.entries())

        assertThat(navigator.backStackKeys).isEqualTo(newKeys)
        assertThat(navigator.backStack.size).isEqualTo(3)
        assertThat(navigator.backStack.all { newKeys.contains(it.navigationKey) }).isTrue()
    }
}
