package com.roudikk.navigator.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.core.NavigationNode

internal val LocalNavigator = compositionLocalOf<Navigator?> { null }
internal val LocalParentNavigator = compositionLocalOf<Navigator?> { null }


/**
 * returns local [Navigator] or throws an error.
 */
@Composable
fun requireNavigator(): Navigator {
    return requireNotNull(LocalNavigator.current) {
        "requireNavigator() must be called in a NavigationNode hosted in a NavContainer."
    }
}

/**
 * Returns current local [Navigator] if it exists.
 */
@Composable
fun findNavigator(): Navigator? {
    return LocalNavigator.current
}

/**
 * Returns the parent [Navigator].
 *
 * If the [NavigationNode] is inside [NavContainer] which is hosted in a [NavigationNode]
 * inside a parent [NavContainer], then the returned [Navigator] is the
 * one used in the parent [NavContainer]
 */
@Composable
fun findParentNavigator(): Navigator? {
    return LocalParentNavigator.current
}
