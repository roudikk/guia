package com.roudikk.navigator.compose.savedstate

import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.core.StackKey
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry

internal fun NavHostSaver(
    entries: Set<StackEntry>
) = Saver<NavHost, StackKey>(
    save = { it.currentEntry?.stackKey },
    restore = {
        NavHost().apply {
            updateEntries(entries)
            setActive(it)
        }
    }
)
