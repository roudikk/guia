package com.roudikk.navigator.savedstate

import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.Navigator

/**
 * Compose saver for [Navigator].
 *
 * Saves and restores the state of a navigator.
 */
internal fun navigatorSaver(saveableStateHolder: SaveableStateHolder): Saver<Navigator, NavigatorState> =
    Saver(
        save = { it.save() },
        restore = { navigatorState ->
            Navigator(
                saveableStateHolder = saveableStateHolder,
                navigationConfig = navigatorState.navigationConfig
            ).apply { restore(navigatorState) }
        }
    )
