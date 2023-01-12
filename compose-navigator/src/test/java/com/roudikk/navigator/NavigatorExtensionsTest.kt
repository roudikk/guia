package com.roudikk.navigator

import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.extensions.Match
import com.roudikk.navigator.extensions.currentEntry
import com.roudikk.navigator.extensions.currentKey
import com.roudikk.navigator.extensions.moveToTop
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popTo
import com.roudikk.navigator.extensions.popToRoot
import com.roudikk.navigator.extensions.removeAll
import com.roudikk.navigator.extensions.replaceLast
import com.roudikk.navigator.extensions.replaceUpTo
import com.roudikk.navigator.extensions.setRoot
import com.roudikk.navigator.extensions.singleInstance
import com.roudikk.navigator.extensions.singleTop
import com.roudikk.navigator.util.TestDataKey
import com.roudikk.navigator.util.TestKey
import com.roudikk.navigator.util.TestKey2
import com.roudikk.navigator.util.TestKey3
import com.roudikk.navigator.util.TestNavigationKey
import com.roudikk.navigator.util.assertKeys
import com.roudikk.navigator.util.testNavigator
import org.junit.Test

class NavigatorExtensionsTest {

    @Test
    fun navigator_currentEntry_returnsEntry() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        assertThat(navigator.currentEntry.navigationKey).isEqualTo(initialKey)
        assertThat(navigator.currentEntry).isEqualTo(navigator.backStack.last())
    }

    @Test
    fun navigator_currentKey_returnsKey() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        assertThat(navigator.currentKey).isEqualTo(initialKey)
        assertThat(navigator.currentKey).isEqualTo(navigator.backStack.last().navigationKey)
    }

    @Test
    fun navigator_navigate_addsEntry() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val newKey = TestNavigationKey()
        navigator.navigate(newKey)

        navigator.assertKeys(initialKey, newKey)
    }

    @Test
    fun navigator_replaceLast_replacesLastEntry() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val newKey = TestNavigationKey()
        navigator.replaceLast(newKey)

        navigator.assertKeys(newKey)
    }

    @Test
    fun navigator_replaceUpTo_inclusive_replacesAllMatching() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        val keys = (0..2).map { TestNavigationKey() }
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )

        val newKey = TestNavigationKey()
        navigator.replaceUpTo(newKey, inclusive = true) {
            it == keys[0]
        }

        navigator.assertKeys(initialKey, newKey)
    }

    @Test
    fun navigator_replaceUpTo_NotInclusive_replacesAllButLast() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        val keys = (0..2).map { TestNavigationKey() }
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )

        val newKey = TestNavigationKey()
        navigator.replaceUpTo(newKey, inclusive = false) {
            it == keys[0]
        }

        navigator.assertKeys(initialKey, keys[0], newKey)
    }

    @Test
    fun navigator_replaceUpToKey_inclusive_replacesAllMatching() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)
        val key = TestKey()
        navigator.navigate(key)

        navigator.assertKeys(initialKey, key)

        val keys = (0..2).map { TestKey2() }
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                add(key)
                addAll(keys)
            }
        )

        val newKey = TestNavigationKey()
        navigator.replaceUpTo<TestKey>(newKey, inclusive = true)

        navigator.assertKeys(initialKey, newKey)
    }

    @Test
    fun navigator_replaceUpToKey_NotInclusive_replacesAllButLast() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)
        val key = TestKey()
        navigator.navigate(key)

        navigator.assertKeys(initialKey, key)

        val keys = (0..2).map { TestKey2() }
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                add(key)
                addAll(keys)
            }
        )

        val newKey = TestNavigationKey()
        navigator.replaceUpTo<TestKey>(newKey, inclusive = false)

        navigator.assertKeys(initialKey, key, newKey)
    }

    @Test
    fun navigator_moveToTop_found() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )

        assertThat(navigator.moveToTop { it is TestDataKey && it.data == 0 })
            .isTrue()

        navigator.assertKeys(
            initialKey,
            keys[1],
            keys[2],
            keys[0]
        )

        val newDataKey = TestDataKey(1)
        navigator.navigate(newDataKey)

        navigator.moveToTop(match = Match.First) { it is TestDataKey && it.data == 1 }

        navigator.assertKeys(
            initialKey,
            keys[2],
            keys[0],
            newDataKey,
            keys[1]
        )
    }

    @Test
    fun navigator_moveToTop_NotFound() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )

        assertThat(navigator.moveToTop { it is TestDataKey && it.data == 123 })
            .isFalse()

        assertThat(navigator.moveToTop(match = Match.First) { it is TestDataKey && it.data == 123 })
            .isFalse()

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )
    }

    @Test
    fun navigator_moveToTopKey_found() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )

        assertThat(navigator.moveToTop<TestNavigationKey>())
            .isTrue()

        navigator.assertKeys(
            keys[0],
            keys[1],
            keys[2],
            initialKey
        )

        navigator.moveToTop<TestDataKey>(match = Match.First)

        navigator.assertKeys(
            keys[1],
            keys[2],
            initialKey,
            keys[0]
        )
    }

    @Test
    fun navigator_moveToTopKey_NotFound() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )

        assertThat(navigator.moveToTop<TestKey3>())
            .isFalse()

        assertThat(navigator.moveToTop<TestKey3>(match = Match.First))
            .isFalse()

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )
    }

    @Test
    fun navigator_singleInstance_checkForExisting() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )

        val newKey = TestDataKey(1)
        navigator.singleInstance(
            navigationKey = newKey,
            match = Match.Last,
            checkForExisting = true
        )

        navigator.assertKeys(initialKey, keys[2])

        navigator.navigate(keys[0])
        navigator.navigate(keys[1])

        navigator.assertKeys(
            initialKey,
            keys[2],
            keys[0],
            keys[1]
        )

        navigator.singleInstance(
            navigationKey = newKey,
            match = Match.First,
            checkForExisting = true
        )

        navigator.assertKeys(initialKey, keys[2])
    }

    @Test
    fun navigator_singleInstance_notCheckForExisting() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            buildList {
                add(initialKey)
                addAll(keys)
            }
        )

        val newKey = TestDataKey(1)
        navigator.singleInstance(
            navigationKey = newKey,
            match = Match.Last,
            checkForExisting = false
        )

        navigator.assertKeys(initialKey, newKey)

        navigator.navigate(keys[0])
        navigator.navigate(keys[1])

        navigator.assertKeys(
            initialKey,
            newKey,
            keys[0],
            keys[1]
        )

        navigator.singleInstance(
            navigationKey = newKey,
            match = Match.First,
            checkForExisting = false
        )

        navigator.assertKeys(initialKey, newKey)
    }

    @Test
    fun navigator_singleTop_sameType_doesNotNavigate() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)
        navigator.assertKeys(initialKey)
        navigator.singleTop(TestNavigationKey())
        navigator.assertKeys(initialKey)
    }

    @Test
    fun navigator_singleTop_differentType_navigates() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)
        navigator.assertKeys(initialKey)
        val newKey = TestKey()
        navigator.singleTop(newKey)
        navigator.assertKeys(initialKey, newKey)
        navigator.singleTop(TestKey())
        navigator.assertKeys(initialKey, newKey)
    }

    @Test
    fun navigator_popTo_found_PopsToKey() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
        )

        assertThat(navigator.popTo(inclusive = true) { it is TestDataKey && it.data == 0 })
            .isTrue()

        navigator.assertKeys(initialKey)

        val testKey = TestKey()
        navigator.navigate(keys[1])
        navigator.navigate(testKey)
        navigator.navigate(keys[2])

        navigator.assertKeys(
            initialKey,
            keys[1],
            testKey,
            keys[2]
        )

        assertThat(navigator.popTo(inclusive = false) { it is TestNavigationKey })
            .isTrue()

        navigator.assertKeys(initialKey)
    }

    @Test
    fun navigator_popTo_notFound_returnsFalse() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
        )

        assertThat(navigator.popTo(inclusive = true) { it is TestKey })
            .isFalse()

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
        )

        val testKey = TestKey()
        navigator.navigate(testKey)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
            testKey,
        )

        assertThat(navigator.popTo(inclusive = false) { it is TestKey3 })
            .isFalse()

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
            testKey,
        )
    }

    @Test
    fun navigator_popToKey_found_PopsToKey() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
        )

        assertThat(navigator.popTo<TestDataKey>(inclusive = true) { it.data == 0 })
            .isTrue()

        navigator.assertKeys(initialKey)

        val testKey = TestKey()
        navigator.navigate(keys[1])
        navigator.navigate(testKey)
        navigator.navigate(keys[2])

        navigator.assertKeys(
            initialKey,
            keys[1],
            testKey,
            keys[2]
        )

        assertThat(navigator.popTo<TestNavigationKey>(inclusive = false))
            .isTrue()

        navigator.assertKeys(initialKey)
    }

    @Test
    fun navigator_popToKey_notFound_returnsFalse() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
        )

        assertThat(navigator.popTo<TestKey>(inclusive = true))
            .isFalse()

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
        )

        val testKey = TestKey()
        navigator.navigate(testKey)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
            testKey,
        )

        assertThat(navigator.popTo<TestKey3>(inclusive = false))
            .isFalse()

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
            testKey,
        )
    }

    @Test
    fun navigator_removeAll_removesMatchingPredicate() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        val testKey = TestKey()
        navigator.navigate(testKey)

        val dataKey0 = TestDataKey(0)
        navigator.navigate(dataKey0)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
            testKey,
            dataKey0
        )

        navigator.removeAll { it is TestDataKey && it.data == 0 }

        navigator.assertKeys(
            initialKey,
            keys[1],
            keys[2],
            testKey
        )
    }

    @Test
    fun navigator_removeAllKey_removesMatchingOfTypeKey() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        val testKey = TestKey()
        navigator.navigate(testKey)

        val testKey3 = TestKey3()
        navigator.navigate(testKey3)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
            testKey,
            testKey3
        )

        navigator.removeAll<TestDataKey>()

        navigator.assertKeys(
            initialKey,
            testKey,
            testKey3
        )
    }

    @Test
    fun navigator_popToRoot_popsToRoot() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
        )

        navigator.popToRoot()
        navigator.assertKeys(initialKey)
        navigator.popToRoot()
        navigator.assertKeys(initialKey)
    }

    @Test
    fun navigator_setRoot_setsNewRoot() {
        val initialKey = TestNavigationKey()
        val navigator = testNavigator(initialKey)

        navigator.assertKeys(initialKey)

        val keys = (0..2).map(::TestDataKey)
        keys.forEach(navigator::navigate)

        navigator.assertKeys(
            initialKey,
            keys[0],
            keys[1],
            keys[2],
        )

        navigator.setRoot(keys[0])
        navigator.assertKeys(keys[0])
        navigator.setRoot(keys[1])
        navigator.assertKeys(keys[1])
    }

}
