package com.roudikk.navigator.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalNavigationVisibilityScope = compositionLocalOf<AnimatedVisibilityScope> {
    error("Must be used inside a navigation node contained in a NavContainer")
}

@Composable
internal fun AnimatedVisibilityScope.ProvideNavigationVisibilityScope(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalNavigationVisibilityScope provides this,
        content = content
    )
}

@Composable
fun NavigationVisibilityScope(block: @Composable AnimatedVisibilityScope.() -> Unit) {
    with(LocalNavigationVisibilityScope.current) {
        block()
    }
}
