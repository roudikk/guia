package com.roudikk.navigator.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.roudikk.navigator.containers.NavContainer
import com.roudikk.navigator.navhost.NavHost

/**
 * For navigation keys hosted within a [NavHost] that require access to that nav host,
 * a Local is provided to all Composables inside a [NavHost.NavContainer].
 */
internal val LocalNavHost = staticCompositionLocalOf<NavHost?> { error("Must be provided") }

/**
 * Returns an optional [NavHost] that is hosting the caller Composable.
 */
@Composable
fun localNavHost() = LocalNavHost.current

/**
 * Returns a [NavHost] that is hosting the caller Composable.
 *
 * @throws IllegalStateException if the [LocalNavHost] was not provided.
 */
@Composable
fun requireLocalNavHost() = checkNotNull(LocalNavHost.current) {
    "No NavHost found, Call requireNavHost inside a NavigationKey hosted by a NavHost."
}
