package com.roudikk.navigator.sample.ui.screens.nested

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.NavigatorRulesBuilder
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.extensions.canGoBack
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popBackstack
import com.roudikk.navigator.navhost.StackKey
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
object NestedStackKey : StackKey

@Parcelize
class NestedKey(val count: Int) : NavigationKey {

    override fun tag(): String = tagFor(count)

    companion object {
        fun tagFor(count: Int) = "NestedKey_$count"
    }
}

fun NavigatorRulesBuilder.nestedNavigation() {
    screen<NestedKey> { NestedScreen(count = it.count) }
}

@Composable
private fun NestedScreen(
    count: Int
) {
    val navigator = requireNavigator()
    val canGoBack by navigator.canGoBack()
    NestedContent(
        count = count,
        canGoBack = canGoBack,
        onRemoveClicked = navigator::popBackstack,
        onAddClicked = { navigator.navigate(NestedKey(count + 1)) }
    )
}

@Composable
private fun NestedContent(
    count: Int,
    canGoBack: Boolean,
    onRemoveClicked: () -> Unit = {},
    onAddClicked: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        IconButton(
            enabled = canGoBack,
            onClick = onRemoveClicked
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Remove"
            )
        }

        Surface(
            tonalElevation = 10.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.size(100.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        IconButton(
            onClick = onAddClicked
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
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
    NestedContent(
        count = 4,
        canGoBack = true
    )
}
