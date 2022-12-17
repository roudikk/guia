package com.roudikk.navigator.savedstate

import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry
import com.roudikk.navigator.navhost.StackKey

internal fun navHostSaver(
    entries: Set<StackEntry>,
    saveableStateHolder: SaveableStateHolder
) = Saver<NavHost, StackKey>(
    save = { it.currentEntry?.stackKey },
    restore = {
        NavHost(saveableStateHolder = saveableStateHolder).apply {
            updateEntries(entries)
            setActive(it)
        }
    }
)
