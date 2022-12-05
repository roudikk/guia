package com.roudikk.navigator.navhost

import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.StackKey

data class StackEntry(
    val stackKey: StackKey,
    val navigator: Navigator
)

class StackEntryListBuilder {
    private val entries = mutableListOf<StackEntry>()

    fun entry(stackKey: StackKey, navigator: Navigator) {
        require(!entries.any { it.stackKey == stackKey }) {
            "StackKey: $stackKey already added, cannot use more than once."
        }
        entries.add(StackEntry(stackKey, navigator))
    }

    fun build(): List<StackEntry> {
        require(entries.isNotEmpty()) {
            "Must add at least one entry using entry(key, navigator))"
        }
        return entries
    }
}
