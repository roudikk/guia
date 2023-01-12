package com.roudikk.navigator.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.roudikk.navigator.containers.NavContainer
import com.roudikk.navigator.core.Navigator

/**
 * Provides access to the current navigator, accessible to all composables
 * within the context of a [NavContainer].
 */
internal val LocalNavigator = compositionLocalOf<Navigator?> { null }

/**
 * Provides access to the current parent navigator, if one exists.
 */
internal val LocalParentNavigator = compositionLocalOf<Navigator?> { null }

/**
 * Returns an optional [Navigator] that is hosting the caller Composable.
 */
@Composable
fun localNavigator(): Navigator? {
    return LocalNavigator.current
}

/**
 * Returns an [Navigator] that is hosting the caller Composable.
 *
 * @throws IllegalStateException if it's called in Composable not within a [NavContainer]
 */
@Composable
fun requireLocalNavigator(): Navigator {
    return checkNotNull(LocalNavigator.current) {
        "requireNavigator() must be called in a NavigationNode hosted in a NavContainer."
    }
}

/**
 * Returns an optional [Navigator] that is the parent of the current navigator hosting
 * the caller Composable.
 */
@Composable
fun localParentNavigator(): Navigator? {
    return LocalParentNavigator.current
}

/**
 * Returns a [Navigator] that is the parent of the current navigator hosting
 * the caller Composable.
 *
 * @throws IllegalStateException if there is no parent [Navigator] in the current context.
 */
@Composable
fun requireLocalParentNavigator(): Navigator {
    return checkNotNull(LocalParentNavigator.current)
}
