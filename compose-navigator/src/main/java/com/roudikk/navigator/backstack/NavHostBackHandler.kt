package com.roudikk.navigator.backstack

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.extensions.currentKey
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackKey

@Composable
fun NavHost.DefaultStackBackHandler(stackKey: StackKey) {
    BackHandler(stackKey != currentEntry?.stackKey) {
        setActive(stackKey)
    }
}

data class StackHistoryEntry(
    val stackKey: StackKey,
    val navigationKey: NavigationKey
)

@Composable
fun NavHost.CrossStackBackHandler() {
    val entries = remember { mutableStateListOf<StackHistoryEntry>() }

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

    LaunchedEffect(fullBackStack) {
        entries.removeAll { !fullBackStack.contains(it.navigationKey) }
    }

    val enabled = entries.size > 1
            && entries.last().stackKey == currentEntry?.stackKey
            && entries.last().navigationKey == currentEntry?.navigator?.currentKey

    LaunchedEffect(enabled) {
        currentNavigator?.overrideBackPress = !enabled
    }

    BackHandler(enabled) {
        val previousEntry = entries.getOrNull(entries.lastIndex - 1)
        previousEntry?.let {
            entries.removeLast()
            setActive(it.stackKey)
        }
    }
}
