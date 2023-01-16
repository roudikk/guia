package com.roudikk.navigator.sample.feature.custom

import androidx.compose.runtime.Composable
import com.roudikk.navigator.core.NavigationNode

class CustomNode(
    override val content: @Composable () -> Unit
) : NavigationNode
