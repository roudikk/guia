package com.roudikk.navigator.sample.ui.screens.navigationtree

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.roudikk.navigator.NavHost
import com.roudikk.navigator.NavigationKey
import com.roudikk.navigator.NavigatorRulesScope
import com.roudikk.navigator.core.StackKey
import com.roudikk.navigator.requireNavHost
import com.roudikk.navigator.sample.ui.screens.bottomnav.rememberBottomNavHost
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@Parcelize
object NavigationTreeStackKey : StackKey

@Parcelize
class NavigationTreeKey : NavigationKey

fun NavigatorRulesScope.navigationTreeNavigation() {
    screen<NavigationTreeKey> { NavigationTreeScreen() }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class
)
@Composable
private fun NavigationTreeScreen(
    navHost: NavHost = requireNavHost()
) {
    val keyedNavigators = navHost.keyedNavigators.filter { it.first != NavigationTreeStackKey }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Navigation Tree") }) }
    ) { padding ->
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState()

        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                backgroundColor = MaterialTheme.colorScheme.surface,
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            ) {
                // Add tabs for all of our pages
                keyedNavigators.forEachIndexed { index, (key, _) ->
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
                count = keyedNavigators.size,
                key = { keyedNavigators[it].first.toString() }
            ) { page ->
                val navigator = keyedNavigators[page].second
                val destinations = navigator.destinations

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = GridCells.Adaptive(100.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(destinations) { destination ->
                        Box(
                            Modifier
                                .sizeIn(minWidth = 100.dp)
                                .aspectRatio(9F / 16F)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(2.dp)
                        ) {
                            Column(
                                Modifier.clip(RoundedCornerShape(2.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .fillMaxSize()
                                        .padding(1.dp)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = destination.navigationKey.tag())
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
    NavigationTreeScreen(navHost = rememberBottomNavHost())
}
