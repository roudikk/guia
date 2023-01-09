package com.roudikk.navigator.sample.feature.common.composables

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SampleSurfaceContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.widthIn(max = 500.dp),
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        content = content
    )
}
