package com.roudikk.navigator.sample.ui.screens.navigationtree

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.navigation.SampleStackKey
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class NavigationTreeScreen : Screen {

    @Composable
    override fun Content() {
        NavigationTreeContent()
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
private fun NavigationTreeContent(
    navigator: Navigator = requireNavigator()
) {
    val lazyListState = rememberLazyListState()
    val elevation by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0 ||
                lazyListState.firstVisibleItemScrollOffset > 0
            ) 4.dp else 0.dp
        }
    }
    val animatedElevation by animateDpAsState(
        targetValue = elevation
    )

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = animatedElevation
            ) {
                Column {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    TopAppBar(
                        title = {
                            Text(
                                text = "Navigation Tree"
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        val state by navigator.stateFlow.collectAsState()

        val stacks = state.navigationStacks.filter { it.key != SampleStackKey.StackTree }
            .map { it.key to it.destinations }

        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                backgroundColor = MaterialTheme.colorScheme.surface,
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                }
            ) {
                // Add tabs for all of our pages
                stacks.forEachIndexed { index, (key, _) ->
                    Tab(
                        text = {
                            Text(text = key::class.java.simpleName)
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                    )
                }
            }

            HorizontalPager(
                modifier = Modifier.weight(1F),
                state = pagerState,
                count = stacks.size,
                key = { stacks[it].first.toString() }
            ) { page ->
                val stack = stacks[page]

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = GridCells.Adaptive(100.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(stack.second) { destination ->
                        Box(
                            Modifier
                                .sizeIn(minWidth = 100.dp)
                                .aspectRatio(9F / 16F)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(2.dp)
                        ) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(2.dp))
                            ) {
                                CompositionLocalProvider(
                                    LocalDensity provides object : Density by LocalDensity.current {
                                        override val density: Float = 1.5f
                                        override val fontScale: Float = 1f
                                    }
                                ) {
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = true
                                    ) {
                                        with(destination.navigationNode) { Content() }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    device = Devices.PIXEL_3
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun NavigationTreePreview() = AppTheme {
    val navigator = rememberNavigator()
    NavigationTreeContent(navigator = navigator)
}
