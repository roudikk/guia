import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.util.TestNavigationKey
import com.roudikk.navigator.util.navigatorWithKey
import org.junit.Test

class NavigatorTest {

    @Test
    fun initialize_containsInitialKey() {
        val navigationKey = TestNavigationKey()
        val navigator = navigatorWithKey(navigationKey)

        assertThat(navigator.backStack).isEqualTo(listOf(navigationKey))
        assertThat(navigator.navigationEntries.size).isEqualTo(1)
        assertThat(navigator.navigationEntries.any { it.navigationKey == navigationKey }).isTrue()
        assertThat(navigator.navigationEntriesMap.size).isEqualTo(1)
        assertThat(navigator.navigationEntriesMap[navigationKey]).isNotNull()
    }

    @Test
    fun setBackstack_updatesState() {
        val navigationKey = TestNavigationKey()
        val navigator = navigatorWithKey(navigationKey)

        val newKeys = (0 until 3).map { TestNavigationKey() }

        navigator.setBackstack(newKeys)

        assertThat(navigator.backStack).isEqualTo(newKeys)
        assertThat(navigator.navigationEntries.size).isEqualTo(3)
        assertThat(navigator.navigationEntries.all { newKeys.contains(it.navigationKey) }).isTrue()
        assertThat(navigator.navigationEntriesMap.size).isEqualTo(3)
        newKeys.forEach { assertThat(navigator.navigationEntriesMap[it]).isNotNull() }
        assertThat(navigator.navigationEntriesMap[navigationKey]).isNull()
    }
}
