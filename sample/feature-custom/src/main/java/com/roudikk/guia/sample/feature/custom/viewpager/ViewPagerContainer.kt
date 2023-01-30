package com.roudikk.guia.sample.feature.custom.viewpager

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.roudikk.guia.containers.NavEntryContainer
import com.roudikk.guia.core.Navigator
import kotlin.math.abs

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Navigator.ViewPagerContainer(
    modifier: Modifier
) {
    val backstackManager = rememberViewPagerBackstackManager(navigator = this)
    val pagerState = rememberPagerState(initialPage = activeIndex)

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        count = backstack.size,
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) { page ->
        backstackManager.visibleBackstack.entries
            .firstOrNull { it.backstackEntry.id == backstack[page].id }
            ?.let {
                NavEntryContainer(
                    backstackManager = backstackManager,
                    lifecycleEntry = it
                )
            }
    }

    LaunchedEffect(pagerState.currentPage) {
        setActive(pagerState.currentPage)
    }

    LaunchedEffect(backstack) {
        if (abs(pagerState.currentPage - activeIndex) == 1) {
            pagerState.animateScrollToPage(activeIndex)
        } else {
            pagerState.scrollToPage(activeIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose(backstackManager::onDispose)
    }
}
