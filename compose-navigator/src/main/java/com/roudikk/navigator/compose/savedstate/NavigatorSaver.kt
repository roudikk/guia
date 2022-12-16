package com.roudikk.navigator.compose.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.core.NavigationEntry
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.NavigatorRules
import kotlinx.parcelize.Parcelize

/**
 * Used to hold the state of a [Navigator] for state saving/restoration.
 */
@Parcelize
internal data class NavigatorState(
    val initialKey: NavigationKey,
    val navigationEntries: List<NavigationEntry>,
    val overrideBackPress: Boolean,
    val results: HashMap<String, Parcelable>,
) : Parcelable

/**
 * Compose saver for [Navigator].
 *
 * Saves and restores the state of a navigator.
 */
internal fun navigatorSaver(
    saveableStateHolder: SaveableStateHolder,
    navigatorRules: NavigatorRules
) = Saver<Navigator, NavigatorState>(
    save = { it.save() },
    restore = { navigatorState ->
        Navigator(
            initialKey = navigatorState.initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorRules = navigatorRules
        ).apply {
            restore(navigatorState)
        }
    }
)

private fun Navigator.save() = NavigatorState(
    initialKey = initialKey,
    navigationEntries = navigationEntries,
    overrideBackPress = overrideBackPress,
    results = hashMapOf<String, Parcelable>().apply {
        results
            .filter { it.value is Parcelable }
            .forEach { this[it.key] = it.value as Parcelable }
    }
)

private fun Navigator.restore(
    navigatorState: NavigatorState
) {
    navigatorState.navigationEntries.forEach { destination ->
        destinationsMap[destination.navigationKey] = destination
    }
    setBackstack(navigatorState.navigationEntries.map { it.navigationKey })
    navigatorState.results.forEach { results[it.key] = it.value }
    overrideBackPress = navigatorState.overrideBackPress
}
