package com.roudikk.guia.sample.feature.common.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.roudikk.guia.animation.LocalNavVisibilityScope

@Composable
fun NavigationAnimationPreview(
    content: @Composable () -> Unit
) {
    AnimatedVisibility(visible = true) {
        CompositionLocalProvider(
            LocalNavVisibilityScope provides this,
            content = content
        )
    }
}
