package com.roudikk.navigator.sample.ui.composables

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.compose.BottomSheetSetup

@Composable
fun BottomSheetSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .widthIn(max = 600.dp),
        tonalElevation = 16.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        content()
    }
}

fun sampleBottomSheetOptions(modifier: Modifier = Modifier) = BottomSheetSetup(
    bottomSheetContainer = { nodeModifier, content ->
        BottomSheetSurface(
            modifier = modifier
                .padding(16.dp)
                .then(nodeModifier),
            content = content
        )
    },
    animationSpec = tween(durationMillis = 300)
)
