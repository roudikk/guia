package com.roudikk.navigator.navhost

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun NavHost.DefaultStackBackHandler(stackKey: StackKey) {
    BackHandler(stackKey != currentEntry?.stackKey) {
        setActive(stackKey)
    }
}
