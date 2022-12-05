package com.roudikk.navigator.navhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalNavHost = staticCompositionLocalOf<NavHost?> { error("Must be provided") }

@Composable
fun findNavHost() = LocalNavHost.current

@Composable
fun requireNavHost() = requireNotNull(LocalNavHost.current) {
    "No NavHost found, Call requireNavHost inside a NavigationKey hosted by a NavHost."
}
