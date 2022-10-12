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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.animation.NavTransition
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.navigation.findDefaultNavigator
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogsScreen : Screen {

    @Composable
    override fun Content() = DialogsContent()
}

@Composable
private fun DialogsContent(
    navigator: Navigator = requireNavigator(),
    defaultNavigator: Navigator = findDefaultNavigator()
) {
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Dialogs",
                lazyListState = lazyListState
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
                    onClick = {
                        navigator.navigate(CancelableDialog(false))
                    }
                ) {
                    Text(text = "Cancelable Dialog")
                }
            }

            item {
                Button(
                    modifier = Modifier
                        .widthIn(min = 300.dp),
                    onClick = {
                        navigator.navigate(BlockingDialog(false))
                    }
                ) {
                    Text(text = "Blocking Dialog")
                }
            }
            item {
                Button(
                    modifier = Modifier
                        .widthIn(min = 300.dp),
                    onClick = {
                        navigator.navigate(BlockingDialog(true))
                    }
                ) {
                    Text(text = "Dialog To Dialog")
                }
            }
            item {
                Button(
                    modifier = Modifier
                        .widthIn(min = 300.dp),
                    onClick = {
                        defaultNavigator.navigate(BlockingBottomSheet(), NavTransition.None)
                    }
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
    DialogsContent(
        navigator = rememberNavigator(),
        defaultNavigator = rememberNavigator()
    )
}
