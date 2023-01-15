package com.roudikk.navigator.sample.feature.navtree

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.roudikk.navigator.core.navigationNode
import com.roudikk.navigator.extensions.requireLocalNavHost
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.sample.feature.navtree.api.NavigationTreeStackKey
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class, ExperimentalComposeUiApi::class
)
@Composable
internal fun NavigationTreeScreen(
    navHost: NavHost = requireLocalNavHost()
) {
    val stackEntries by remember {
        derivedStateOf {
            navHost.stackEntries.toMutableList().apply {
                removeAll { it.stackKey == NavigationTreeStackKey }
            }
        }
    }

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
                stackEntries.forEachIndexed { index, entry ->
                    Tab(
                        text = {
                            Text(text = entry.stackKey::class.java.simpleName)
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
                count = stackEntries.size,
                key = { stackEntries[it].stackKey.toString() }
            ) { page ->
                val navigator = stackEntries[page].navigator

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = GridCells.Adaptive(200.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(navigator.backStack) { backStackKey ->
                        Box(
                            Modifier
                                .pointerInteropFilter { true }
                                .sizeIn(minWidth = 200.dp)
                                .aspectRatio(9F / 16F)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(1.dp)
                        ) {
                            Column(
                                Modifier.clip(RoundedCornerShape(2.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .fillMaxSize()
                                        .padding(1.dp)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val navigationNode = navigator.navigationNode(backStackKey)
                                    navigationNode.content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
