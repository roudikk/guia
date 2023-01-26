package com.roudikk.guia.sample.feature.custom.card

import androidx.compose.runtime.Composable
import com.roudikk.guia.core.NavigationNode

class Card(
    override val content: @Composable () -> Unit
) : NavigationNode
