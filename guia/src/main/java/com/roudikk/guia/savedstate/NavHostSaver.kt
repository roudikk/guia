package com.roudikk.guia.savedstate

import androidx.compose.runtime.saveable.Saver
import com.roudikk.guia.navhost.NavHost
import com.roudikk.guia.navhost.StackEntry
import com.roudikk.guia.navhost.StackKey

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
