package com.roudikk.navigator.sample.feature.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.Window
import androidx.compose.ui.graphics.vector.ImageVector
import com.roudikk.navigator.navhost.StackKey
import com.roudikk.navigator.sample.feature.custom.api.CustomStackKey
import com.roudikk.navigator.sample.feature.dialogs.api.DialogsStackKey
import com.roudikk.navigator.sample.feature.home.api.HomeStackKey
import com.roudikk.navigator.sample.feature.nested.api.NestedStackKey

enum class BottomNavTab(
    val label: String,
    val stackKey: StackKey,
    val tag: String,
    val icon: ImageVector
) {
    Home(
        label = "Home",
        stackKey = HomeStackKey,
        tag = "tab_home",
        icon = Icons.Default.Home
    ),
    Nested(
        label = "Nested",
        stackKey = NestedStackKey,
        tag = "tab_nested",
        icon = Icons.Default.StackedBarChart
    ),
    Dialogs(
        label = "Dialogs",
        stackKey = DialogsStackKey,
        tag = "tab_dialogs",
        icon = Icons.Default.Window
    ),
    Custom(
        label = "Custom",
        stackKey = CustomStackKey,
        tag = "tab_custom",
        icon = Icons.Default.Grid4x4
    )
}
