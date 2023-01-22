package com.roudikk.navigator.sample.feature.custom.card

import androidx.compose.runtime.Composable
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.custom.api.CardKey

inline fun <reified Key : NavigationKey> NavigatorConfigBuilder.card(
    noinline content: @Composable (Key) -> Unit
) {
    navigationNode<Key> { Card { content(it) } }
}

fun NavigatorConfigBuilder.cardNavigation() {
    card<CardKey> { key -> CardScreen(id = key.id) }
}
