package com.roudikk.navigator

import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.extensions.currentEntry
import com.roudikk.navigator.extensions.currentKey
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.replaceLast
import com.roudikk.navigator.extensions.replaceUpTo
import com.roudikk.navigator.util.TestKey
import com.roudikk.navigator.util.TestKey2
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

        val keys = (0..1).map { TestNavigationKey() }
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

        val keys = (0..1).map { TestNavigationKey() }
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

        val keys = (0..1).map { TestKey2() }
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

        val keys = (0..1).map { TestKey2() }
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
}
