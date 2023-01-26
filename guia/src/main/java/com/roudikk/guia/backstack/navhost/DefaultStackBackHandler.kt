package com.roudikk.guia.backstack.navhost

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.roudikk.guia.backstack.NavBackHandler
import com.roudikk.guia.containers.NavContainer
import com.roudikk.guia.navhost.NavHost
import com.roudikk.guia.navhost.StackKey

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
