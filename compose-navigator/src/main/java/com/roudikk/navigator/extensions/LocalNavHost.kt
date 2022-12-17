package com.roudikk.navigator.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.roudikk.navigator.navhost.NavHost

internal val LocalNavHost = staticCompositionLocalOf<NavHost?> { error("Must be provided") }

@Composable
fun findNavHost() = LocalNavHost.current

@Composable
fun requireNavHost() = requireNotNull(LocalNavHost.current) {
    "No NavHost found, Call requireNavHost inside a NavigationKey hosted by a NavHost."
}
