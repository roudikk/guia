package com.roudikk.guia.backstack.navhost

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.roudikk.guia.backstack.NavBackHandler
import com.roudikk.guia.containers.NavContainer
import com.roudikk.guia.core.BackstackEntry
import com.roudikk.guia.extensions.currentEntry
import com.roudikk.guia.navhost.NavHost
import com.roudikk.guia.navhost.StackKey
import com.roudikk.guia.savedstate.stackHistorySaver
import kotlinx.parcelize.Parcelize

/**
 * A [BackHandler] for a [NavHost] that will navigate between stack keys as [NavHost.setActive]
 * is called.
 *
 * Make sure to call this before the [NavHost]'s [NavContainer] so entries navigation entries
 * inside the nav host's navigators can override the back press.
 */
@Composable
fun NavHost.StackHistoryBackHandler() {
    val stackHistory = rememberSaveable(saver = stackHistorySaver()) { mutableStateListOf() }

    // Add an history entry whenever the current stack entry has been changed.
    LaunchedEffect(currentEntry) {
        val currentEntry = currentEntry ?: return@LaunchedEffect
        val backstackEntry = currentEntry.navigator.currentEntry ?: return@LaunchedEffect
        if (stackHistory.lastOrNull()?.stackKey != currentEntry.stackKey) {
            stackHistory.add(
                StackHistoryEntry(
                    stackKey = currentEntry.stackKey,
                    backstackEntry = backstackEntry
                )
            )
        }
    }

    val fullBackstack = stackEntries
        .map { it.navigator }
        .map { it.backstack }
        .flatten()

    // Cleanup history entries if their associated navigation key is no longer in any backstack.
    LaunchedEffect(fullBackstack) {
        stackHistory.removeAll { entry -> fullBackstack.none { it.id == entry.backstackEntry.id } }
    }

    val overrideBackPress by remember {
        derivedStateOf {
            stackHistory.size > 1 &&
                stackHistory.lastOrNull()?.stackKey == currentEntry?.stackKey &&
                stackHistory.lastOrNull()?.backstackEntry?.id == currentEntry?.navigator?.currentEntry?.id
        }
    }

    // If we are overriding the back press, we disable the current navigator's
    // back handling so it doesn't override the below BackHandler.
    LaunchedEffect(overrideBackPress) {
        currentNavigator?.overrideBackPress = !overrideBackPress
    }

    // Get the previous entry and navigate to its Stack Key.
    NavBackHandler(overrideBackPress) {
        stackHistory.getOrNull(stackHistory.lastIndex - 1)?.let {
            setActive(it.stackKey)
            stackHistory.removeLast()
        }
    }
}

/**
 * An entry in the stack history.
 *
 * @property stackKey, the stack key at that point in history.
 * @property backstackEntry, the navigation key at that point in history.
 */
@Parcelize
internal class StackHistoryEntry(
    val stackKey: StackKey,
    val backstackEntry: BackstackEntry
) : Parcelable
