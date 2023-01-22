package com.roudikk.navigator.sample.feature.custom.viewpager

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.roudikk.navigator.containers.NavigationEntryContainer
import com.roudikk.navigator.core.Navigator
import kotlin.math.abs

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Navigator.ViewPagerContainer(
    modifier: Modifier
) {
    val backStackManager = rememberViewPagerBackStackManager(navigator = this)
    val pagerState = rememberPagerState(initialPage = activeIndex)

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        count = backStack.size,
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) { page ->
        backStackManager.visibleBackStack.entries
            .firstOrNull { it.backStackEntry.id == backStack[page].id }
            ?.let {
                NavigationEntryContainer(
                    backStackManager = backStackManager,
                    lifecycleEntry = it
                )
            }
    }

    LaunchedEffect(pagerState.currentPage) {
        setActive(pagerState.currentPage)
    }

    LaunchedEffect(backStack) {
        if (abs(pagerState.currentPage - activeIndex) == 1) {
            pagerState.animateScrollToPage(activeIndex)
        } else {
            pagerState.scrollToPage(activeIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose(backStackManager::onDispose)
    }
}
