package com.roudikk.navigator.sample.feature.custom.viewpager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.core.rememberNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPagerRootScreen() {
    val navigator = rememberNavigator(builder = { viewPagerNavigation() })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (navigator.backStack.isNotEmpty()) {
                            "Page No: ${navigator.activeIndex + 1}"
                        } else {
                            "Add pages"
                        }
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            navigator.ViewPagerContainer(
                modifier = Modifier.weight(1F)
            )

            Column {
                Row(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                        enabled = navigator.activeIndex != navigator.backStack.lastIndex &&
                            navigator.backStack.isNotEmpty()
                    ) {
                        Text("Next")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        modifier = Modifier.weight(1F),
                        onClick = { navigator.addPage() },
                        enabled = navigator.backStack.size < 10
                    ) {
                        Text("Add Page")
                    }

                    Button(
                        modifier = Modifier.weight(1F),
                        onClick = { navigator.removePage() },
                        enabled = navigator.backStack.isNotEmpty()
                    ) {
                        Text("Remove Page")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
