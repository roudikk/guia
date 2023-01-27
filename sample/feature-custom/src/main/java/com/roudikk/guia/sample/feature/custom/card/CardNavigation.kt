package com.roudikk.guia.sample.feature.custom.card

import androidx.compose.runtime.Composable
import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.guia.sample.feature.custom.navigation.CardKey

inline fun <reified Key : NavigationKey> NavigatorConfigBuilder.card(
    noinline content: @Composable (Key) -> Unit
) {
    navigationNode<Key> { Card { content(it) } }
}

fun NavigatorConfigBuilder.cardNavigation() {
    card<CardKey> { key -> CardScreen(id = key.id) }
    supportedNavigationNodes(Card::class)
    defaultTransition { -> CrossFadeTransition }
}
