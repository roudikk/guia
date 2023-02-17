package com.roudikk.guia.navhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.roudikk.guia.backstack.navhost.DefaultStackBackHandler
import com.roudikk.guia.backstack.navhost.StackHistoryBackHandler
import com.roudikk.guia.containers.NavContainer
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.savedstate.navHostSaver

/**
 * Returns a saveable instance of a [NavHost].
 *
 * @param initialKey, the initial [StackKey]
 * @param entries, the stack entries
 * @param initialize, initialize the [NavHost] before it is returned.
 */
@Composable
fun rememberNavHost(
    initialKey: StackKey? = null,
    entries: Set<StackEntry>,
    initialize: @DisallowComposableCalls (NavHost) -> Unit = {},
): NavHost {
    return rememberSaveable(
        saver = navHostSaver(entries = entries)
    ) {
        NavHost().apply {
            updateEntries(entries)
            initialKey?.let(::setActive)
            initialize(this)
        }
    }.apply {
        updateEntries(entries)
    }
}

/**
 * A [NavHost] is a component that can handle managing multiple navigators at the same time,
 * allowing the consumer to render a certain navigator conditionally, animate the transition between
 * navigator changes and implement custom back behaviour.
 *
 * @see [NavHost.NavContainer] to render a [NavHost]'s state
 * @see [NavHost.DefaultStackBackHandler] and [NavHost.StackHistoryBackHandler] for back behaviours
 */
class NavHost {
    var stackEntries by mutableStateOf(setOf<StackEntry>())
        private set

    var currentEntry by mutableStateOf<StackEntry?>(null)
        private set

    val currentNavigator by derivedStateOf { currentEntry?.navigator }

    val currentKey by derivedStateOf { currentEntry?.stackKey }

    /**
     * Returns the navigator identified by a [StackKey]
     *
     * @param stackKey, key for a given navigator.
     *
     * @throws IllegalStateException if the  [stackKey] is not found in [stackEntries]
     */
    fun navigator(stackKey: StackKey): Navigator {
        return checkNotNull(stackEntries.firstOrNull { it.stackKey == stackKey }?.navigator)
    }

    /**
     * updates all the stack entries and tries to update the [currentEntry] if one of the new entries
     * has a matching [StackKey], otherwise the [currentEntry] will be set to null.
     *
     * @param entries, the new stack entries.
     */
    fun updateEntries(entries: Set<StackEntry>) {
        this.stackEntries = entries
        currentEntry = entries.firstOrNull { it.stackKey == currentEntry?.stackKey }
    }

    /**
     * Updates the current active [StackEntry]
     *
     * @param stackKey, the new stack key.
     *
     * @throws IllegalStateException if the [stackKey] is not part of the stack entries.
     */
    fun setActive(stackKey: StackKey?) {
        currentEntry = if (stackKey != null) {
            check(stackEntries.any { it.stackKey == stackKey }) {
                "$stackKey does not exist in this NavHost, must be provided when calling rememberNavHost"
            }
            stackEntries.first { it.stackKey == stackKey }
        } else {
            null
        }
    }
}
