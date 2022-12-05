import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorRules
import com.roudikk.navigator.TestNavigationKey
import com.roudikk.navigator.TestSaveableStateHolder
import org.junit.Test

class NavigatorTest {

    @Test
    fun navigator_initialize_containsInitialKey() {
        val navigationKey = TestNavigationKey()
        val navigator = Navigator(
            initialKey = navigationKey,
            saveableStateHolder = TestSaveableStateHolder(),
            navigatorRules = NavigatorRules()
        )

        assertThat(navigator.backStack).isEqualTo(listOf(navigationKey))
        assertThat(navigator.destinations.size).isEqualTo(1)
        assertThat(navigator.destinations.any { it.navigationKey == navigationKey }).isTrue()
        assertThat(navigator.destinationsMap.size).isEqualTo(1)
        assertThat(navigator.destinationsMap[navigationKey]).isNotNull()
    }

    @Test
    fun navigator_setBackstack_updatesState() {
        val navigationKey = TestNavigationKey()
        val navigator = Navigator(
            initialKey = navigationKey,
            saveableStateHolder = TestSaveableStateHolder(),
            navigatorRules = NavigatorRules()
        )

        val newKeys = (0 until 3).map { TestNavigationKey() }

        navigator.setBackstack(newKeys)

        assertThat(navigator.backStack).isEqualTo(newKeys)
        assertThat(navigator.destinations.size).isEqualTo(3)
        assertThat(navigator.destinations.all { newKeys.contains(it.navigationKey) }).isTrue()
        assertThat(navigator.destinationsMap.size).isEqualTo(3)
        newKeys.forEach { assertThat(navigator.destinationsMap[it]).isNotNull() }
        assertThat(navigator.destinationsMap[navigationKey]).isNull()
    }

}
