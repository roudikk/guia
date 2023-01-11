package com.roudikk.navigator

import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.extensions.Match
import com.roudikk.navigator.extensions.currentEntry
import com.roudikk.navigator.extensions.currentKey
import com.roudikk.navigator.extensions.moveToTop
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.replaceLast
import com.roudikk.navigator.extensions.replaceUpTo
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
}
