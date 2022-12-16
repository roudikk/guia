@file:Suppress("unused")

package com.roudikk.navigator.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.roudikk.navigator.Navigator

internal val LocalNavigator = compositionLocalOf<Navigator?> { null }
internal val LocalParentNavigator = compositionLocalOf<Navigator?> { null }

@Composable
fun findNavigator(): Navigator? {
    return LocalNavigator.current
}

@Composable
fun requireNavigator(): Navigator {
    return requireNotNull(LocalNavigator.current) {
        "requireNavigator() must be called in a NavigationNode hosted in a NavContainer."
    }
}

@Composable
fun findParentNavigator(): Navigator? {
    return LocalParentNavigator.current
}

@Composable
fun requireParentNavigator(): Navigator {
    return requireNotNull(LocalParentNavigator.current)
}
