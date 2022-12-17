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
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.savedstate.navHostSaver

@Composable
fun rememberNavHost(
    initialKey: StackKey,
    entries: Set<StackEntry>,
    initialize: @DisallowComposableCalls (NavHost) -> Unit = {},
): NavHost {
    val saveableStateHolder = rememberSaveableStateHolder()
    return rememberSaveable(
        saver = navHostSaver(
            entries = entries,
            saveableStateHolder = saveableStateHolder
        )
    ) {
        NavHost(saveableStateHolder = saveableStateHolder).apply {
            updateEntries(entries)
            setActive(initialKey)
            initialize(this)
        }
    }.apply {
        updateEntries(entries)
    }
}

class NavHost(
    val saveableStateHolder: SaveableStateHolder
) {
    var stackEntries by mutableStateOf(setOf<StackEntry>())
        private set

    var currentEntry by mutableStateOf<StackEntry?>(null)
        private set

    val currentNavigator by derivedStateOf { currentEntry?.navigator }

    fun navigator(stackKey: StackKey): Navigator {
        return requireNotNull(stackEntries.firstOrNull { it.stackKey == stackKey }?.navigator)
    }

    fun updateEntries(entries: Set<StackEntry>) {
        this.stackEntries = entries
        currentEntry = entries.firstOrNull { it.stackKey == currentEntry?.stackKey }
    }

    fun setActive(stackKey: StackKey) {
        require(stackEntries.any { it.stackKey == stackKey }) {
            "$stackKey does not exist in this NavHost, must be provided when calling rememberNavHost"
        }

        currentEntry = stackEntries.first { it.stackKey == stackKey }
    }
}
