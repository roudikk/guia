package com.roudikk.navigator.sample.ui.screens.home

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigatorRulesBuilder
import com.roudikk.navigator.core.StackKey
import com.roudikk.navigator.extensions.clearResult
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.results
import com.roudikk.navigator.sample.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.navigation.findRootNavigator
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.screens.details.DetailsKey
import com.roudikk.navigator.sample.ui.screens.details.DetailsResult
import com.roudikk.navigator.sample.ui.screens.settings.SettingsKey
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
object HomeStackKey : StackKey

@Parcelize
class HomeKey : NavigationKey

fun NavigatorRulesBuilder.homeNavigation() {
    screen<HomeKey> { HomeScreen() }
}

@Composable
private fun HomeScreen() {
    val viewModel = viewModel<HomeViewModel>(LocalNavHostViewModelStoreOwner.current)
    val context = LocalContext.current

    val navigator = requireNavigator()
    val rootNavigator = findRootNavigator()

    val result = navigator.results<DetailsResult>()

    val command = viewModel.event
    LaunchedEffect(command) {
        when (command) {
            is HomeEvent.ShowToast -> context.showToast(command.item)
            is HomeEvent.OpenDetails -> navigator.navigate(DetailsKey(command.item))
            HomeEvent.OpenSettings -> rootNavigator.navigate(SettingsKey())
            HomeEvent.ClearResult -> navigator.clearResult<DetailsResult>()
            else -> return@LaunchedEffect
        }
        viewModel.onEventHandled()
    }

    HomeContent(
        listItems = viewModel.listItems,
        result = result?.value,
        onClearResultSelected = viewModel::onClearResultSelected,
        onItemSelected = viewModel::onItemSelected,
        onAddItemSelected = viewModel::onAddItemSelected,
        onRemoveItemSelected = viewModel::onRemoveItemSelected,
        onClearAllSelected = viewModel::onClearAllSelected,
        onSettingsSelected = viewModel::onSettingsSelected
    )
}

private fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

@Composable
private fun HomeContent(
    listItems: List<String>,
    result: String? = null,
    onClearResultSelected: () -> Unit = {},
    onItemSelected: (String) -> Unit = {},
    onAddItemSelected: () -> Unit = {},
    onRemoveItemSelected: (String) -> Unit = {},
    onClearAllSelected: () -> Unit = {},
    onSettingsSelected: () -> Unit = {}
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val enabled by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 ||
                    lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    BackHandler(
        enabled = enabled
    ) {
        scope.launch {
            lazyListState.scrollToItem(0)
        }
    }

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
                    IconButton(
                        onClick = { onSettingsSelected() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info"
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
    ) { padding ->
        Crossfade(
            modifier = Modifier.padding(padding),
            targetState = listItems.isEmpty()
        ) { itemsEmpty ->
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
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Current result: ${result ?: "None"}",
                                modifier = Modifier.weight(1F)
                            )

                            IconButton(onClick = onClearResultSelected) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    }

                    items(listItems, key = { it }) { item ->
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
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(item) }
            .fillMaxWidth()
            .then(modifier),
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

@Preview(
    device = Devices.PIXEL_3
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun HomeContentPreview() {
    HomeContent(listItems = listOf("1", "2", "3"))
}
