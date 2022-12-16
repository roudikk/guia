package com.roudikk.navigator.sample.ui.screens.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.NavigatorRulesBuilder
import com.roudikk.navigator.navhost.StackKey
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.sample.DeepLinkViewModel
import com.roudikk.navigator.sample.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.navigation.requireRootNavigator
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
object DialogsStackKey : StackKey

@Parcelize
class DialogsKey : NavigationKey

fun NavigatorRulesBuilder.dialogsNavigation() {
    screen<DialogsKey> { DialogsScreen() }
}

@Composable
private fun DialogsScreen(
) {
    val deepLinkViewModel = viewModel<DeepLinkViewModel>(LocalNavHostViewModelStoreOwner.current)

    val navigator = requireNavigator()
    val rootNavigator = requireRootNavigator()

    DialogsContent(
        onCancelableDialogClicked = { navigator.navigate(CancelableDialogKey(false)) },
        onBlockingDialogClicked = { navigator.navigate(BlockingDialogKey(false)) },
        onDialogToDialogClicked = { navigator.navigate(BlockingDialogKey(true)) },
        onBlockingBottomSheetClicked = { rootNavigator.navigate(BlockingBottomSheetKey()) },
    )
}

@Composable
private fun DialogsContent(
    onCancelableDialogClicked: () -> Unit = {},
    onBlockingDialogClicked: () -> Unit = {},
    onDialogToDialogClicked: () -> Unit = {},
    onBlockingBottomSheetClicked: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Dialogs") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            state = lazyListState,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Button(
                    modifier = Modifier
                        .widthIn(min = 300.dp),
                    onClick = onCancelableDialogClicked
                ) {
                    Text(text = "Cancelable Dialog")
                }
            }

            item {
                Button(
                    modifier = Modifier
                        .widthIn(min = 300.dp),
                    onClick = onBlockingDialogClicked
                ) {
                    Text(text = "Blocking Dialog")
                }
            }
            item {
                Button(
                    modifier = Modifier
                        .widthIn(min = 300.dp),
                    onClick = onDialogToDialogClicked
                ) {
                    Text(text = "Dialog To Dialog")
                }
            }
            item {
                Button(
                    modifier = Modifier
                        .widthIn(min = 300.dp),
                    onClick = onBlockingBottomSheetClicked
                ) {
                    Text(text = "Blocking Bottom Sheet")
                }
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
private fun DialogsContentPreview() = AppTheme {
    DialogsContent()
}
