package com.roudikk.guia.sample.feature.common.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SampleSurfaceContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = Surface(
    modifier = Modifier
        .heightIn(max = 500.dp)
        .widthIn(max = 400.dp)
        .then(modifier)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(12.dp)
        ),
    tonalElevation = 4.dp,
    shape = RoundedCornerShape(12.dp),
    shadowElevation = 2.dp,
    content = content
)
