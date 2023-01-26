package com.roudikk.guia.sample.feature.custom.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.entries
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.sample.feature.custom.navigation.CardKey

private fun Navigator.addCards() {
    setBackstack((0..20).map { CardKey(it) }.entries())
}

@Composable
fun CardRootScreen() {
    val navigator = rememberNavigator(
        builder = { cardNavigation() },
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
            visible = navigator.backstack.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            navigator.CardContainer(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxSize()
            )
        }
    }
}
