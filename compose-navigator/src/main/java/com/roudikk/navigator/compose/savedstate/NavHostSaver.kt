package com.roudikk.navigator.compose.savedstate

import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.core.StackKey
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry

internal fun NavHostSaver(
    saveableStateHolder: SaveableStateHolder,
    entries: List<StackEntry>,
) = Saver<NavHost, StackKey>(
    save = { it.activeKey },
    restore = { NavHost(it, saveableStateHolder, entries) }
)
