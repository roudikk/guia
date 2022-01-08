package com.roudikk.composenavigator.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.roudikk.compose_navigator.Screen
import com.roudikk.compose_navigator.findNavigator
import com.roudikk.composenavigator.AppPreview
import com.roudikk.composenavigator.ui.composables.AppTopAppBar
import kotlinx.parcelize.Parcelize

@Parcelize
class SettingsScreen : Screen {

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        SettingsContent(animatedVisibilityScope)
    }
}

@Composable
private fun SettingsContent(animatedVisibilityScope: AnimatedVisibilityScope) {
    val navigator = findNavigator()

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = {
            AppTopAppBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        },
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f))

            with(animatedVisibilityScope) {
                Text(
                    modifier = androidx.compose.ui.Modifier
                        .animateEnterExit(
                            enter = slideInVertically(animationSpec = tween(delayMillis = 1000)) { it }
                                    + fadeIn(animationSpec = tween(delayMillis = 1000)),
                            exit = slideOutVertically { it }
                                    + fadeOut()
                        )
                        .padding(16.dp),
                    text = "Sorry I'm late! ",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsContentPreview() = AppPreview {
    AnimatedVisibility(visible = true) {
        SettingsContent(this)
    }
}