package com.roudikk.navigator.sample.ui.screens.home

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.*
import com.roudikk.navigator.sample.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.MaterialSharedAxisTransitionXY
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.screens.details.DetailsScreen
import com.roudikk.navigator.sample.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize

@Parcelize
class HomeScreen : Screen {

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        HomeController()
    }
}

@Composable
private fun HomeController(
    viewModel: HomeViewModel = viewModel(),
    navigator: Navigator = findNavigator(),
    defaultNavigator: Navigator = findDefaultNavigator()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.commandsFlow
            .onEach { homeCommand ->
                when (homeCommand) {
                    is HomeCommand.OpenDetails -> navigator.navigate(
                        navigationNode = DetailsScreen(homeCommand.item),
                        navOptions = NavOptions(
                            navTransition = MaterialSharedAxisTransitionX
                        )
                    )
                    HomeCommand.OpenSettings -> defaultNavigator.navigate(
                        navigationNode = SettingsScreen(),
                        navOptions = NavOptions(
                            navTransition = MaterialSharedAxisTransitionXY
                        )
                    )
                }
            }
            .launchIn(this)

        navigator.results<HomeScreen>()
            .onEach {
                Toast.makeText(context, "Result from: $it", Toast.LENGTH_SHORT).show()
            }
            .launchIn(this)
    }

    HomeContent(
        stateFlow = viewModel.stateFlow,
        onItemSelected = viewModel::onItemSelected,
        onAddItemSelected = viewModel::onAddItemSelected,
        onRemoveItemSelected = viewModel::onRemoveItemSelected,
        onClearAllSelected = viewModel::onClearAllSelected,
        onSettingsSelected = viewModel::onSettingsSelected
    )
}

@Composable
private fun HomeContent(
    stateFlow: StateFlow<List<String>>,
    onItemSelected: (String) -> Unit = {},
    onAddItemSelected: () -> Unit = {},
    onRemoveItemSelected: (String) -> Unit = {},
    onClearAllSelected: () -> Unit = {},
    onSettingsSelected: () -> Unit = {}
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
    HomeController(
        viewModel = HomeViewModel(SavedStateHandle()),
        navigator = Navigator(),
        defaultNavigator = Navigator()
    )
}
