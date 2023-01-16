package com.roudikk.navigator.sample.feature.custom

import androidx.compose.runtime.Composable
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigatorConfigBuilder

inline fun <reified Key : NavigationKey> NavigatorConfigBuilder.custom(
    noinline content: @Composable (Key) -> Unit
) {
    navigationNode<Key> {
        CustomNode { content(it) }
    }
}
