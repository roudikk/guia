package com.roudikk.navigator.compose.savedstate

import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.navhost.StackKey
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry

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
