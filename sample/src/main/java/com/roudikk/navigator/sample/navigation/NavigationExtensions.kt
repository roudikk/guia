package com.roudikk.navigator.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import com.roudikk.navigator.Navigator

val LocalRootNavigator = staticCompositionLocalOf<Navigator> { error("Must be provided.") }

@Composable
fun requireRootNavigator(): Navigator {
    return requireNotNull(LocalRootNavigator.current)
}

val LocalNavHostViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner> {
    error("Must be provided")
}
