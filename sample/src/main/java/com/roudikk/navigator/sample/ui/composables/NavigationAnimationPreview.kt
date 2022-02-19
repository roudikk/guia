package com.roudikk.navigator.sample.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.roudikk.navigator.compose.LocalNavigationAnimation

@Composable
fun NavigationAnimationPreview(
    content: @Composable () -> Unit
) {
    AnimatedVisibility(visible = true) {
        CompositionLocalProvider(
            LocalNavigationAnimation provides this,
            content = content
        )
    }
}
