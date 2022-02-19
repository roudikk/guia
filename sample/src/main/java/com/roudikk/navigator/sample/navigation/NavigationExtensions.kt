package com.roudikk.navigator.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import com.roudikk.navigator.Navigator

val LocalDefaultNavigator = staticCompositionLocalOf<Navigator> { error("Must be provided.") }

@Composable
fun findDefaultNavigator(): Navigator {
    return LocalDefaultNavigator.current
}

val LocalNavHostViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner> {
    error("Must be provided")
}
