package com.roudikk.navigator.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.core.BackStackEntry
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
    navigatorConfig: NavigatorConfig,
    resultManager: ResultManager
) = Saver<Navigator, NavigatorState>(
    save = { it.save() },
    restore = { navigatorState ->
        Navigator(
            initialKey = navigatorState.initialKey,
            navigatorConfig = navigatorConfig,
            resultManager = resultManager
        ).apply {
            restore(navigatorState)
        }
    }
)

private fun Navigator.save() = NavigatorState(
    initialKey = initialKey,
    backstack = backStack,
    overrideBackPress = overrideBackPress
)

private fun Navigator.restore(
    navigatorState: NavigatorState
) {
    setBackstack(navigatorState.backstack)
    overrideBackPress = navigatorState.overrideBackPress
}

/**
 * Used to hold the state of a [Navigator] for state saving/restoration.
 */
@Parcelize
internal data class NavigatorState(
    val initialKey: NavigationKey,
    val backstack: List<BackStackEntry>,
    val overrideBackPress: Boolean
) : Parcelable
