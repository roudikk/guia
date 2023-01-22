package com.roudikk.navigator.sample.feature.custom.viewpager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.core.entry
import com.roudikk.navigator.core.rememberNavigator
import com.roudikk.navigator.sample.feature.custom.api.PageKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPagerRootScreen() {
    val navigator = rememberNavigator(
        builder = { viewPagerNavigation() },
        initialize = {
            it.setBackstack(
                (0..9).map { index -> PageKey(isActive = index == 0).entry() }
            )
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Page No: ${navigator.activeIndex + 1}") }
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            navigator.ViewPagerContainer(
                modifier = Modifier.weight(1F)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1F),
                    onClick = { navigator.setActive(navigator.activeIndex - 1) },
                    enabled = navigator.activeIndex != 0
                ) {
                    Text("Previous")
                }

                Button(
                    modifier = Modifier.weight(1F),
                    onClick = { navigator.setActive(navigator.activeIndex + 1) },
                    enabled = navigator.activeIndex != navigator.backStack.lastIndex
                ) {
                    Text("Next")
                }
            }
        }
    }
}
