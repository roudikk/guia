package com.roudikk.navigator.navhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.roudikk.navigator.compose.savedstate.NavHostSaver
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.StackKey

@Composable
fun rememberNavHost(
    initialKey: StackKey,
    initialize: @DisallowComposableCalls (NavHost) -> Unit = {},
    builder: StackEntryListBuilder.() -> Unit,
): NavHost {
    val saveableStateHolder = rememberSaveableStateHolder()
    val entries = remember { StackEntryListBuilder().apply(builder).build() }

    return rememberSaveable(
        saver = NavHostSaver(saveableStateHolder, entries)
    ) {
        NavHost(
            initialKey = initialKey,
            saveableStateHolder = saveableStateHolder,
            entries = entries
        ).apply(initialize)
    }
}

class NavHost(
    initialKey: StackKey,
    internal val saveableStateHolder: SaveableStateHolder,
    val entries: List<StackEntry>
) {

    init {
        require(entries.any { it.stackKey == initialKey }) {
            "$initialKey must be added to list of entries."
        }
    }

    var activeKey by mutableStateOf(initialKey)
        private set

    val activeNavigator by derivedStateOf {
        requireNotNull(entries.firstOrNull { it.stackKey == activeKey }).navigator
    }

    fun setActive(stackKey: StackKey) {
        require(entries.any { it.stackKey == stackKey }) {
            "$stackKey does not exist in this NavHost, must be provided when calling rememberNavHost"
        }

        activeKey = stackKey
    }
}
