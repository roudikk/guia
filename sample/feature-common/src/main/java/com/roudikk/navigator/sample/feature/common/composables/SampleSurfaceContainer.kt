package com.roudikk.navigator.sample.feature.common.composables

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.containers.BottomSheetSetup

@Composable
fun SampleSurfaceContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.widthIn(max = 500.dp),
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        content = content
    )
}

fun sampleBottomSheetOptions(modifier: Modifier = Modifier) = BottomSheetSetup(
    bottomSheetContainer = { nodeModifier, content ->
        SampleSurfaceContainer(
            modifier = modifier
                .then(nodeModifier),
            content = content
        )
    },
    animationSpec = tween(durationMillis = 300)
)
