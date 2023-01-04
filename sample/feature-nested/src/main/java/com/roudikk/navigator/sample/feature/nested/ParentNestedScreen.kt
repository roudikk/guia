package com.roudikk.navigator.sample.feature.nested

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.containers.NavContainer
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.entry
import com.roudikk.navigator.core.rememberNavigator
import com.roudikk.navigator.extensions.currentKey
import com.roudikk.navigator.extensions.popTo
import com.roudikk.navigator.extensions.popToRoot
import com.roudikk.navigator.sample.feature.common.deeplink.DeepLinkViewModel
import com.roudikk.navigator.sample.feature.common.deeplink.NestedDestination
import com.roudikk.navigator.sample.feature.common.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.feature.common.navigation.VerticalSlideTransition
import com.roudikk.navigator.sample.feature.common.theme.AppTheme
import com.roudikk.navigator.sample.feature.nested.api.NestedKey

@Composable
fun ParentNestedScreen() {
    val deepLinkViewModel = viewModel<DeepLinkViewModel>(LocalNavHostViewModelStoreOwner.current)

    val nestedNavigator = rememberNavigator(
        initialKey = NestedKey(1),
        initialize = { it.deeplink(deepLinkViewModel) }
    ) {
        defaultTransition { -> VerticalSlideTransition }
        nestedNavigation()
    }

    ParentNestedContent(
        onPopToRootClicked = nestedNavigator::popToRoot,
        onNavigateToClicked = { index ->
            nestedNavigator.navigateToIndex(index)
        }
    ) {
        nestedNavigator.NavContainer()
    }

    LaunchedEffect(deepLinkViewModel.destinations) {
        nestedNavigator.deeplink(deepLinkViewModel)
    }
}

private fun Navigator.navigateToIndex(index: Int) {
    val currentKey = currentKey as NestedKey
    if (index > currentKey.index) {
        setBackstack(
            backStack + (currentKey.index + 1 until index + 1)
                .map { NestedKey(it).entry() }
        )
    } else {
        popTo<NestedKey> { key ->
            key.index == index
        }
    }
}

private fun Navigator.deeplink(deepLinkViewModel: DeepLinkViewModel) {
    deepLinkViewModel.destinations
        .filterIsInstance<NestedDestination>()
        .forEach { destination ->
            when (destination) {
                is NestedDestination.Nested -> navigateToIndex(destination.index)
            }
        }
    deepLinkViewModel.onNestedDestinationsHandled()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParentNestedContent(
    onPopToRootClicked: () -> Unit = {},
    onNavigateToClicked: (Int) -> Unit = {},
    container: @Composable () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Nested Navigation") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f)) {
                container()
            }

            Row(
                modifier = Modifier
                    .imePadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .fillMaxHeight()
                        .weight(1f),
                    onClick = onPopToRootClicked
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Pop to root"
                    )
                }

                val textFieldValue = rememberSaveable { mutableStateOf("") }

                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    value = textFieldValue.value,
                    onValueChange = {
                        textFieldValue.value = it
                    },
                    shape = RoundedCornerShape(20.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            textFieldValue.value.toIntOrNull()?.let {
                                onNavigateToClicked(it)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Pop"
                            )
                        }
                    },
                    placeholder = {
                        Text(text = "Navigate to index")
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
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
private fun NestedContentPreview() = AppTheme {
    ParentNestedContent {}
}
