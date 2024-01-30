package com.roudikk.guia.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.roudikk.guia.containers.NavContainer
import com.roudikk.guia.core.Navigator

/**
 * Provides access to the current navigator, accessible to all composables
 * within the context of a [NavContainer].
 */
val LocalNavigator = compositionLocalOf<Navigator?> { null }

/**
 * Provides access to the current parent navigator, if one exists.
 */
val LocalParentNavigator = compositionLocalOf<Navigator?> { null }

/**
 * Returns an [Navigator] that is hosting the caller Composable.
 *
 * @throws IllegalStateException if it's called in Composable not within a [NavContainer]
 */
val ProvidableCompositionLocal<Navigator?>.currentOrThrow: Navigator
    @ReadOnlyComposable
    @Composable
    inline get() = checkNotNull(current) {
        "LocalNavigator must be called in a NavigationNode hosted in a NavContainer."
    }

/**
 * Returns an optional [Navigator] that is hosting the caller Composable.
 */
@Deprecated(
    "Use LocalNavigator.current",
    replaceWith = ReplaceWith(
        expression = "LocalNavigator.current",
        imports = arrayOf("com.roudikk.guia.extensions.LocalNavigator")
    )
)
@Composable
fun localNavigator(): Navigator? {
    return LocalNavigator.current
}

/**
 * Returns an [Navigator] that is hosting the caller Composable.
 *
 * @throws IllegalStateException if it's called in Composable not within a [NavContainer]
 */
@Deprecated(
    "Use LocalNavigator.currentOrThrow",
    replaceWith = ReplaceWith(
        expression = "LocalNavigator.currentOrThrow",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavigator",
            "com.roudikk.guia.extensions.currentOrThrow",
        )
    )
)
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
@Deprecated(
    "Use LocalParentNavigator.current",
    replaceWith = ReplaceWith(
        expression = "LocalParentNavigator.current",
        imports = arrayOf("com.roudikk.guia.extensions.LocalParentNavigator")
    )
)
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
@Deprecated(
    "Use LocalParentNavigator.currentOrThrow",
    replaceWith = ReplaceWith(
        expression = "LocalParentNavigator.currentOrThrow",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalParentNavigator",
            "com.roudikk.guia.extensions.currentOrThrow",
        )
    )
)
@Composable
fun requireLocalParentNavigator(): Navigator {
    return checkNotNull(LocalParentNavigator.current)
}
