package com.roudikk.navigator.sample.feature.custom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.entries
import com.roudikk.navigator.core.rememberNavigator
import com.roudikk.navigator.sample.feature.custom.api.CustomKey

private fun Navigator.addCards() {
    setBackstack((0..20).map { CustomKey(it) }.entries())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRootScreen() {
    val navigator = rememberNavigator(
        builder = { customNavigation() },
        initialize = { it.addCards() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navigator.addCards() }) {
            Text("Add Cards")
        }

        AnimatedVisibility(
            visible = navigator.backStack.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            navigator.CustomContainer(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxSize()
            )
        }
    }
}
