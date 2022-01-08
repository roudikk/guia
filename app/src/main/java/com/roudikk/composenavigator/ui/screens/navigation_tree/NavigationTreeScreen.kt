package com.roudikk.composenavigator.ui.screens.navigation_tree

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.roudikk.compose_navigator.Screen
import com.roudikk.compose_navigator.findNavigator
import com.roudikk.composenavigator.AppNavigationKey
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class NavigationTreeScreen : Screen {

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        NavigationTreeScreenContent()
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
private fun NavigationTreeScreenContent() {
    val navigator = findNavigator()

    val lazyListState = rememberLazyListState()
    val animatedElevation by animateDpAsState(
        targetValue = if (lazyListState.firstVisibleItemIndex > 0
            || lazyListState.firstVisibleItemScrollOffset > 0
        ) 4.dp else 0.dp
    )
    Scaffold(
        topBar = {
            Surface(
                tonalElevation = animatedElevation
            ) {
                Column {
                    Spacer(modifier = Modifier.statusBarsHeight())
                    SmallTopAppBar(
                        title = {
                            Text(
                                text = "Navigation Tree"
                            )
                        }
                    )
                }
            }
        }
    ) {
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        val state by navigator.stateFlow.collectAsState()

        val stacks = state.navigationStacks.filter { it.key != AppNavigationKey.NavigationTree }
            .map { it.key to it.destinations }

        Column {
            TabRow(
                backgroundColor = MaterialTheme.colorScheme.surface,
                // Our selected tab is our current page
                selectedTabIndex = pagerState.currentPage,
                // Override the indicator, using the provided pagerTabIndicatorOffset modifier
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
                            Text(text = key::class.simpleName!!)
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

                Grid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columnCount = 2,
                    list = stack.second
                ) { destination ->
                    Box(
                        Modifier
                            .weight(1F)
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
                                    override val density: Float = 1.5F
                                    override val fontScale: Float = 1F
                                }
                            ) {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = true
                                ) {
                                    destination.navigationNode.Content(this)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> Grid(
    modifier: Modifier = Modifier,
    columnCount: Int = 1,
    list: List<T>,
    child: @Composable RowScope.(data: T) -> Unit
) {
    val rows = (list.size / columnCount) + (if (list.size % columnCount > 0) 1 else 0)
    var listSize = list.size
    var extraItems = 0
    while (listSize > 0 && listSize % columnCount != 0) {
        extraItems++
        listSize++
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        for (row in 0 until rows) {
            Row(
                modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (cell in 0 until columnCount) {
                    val rowIndex = (row * columnCount) + cell
                    if (rowIndex < list.size) {
                        child(list[rowIndex])
                    } else {
                        break
                    }
                }

                if (row == rows - 1) {
                    for (i in 0 until extraItems) {
                        Box(Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))
    }
}