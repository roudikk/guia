package com.roudikk.navigator.sample.ui.composables

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.roudikk.navigator.BottomSheetSetup

@Composable
fun BottomSheetSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .widthIn(max = 600.dp),
        tonalElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        content()
    }
}

fun defaultBottomSheetSetup(modifier: Modifier = Modifier) = BottomSheetSetup(
    bottomSheetContainer = { content ->
        BottomSheetSurface(
            modifier = Modifier
                .systemBarsPadding(bottom = false)
                .then(modifier),
            content = content
        )
    }
)