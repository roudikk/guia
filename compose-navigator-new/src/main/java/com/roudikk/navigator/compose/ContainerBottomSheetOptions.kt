package com.roudikk.navigator.compose

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.roudikk.navigator.Navigator

/**
 * Provide extra bottom sheet options.
 *
 * @property scrimColor the scrim color behind the bottom sheet and on top of the content behind it.
 * @property bottomSheetContainer use this when the navigation requires animating between content
 * of two bottom sheets using [Navigator.navigate] instead of animating the transitions between 2
 * bottom sheets, this container will be the parent of all the bottom sheets defined in the app.
 */
data class ContainerBottomSheetOptions(
    val scrimColor: Color = Color.Black.copy(alpha = 0.4F),

    val animationSpec: AnimationSpec<Float> = tween(300),

    val bottomSheetContainer: @Composable (
        modifier: Modifier,
        content: @Composable () -> Unit
    ) -> Unit = { modifier, content ->
        Box(modifier = modifier) {
            content()
        }
    }
)
