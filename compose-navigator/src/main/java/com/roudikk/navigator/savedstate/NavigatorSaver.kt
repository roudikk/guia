package com.roudikk.navigator.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.core.NavigationEntry
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorConfig
import com.roudikk.navigator.core.ResultManager
import kotlinx.parcelize.Parcelize

/**
 * Compose saver for [Navigator].
 *
 * Saves and restores the state of a navigator.
 */
internal fun navigatorSaver(
    saveableStateHolder: SaveableStateHolder,
    navigatorConfig: NavigatorConfig,
    resultManager: ResultManager
) = Saver<Navigator, NavigatorState>(
    save = { it.save() },
    restore = { navigatorState ->
        Navigator(
            initialKey = navigatorState.initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorConfig = navigatorConfig,
            resultManager = resultManager
        ).apply {
            restore(navigatorState)
        }
    }
)

private fun Navigator.save() = NavigatorState(
    initialKey = initialKey,
    navigationEntries = navigationEntries,
    overrideBackPress = overrideBackPress
)

private fun Navigator.restore(
    navigatorState: NavigatorState
) {
    navigatorState.navigationEntries.forEach { entry ->
        navigationEntriesMap[entry.navigationKey] = entry
    }
    setBackstack(navigatorState.navigationEntries.map { it.navigationKey })
    overrideBackPress = navigatorState.overrideBackPress
}

/**
 * Used to hold the state of a [Navigator] for state saving/restoration.
 */
@Parcelize
internal data class NavigatorState(
    val initialKey: NavigationKey,
    val navigationEntries: List<NavigationEntry>,
    val overrideBackPress: Boolean
) : Parcelable
