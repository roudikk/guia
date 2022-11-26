package com.roudikk.navigator.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.NavigatorRules
import com.roudikk.navigator.core.Destination
import kotlinx.parcelize.Parcelize

/**
 * Used to hold the state of a [Navigator] for state saving/restoration.
 */
@Parcelize
internal data class NavigatorState(
    val destinations: List<Destination>,
) : Parcelable


/**
 * Compose saver for [Navigator].
 *
 * Saves and restores the state of a navigator.
 */
internal fun navigatorSaver(
    saveableStateHolder: SaveableStateHolder,
    navigatorRules: NavigatorRules
): Saver<Navigator, NavigatorState> =
    Saver(
        save = { it.save() },
        restore = { navigatorState ->
            Navigator(
                saveableStateHolder = saveableStateHolder,
                navigatorRules = navigatorRules
            ).apply { restore(navigatorState) }
        }
    )

private fun Navigator.save() = NavigatorState(destinations = destinations)

private fun Navigator.restore(
    navigatorState: NavigatorState
) {
    navigatorState.destinations.forEach { destination ->
        destinationsMap[destination.navigationKey] = destination
    }
    setBackstack(navigatorState.destinations.map { it.navigationKey })
}
