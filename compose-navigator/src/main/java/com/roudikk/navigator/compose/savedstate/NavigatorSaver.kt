package com.roudikk.navigator.compose.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorRules
import com.roudikk.navigator.core.Destination
import kotlinx.parcelize.Parcelize

/**
 * Used to hold the state of a [Navigator] for state saving/restoration.
 */
@Parcelize
internal data class NavigatorState(
    val initialKey: NavigationKey,
    val destinations: List<Destination>,
) : Parcelable

/**
 * Compose saver for [Navigator].
 *
 * Saves and restores the state of a navigator.
 */
internal fun NavigatorSaver(
    saveableStateHolder: SaveableStateHolder,
    navigatorRules: NavigatorRules
) = Saver<Navigator, NavigatorState>(
    save = { it.save() },
    restore = { navigatorState ->
        Navigator(
            initialKey = navigatorState.initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorRules = navigatorRules
        ).apply { restore(navigatorState) }
    }
)

private fun Navigator.save() = NavigatorState(
    initialKey = initialKey,
    destinations = destinations
)

private fun Navigator.restore(
    navigatorState: NavigatorState
) {
    navigatorState.destinations.forEach { destination ->
        destinationsMap[destination.navigationKey] = destination
    }
    setBackstack(navigatorState.destinations.map { it.navigationKey })
}
