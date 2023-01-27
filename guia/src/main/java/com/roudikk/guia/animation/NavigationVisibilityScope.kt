package com.roudikk.guia.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.roudikk.guia.core.NavigationKey

/**
 * When a navigation transition is in progress, it's done within an [AnimatedContent].
 *
 * The Composable content of a [NavigationKey] might require access to the [AnimatedVisibilityScope]
 * of that [AnimatedContent], for example to animate elements using [AnimatedVisibilityScope.animateEnterExit].
 *
 * This Local will be provided by the NavContainer to all screens, bottom sheets and dialogs.
 *
 * For Composable previews where this Local wouldn't be provided. Make sure to provide a value yourself
 * using the [CompositionLocalProvider].
 */
val LocalNavigationVisibilityScope = compositionLocalOf<AnimatedVisibilityScope> {
    error("Must be used inside a navigation node contained in a NavContainer")
}

/**
 * Provides an instance of [LocalNavigationVisibilityScope], used by the Composable containers.
 */
@Composable
internal fun AnimatedVisibilityScope.ProvideNavigationVisibilityScope(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalNavigationVisibilityScope provides this,
        content = content
    )
}

/**
 * Call this to execute a [block] within the context of a [LocalNavigationVisibilityScope].
 */
@Composable
fun NavigationVisibilityScope(block: @Composable AnimatedVisibilityScope.() -> Unit) {
    with(LocalNavigationVisibilityScope.current) {
        block()
    }
}
