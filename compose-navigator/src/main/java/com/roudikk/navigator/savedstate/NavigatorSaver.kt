package com.roudikk.navigator.savedstate

import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.Navigator

/**
 * Compose saver for [Navigator].
 *
 * Saves and restores the state of a navigator.
 */
internal val NavigatorSaver: Saver<Navigator, NavigatorState>
    get() = Saver(
        save = { it.save() },
        restore = { navigatorState ->
            Navigator(navigatorState.navigationConfig).apply { restore(navigatorState) }
        }
    )
