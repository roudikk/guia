package com.roudikk.navigator.navhost

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.roudikk.navigator.core.StackKey

@Composable
fun NavHost.DefaultStackBackHandler(stackKey: StackKey) {
    BackHandler(stackKey != activeKey) {
        setActive(stackKey)
    }
}

@Composable
fun NavHost.CrossStackBackHandler() {
    var entries by remember { mutableStateOf(emptyList<StackKey>()) }
    var backKey by remember { mutableStateOf<StackKey?>(null) }
//
//    LaunchedEffect(activeNavigator.backStack) {
//        val newEntries = mutableListOf<Pair<StackKey, NavigationKey>>().apply {
//            addAll(entries.filter { (_, key) ->
//                navigatorKeyMap.values.any { it.backStack.contains(key) }
//            })
//        }
//
//        activeNavigator.backStack
//            .filter { entry -> !entries.any { it.second == entry } }
//            .forEach {
//
//            }
//
//        entries = newEntries
//    }

    LaunchedEffect(activeKey) {
        entries = entries.toMutableList().apply {
            add(activeKey)
        }
    }

    LaunchedEffect(backKey) {
        backKey?.let { key ->
            entries = entries.dropLast(1)
            setActive(key)
            backKey = null
        }
    }

    BackHandler(entries.size > 1) {
        backKey = entries.last()
    }
}