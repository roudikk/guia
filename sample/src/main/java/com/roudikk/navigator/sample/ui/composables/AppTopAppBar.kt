package com.roudikk.navigator.sample.ui.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun AppTopAppBar(
    title: String,
    lazyListState: LazyListState? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    val animatedElevation by animateDpAsState(
        lazyListState?.let {
            if (lazyListState.firstVisibleItemIndex > 0 ||
                lazyListState.firstVisibleItemScrollOffset > 0
            ) 4.dp else 0.dp
        } ?: 0.dp
    )

    Surface(
        tonalElevation = animatedElevation
    ) {
        SmallTopAppBar(
            modifier = Modifier
                .statusBarsPadding(),
            title = { Text(text = title) },
            navigationIcon = navigationIcon,
            actions = actions
        )
    }
}
