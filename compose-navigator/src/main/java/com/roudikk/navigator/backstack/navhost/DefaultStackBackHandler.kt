package com.roudikk.navigator.backstack.navhost

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.roudikk.navigator.backstack.NavBackHandler
import com.roudikk.navigator.containers.NavContainer
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackKey

/**
 * [BackHandler] for a [NavHost] that falls back to [stackKey] whenever the [NavHost.currentNavigator]
 * is no longer handling the back press.
 *
 * Make sure to call this before [NavHost.NavContainer] so navigators can override the back press.
 */
@Composable
fun NavHost.DefaultStackBackHandler(stackKey: StackKey) {
    require(stackEntries.any { it.stackKey == stackKey }) {
        "$stackKey is not part of the nav host."
    }

    NavBackHandler(stackKey != currentEntry?.stackKey) {
        setActive(stackKey)
    }
}
