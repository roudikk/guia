package com.roudikk.composenavigator.ui.screens.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.compose_navigator.Screen
import com.roudikk.compose_navigator.findNavigator
import com.roudikk.composenavigator.AppPreview
import com.roudikk.composenavigator.ui.composables.AppTopAppBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

@Parcelize
class HomeScreen : Screen {

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        val viewModel = viewModel<HomeViewModel>()
        val navigator = findNavigator()

        LaunchedEffect(Unit) {
            viewModel.commandsFlow
                .collect {
                    navigator
                }
        }

        HomeContent(
            stateFlow = viewModel.stateFlow,
            onItemSelected = viewModel::onItemSelected,
            onAddItemSelected = viewModel::onAddItemSelected,
            onRemoveItemSelected = viewModel::onRemoveItemSelected,
            onClearAllSelected = viewModel::onClearAllSelected
        )
    }
}

@Composable
private fun HomeContent(
    stateFlow: StateFlow<List<String>>,
    onItemSelected: (String) -> Unit = {},
    onAddItemSelected: () -> Unit = {},
    onRemoveItemSelected: (String) -> Unit = {},
    onClearAllSelected: () -> Unit = {}
) {
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Home",
                lazyListState = lazyListState,
                actions = {
                    IconButton(
                        onClick = { onClearAllSelected() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "Clear all items"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddItemSelected
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item"
                )
            }
        }
    ) {
        val items by stateFlow.collectAsState()

        Crossfade(targetState = items.isEmpty()) { itemsEmpty ->
            if (itemsEmpty) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.widthIn(max = 240.dp),
                        text = "Add items by clicking on the + button on the bottom right",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(items, key = { it }) { item ->
                        ListItem(
                            modifier = Modifier.animateItemPlacement(),
                            item = item,
                            onClick = onItemSelected,
                            onRemove = onRemoveItemSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    item: String,
    onClick: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    Surface(
        onClick = { onClick(item) },
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                text = "Item: $item"
            )

            IconButton(
                modifier = Modifier.padding(end = 4.dp),
                onClick = { onRemove(item) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
@Preview
private fun HomeContentPreview() = AppPreview {
    HomeContent(
        stateFlow = MutableStateFlow(listOf("Item 1", "Item 2", "Item 3"))
    )
}
