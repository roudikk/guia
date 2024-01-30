package com.roudikk.guia.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.roudikk.guia.containers.NavContainer
import com.roudikk.guia.navhost.NavHost

/**
 * For navigation keys hosted within a [NavHost] that require access to that nav host,
 * a Local is provided to all Composables inside a [NavHost.NavContainer].
 */
val LocalNavHost = staticCompositionLocalOf<NavHost?> { null }

/**
 * Returns a [NavHost] that is hosting the caller Composable.
 *
 * @throws IllegalStateException if the [LocalNavHost] was not provided.
 */
val ProvidableCompositionLocal<NavHost?>.currentOrThrow: NavHost
    @ReadOnlyComposable
    @Composable
    inline get() = checkNotNull(current) {
        "No NavHost found, Call LocalNavHost inside a NavigationKey hosted by a NavHost."
    }

/**
 * Returns an optional [NavHost] that is hosting the caller Composable.
 */
@Deprecated(
    "Use LocalNavHost.current",
    replaceWith = ReplaceWith(
        expression = "LocalNavHost.current",
        imports = arrayOf("com.roudikk.guia.extensions.LocalNavHost")
    )
)
@Composable
fun localNavHost() = LocalNavHost.current

/**
 * Returns a [NavHost] that is hosting the caller Composable.
 *
 * @throws IllegalStateException if the [LocalNavHost] was not provided.
 */
@Deprecated(
    "Use LocalNavHost.currentOrThrow",
    replaceWith = ReplaceWith(
        expression = "LocalNavHost.currentOrThrow",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavHost",
            "com.roudikk.guia.extensions.currentOrThrow",
        )
    )
)
@Composable
fun requireLocalNavHost() = checkNotNull(LocalNavHost.current) {
    "No NavHost found, Call requireNavHost inside a NavigationKey hosted by a NavHost."
}
