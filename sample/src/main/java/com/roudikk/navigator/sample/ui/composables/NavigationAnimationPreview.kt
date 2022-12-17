package com.roudikk.navigator.sample.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.roudikk.navigator.animation.LocalNavigationVisibilityScope

@Composable
fun NavigationAnimationPreview(
    content: @Composable () -> Unit
) {
    AnimatedVisibility(visible = true) {
        CompositionLocalProvider(
            LocalNavigationVisibilityScope provides this,
            content = content
        )
    }
}
