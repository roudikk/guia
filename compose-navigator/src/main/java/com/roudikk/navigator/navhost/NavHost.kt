package com.roudikk.navigator.navhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.roudikk.navigator.compose.savedstate.NavHostSaver
import com.roudikk.navigator.core.StackKey

@Composable
fun rememberNavHost(
    initialKey: StackKey,
    entries: Set<StackEntry>,
    initialize: @DisallowComposableCalls (NavHost) -> Unit = {},
): NavHost {
   return rememberSaveable(
        saver = NavHostSaver(entries)
    ) {
        NavHost( ).apply {
            updateEntries(entries)
            setActive(initialKey)
            initialize(this)
        }
    }.apply {
        updateEntries(entries)
    }
}

class NavHost {
    var entries by mutableStateOf(setOf<StackEntry>())
        private set

    var currentEntry by mutableStateOf<StackEntry?>(null)
        private set

    fun updateEntries(entries: Set<StackEntry>) {
        this.entries = entries
        currentEntry = entries.firstOrNull { it.stackKey == currentEntry?.stackKey }
    }

    fun setActive(stackKey: StackKey) {
        require(entries.any { it.stackKey == stackKey }) {
            "$stackKey does not exist in this NavHost, must be provided when calling rememberNavHost"
        }

        currentEntry = entries.first { it.stackKey == stackKey }
    }
}
