package com.roudikk.navigator.backstack.navhost

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.roudikk.navigator.containers.NavContainer
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.extensions.currentKey
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackKey

/**
 * An entry in the stack history.
 *
 * @property stackKey, the stack key at that point in history.
 * @property navigationKey, the navigation key at that point in history.
 */
private class StackHistoryEntry(
    val stackKey: StackKey,
    val navigationKey: NavigationKey
)

/**
 * A [BackHandler] for a [NavHost] that will navigate between stack keys as [NavHost.setActive]
 * is called.
 *
 * Make sure to call this before the [NavHost]'s [NavContainer] so entries navigation entries
 * inside the nav host's navigators can override the back press.
 */
@Composable
fun NavHost.StackHistoryBackHandler() {
    val entries = remember { mutableStateListOf<StackHistoryEntry>() }

    // Add an history entry whenever the current stack entry has been changed.
    LaunchedEffect(currentEntry) {
        currentEntry?.let {
            if (entries.lastOrNull()?.stackKey != it.stackKey) {
                entries.add(
                    StackHistoryEntry(
                        stackKey = it.stackKey,
                        navigationKey = it.navigator.currentKey
                    )
                )
            }
        }
    }

    val fullBackStack = stackEntries
        .map { it.navigator }
        .map { it.backStack }
        .flatten()

    // Cleanup history entries if their associated navigation key is no longer in any backstack.
    LaunchedEffect(fullBackStack) {
        entries.removeAll { !fullBackStack.contains(it.navigationKey) }
    }

    val overrideBackPress by remember(entries, currentEntry) {
        derivedStateOf {
            entries.size > 1 &&
                entries.last().stackKey == currentEntry?.stackKey &&
                entries.last().navigationKey == currentEntry?.navigator?.currentKey
        }
    }

    // If we are overriding the back press, we disable the current navigator's
    // back handling so it doesn't override the below BackHandler
    LaunchedEffect(overrideBackPress) {
        currentNavigator?.overrideBackPress = !overrideBackPress
    }

    // Get the previous entry and navigate to its Stack Key.
    BackHandler(overrideBackPress) {
        val previousEntry = entries.getOrNull(entries.lastIndex - 1)
        previousEntry?.let {
            entries.removeLast()
            setActive(it.stackKey)
        }
    }
}
