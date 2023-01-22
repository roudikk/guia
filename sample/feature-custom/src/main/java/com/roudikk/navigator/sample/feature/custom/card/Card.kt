package com.roudikk.navigator.sample.feature.custom.card

import androidx.compose.runtime.Composable
import com.roudikk.navigator.core.NavigationNode

class Card(
    override val content: @Composable () -> Unit
) : NavigationNode
