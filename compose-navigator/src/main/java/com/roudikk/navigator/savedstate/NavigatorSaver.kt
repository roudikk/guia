package com.roudikk.navigator.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.core.BackstackEntry
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorConfig
import com.roudikk.navigator.core.ResultManager
import kotlinx.parcelize.Parcelize

/**
 * Used to save and restore the state of a [Navigator].
 */
internal fun navigatorSaver(
    navigatorConfig: NavigatorConfig,
    resultManager: ResultManager
) = Saver<Navigator, NavigatorState>(
    save = { it.save() },
    restore = { navigatorState ->
        Navigator(
            navigatorConfig = navigatorConfig,
            resultManager = resultManager
        ).apply {
            restore(navigatorState)
        }
    }
)

private fun Navigator.save() = NavigatorState(
    backstack = backstack,
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
    val backstack: List<BackstackEntry>,
    val overrideBackPress: Boolean
) : Parcelable
