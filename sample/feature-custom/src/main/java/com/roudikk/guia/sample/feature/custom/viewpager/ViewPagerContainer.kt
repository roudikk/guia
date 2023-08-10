package com.roudikk.guia.sample.feature.custom.viewpager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roudikk.guia.containers.NavEntryContainer
import com.roudikk.guia.core.Navigator
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Navigator.ViewPagerContainer(
    modifier: Modifier
) {
    val lifecycleManager = rememberViewPagerLifecycleManager(navigator = this)
    val pagerState = rememberPagerState(
        initialPage = activeIndex,
        pageCount = { backstack.size }
    )

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) { page ->
        lifecycleManager.renderGroup.entries
            .firstOrNull { it.backstackEntry.id == backstack[page].id }
            ?.let {
                NavEntryContainer(
                    lifecycleManager = lifecycleManager,
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
        onDispose(lifecycleManager::onDispose)
    }
}
