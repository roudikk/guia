package com.roudikk.composenavigator.ui.screens.nested

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsPadding
import com.roudikk.compose_navigator.*
import com.roudikk.compose_navigator.animation.navigationFadeIn
import com.roudikk.compose_navigator.animation.navigationFadeOut
import com.roudikk.compose_navigator.animation.navigationSlideInVertically
import com.roudikk.compose_navigator.animation.navigationSlideOutVertically
import com.roudikk.composenavigator.ui.composables.AppTopAppBar
import kotlinx.parcelize.Parcelize

@Parcelize
class ParentNestedScreen : Screen {

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        Scaffold(
            topBar = {
                AppTopAppBar(title = "Nested navigation")
            }
        ) {
            NavHost(
                navigationConfig = NavigationConfig.SingleStack(NestedScreen(1))
            ) {
                val navigator = findNavigator()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(modifier = Modifier.weight(1f)) {
                        NavContainer(
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Button(
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            navigator.popToRoot()
                        }
                    ) {
                        Text(text = "Pop to root")
                    }
                }
            }
        }
    }
}

@Parcelize
class NestedScreen(
    private val count: Int
) : Screen {

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        val navigator = findNavigator()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconButton(
                enabled = navigator.canGoBack(),
                onClick = {
                    navigator.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "back"
                )
            }

            Surface(
                tonalElevation = 10.dp,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(100.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "$count",
                        fontSize = 40.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    navigator.navigate(
                        NestedScreen(count + 1),
                        navOptions = NavOptions(
                            navTransition = NavTransition(
                                enter = navigationSlideInVertically { it / 2 }
                                        + navigationFadeIn(),
                                exit = navigationSlideOutVertically { -it / 2 }
                                        + navigationFadeOut(),
                                popEnter = navigationSlideInVertically { -it / 2 }
                                        + navigationFadeIn(),
                                popExit = navigationSlideOutVertically { it / 2 }
                                        + navigationFadeOut()
                            )
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    }
}