package com.roudikk.navigator.savedstate

import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry
import com.roudikk.navigator.navhost.StackKey

/**
 * Used to save and restore the state of a [NavHost].
 */
internal fun navHostSaver(
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
