package com.roudikk.guia

import com.google.common.truth.Truth.assertThat
import com.roudikk.guia.navhost.NavHost
import com.roudikk.guia.navhost.StackEntry
import com.roudikk.guia.util.TestNavigationKey
import com.roudikk.guia.util.TestStackKey
import com.roudikk.guia.util.testNavigator
import org.junit.Assert.assertThrows
import org.junit.Test

class NavHostTest {

    @Test
    fun navHost_initialize_initialState() {
        val navHost = NavHost()
        assertThat(navHost.currentEntry).isNull()
        assertThat(navHost.stackEntries).isEmpty()
        assertThat(navHost.currentNavigator).isNull()
    }

    @Test
    fun navHost_updateEntries_updatesEntries_updatesCurrentKey() {
        val navHost = NavHost()
        assertThat(navHost.stackEntries).isEmpty()
        val activeKey = TestStackKey()
        val activeEntry = StackEntry(
            stackKey = activeKey,
            navigator = testNavigator(TestNavigationKey())
        )
        val entries = setOf(
            activeEntry,
            StackEntry(
                stackKey = TestStackKey(),
                navigator = testNavigator(TestNavigationKey())
            ),
            StackEntry(
                stackKey = TestStackKey(),
                navigator = testNavigator(TestNavigationKey())
            )
        )
        navHost.updateEntries(entries)
        assertThat(navHost.stackEntries).isEqualTo(entries)
        assertThat(navHost.currentEntry).isNull()
        navHost.setActive(activeKey)
        assertThat(navHost.currentEntry).isEqualTo(activeEntry)
        val newActiveEntry = StackEntry(
            stackKey = activeKey,
            navigator = testNavigator(TestNavigationKey())
        )
        val newEntries = setOf(
            newActiveEntry,
            StackEntry(
                stackKey = TestStackKey(),
                navigator = testNavigator(TestNavigationKey())
            )
        )
        navHost.updateEntries(newEntries)
        assertThat(navHost.stackEntries).isEqualTo(newEntries)
        assertThat(navHost.currentEntry).isEqualTo(newActiveEntry)
    }

    @Test
    fun navHost_setActive_available_updatesActiveEntry() {
        val navHost = NavHost()
        assertThat(navHost.stackEntries).isEmpty()
        val entry1 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry2 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry3 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entries = setOf(entry1, entry2, entry3)
        navHost.updateEntries(entries)
        assertThat(navHost.currentEntry).isNull()
        navHost.setActive(entry1.stackKey)
        assertThat(navHost.currentEntry).isEqualTo(entry1)
        navHost.setActive(null)
        assertThat(navHost.currentEntry).isNull()
        navHost.setActive(entry2.stackKey)
        assertThat(navHost.currentEntry).isEqualTo(entry2)
        navHost.setActive(entry3.stackKey)
        assertThat(navHost.currentEntry).isEqualTo(entry3)
    }

    @Test
    fun navHost_setActive_unavailable_throwsException() {
        val navHost = NavHost()
        assertThat(navHost.stackEntries).isEmpty()
        val entry1 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry2 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry3 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entries = setOf(entry1, entry2, entry3)
        navHost.updateEntries(entries)
        assertThrows(IllegalStateException::class.java) {
            navHost.setActive(TestStackKey())
        }
    }

    @Test
    fun navHost_currentNavigator_updatesWithActiveEntry() {
        val navHost = NavHost()
        assertThat(navHost.stackEntries).isEmpty()
        val entry1 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry2 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry3 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entries = setOf(entry1, entry2, entry3)
        navHost.updateEntries(entries)
        assertThat(navHost.currentEntry).isNull()
        assertThat(navHost.currentNavigator).isNull()
        navHost.setActive(entry1.stackKey)
        assertThat(navHost.currentEntry).isEqualTo(entry1)
        navHost.setActive(null)
        assertThat(navHost.currentEntry).isNull()
        navHost.setActive(entry2.stackKey)
        assertThat(navHost.currentEntry).isEqualTo(entry2)
        navHost.setActive(entry3.stackKey)
        assertThat(navHost.currentEntry).isEqualTo(entry3)
    }

    @Test
    fun navHost_navigator_availableKey_returnsNavigator() {
        val navHost = NavHost()
        assertThat(navHost.stackEntries).isEmpty()
        val entry1 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry2 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry3 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entries = setOf(entry1, entry2, entry3)
        navHost.updateEntries(entries)
        assertThat(navHost.navigator(entry1.stackKey)).isEqualTo(entry1.navigator)
        assertThat(navHost.navigator(entry2.stackKey)).isEqualTo(entry2.navigator)
        assertThat(navHost.navigator(entry3.stackKey)).isEqualTo(entry3.navigator)
    }

    @Test
    fun navHost_navigator_keyNotFound_throwsException() {
        val navHost = NavHost()
        assertThat(navHost.stackEntries).isEmpty()
        val entry1 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry2 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entry3 = StackEntry(
            stackKey = TestStackKey(),
            navigator = testNavigator(TestNavigationKey())
        )
        val entries = setOf(entry1, entry2, entry3)
        navHost.updateEntries(entries)
        assertThrows(IllegalStateException::class.java) {
            navHost.navigator(TestStackKey())
        }
    }
}
