package com.roudikk.navigator.sample.ui.screens.details

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.*
import com.roudikk.navigator.sample.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.screens.home.HomeScreen
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class DetailsScreen(
    private val item: String
) : Screen {

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        DetailsContent(item = item)
    }
}

@Parcelize
class DetailsBottomSheet(
    private val item: String
) : BottomSheet {

    override val bottomSheetOptions: BottomSheetOptions
        get() = BottomSheetOptions(
            confirmStateChange = {
                it != ModalBottomSheetValue.HalfExpanded
            }
        )

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        DetailsList(
            navigator = findNavigator(),
            item = item
        )
    }
}

@Composable
private fun DetailsContent(
    navigator: Navigator = findNavigator(),
    item: String
) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Details",
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DetailsList(
                navigator = navigator,
                item = item
            )
        }
    }
}

@Composable
private fun DetailsList(
    navigator: Navigator,
    item: String
) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Item: $item",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            modifier = Modifier
                .widthIn(min = 300.dp),
            onClick = {
                val newItem = UUID.randomUUID().toString().split("-")[0]
                navigator.navigate(
                    navigationNode = DetailsScreen(newItem),
                    navOptions = NavOptions(
                        navTransition = MaterialSharedAxisTransitionX
                    )
                )
            }
        ) {
            Text(text = "New random item")
        }

        Button(
            modifier = Modifier
                .widthIn(min = 300.dp),
            onClick = {
                navigator.sendResult<HomeScreen>(item)
                navigator.popToRoot()
            }
        ) {
            Text(text = "Send result back to home")
        }

        Button(
            modifier = Modifier
                .widthIn(min = 300.dp),
            onClick = {
                val newItem = UUID.randomUUID().toString().split("-")[0]
                navigator.navigate(
                    navigationNode = DetailsBottomSheet(newItem),
                    navOptions = NavOptions(
                        navTransition = MaterialSharedAxisTransitionX
                    )
                )
            }
        ) {
            Text(text = "Bottom Sheet")
        }

        Button(
            modifier = Modifier
                .widthIn(min = 300.dp),
            onClick = {
                val newItem = UUID.randomUUID().toString().split("-")[0]
                navigator.navigate(
                    navigationNode = DetailsScreen(newItem),
                    navOptions = NavOptions(
                        launchMode = LaunchMode.SINGLE_TOP,
                        navTransition = MaterialSharedAxisTransitionX
                    )
                )
            }
        ) {
            Text(text = "Single top Screen")
        }

        Button(
            modifier = Modifier
                .widthIn(min = 300.dp),
            onClick = {
                val newItem = UUID.randomUUID().toString().split("-")[0]
                navigator.navigate(
                    navigationNode = DetailsBottomSheet(newItem),
                    navOptions = NavOptions(
                        launchMode = LaunchMode.SINGLE_TOP,
                        navTransition = MaterialSharedAxisTransitionX
                    )
                )
            }
        ) {
            Text(text = "Single top bottom sheet")
        }

        Button(
            modifier = Modifier
                .widthIn(min = 300.dp),
            onClick = {
                val newItem = UUID.randomUUID().toString().split("-")[0]
                navigator.navigate(
                    navigationNode = DetailsScreen(newItem),
                    navOptions = NavOptions(
                        launchMode = LaunchMode.SINGLE_INSTANCE,
                        navTransition = MaterialSharedAxisTransitionX
                    )
                )
            }
        ) {
            Text(text = "Single Instance")
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
private fun DetailsContentPreview() = AppTheme {
    DetailsContent(
        navigator = Navigator(),
        item = "Test Item"
    )
}
