package com.roudikk.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import com.roudikk.navigator.core.NavigationKey
import kotlinx.parcelize.Parcelize

class TestSaveableStateHolder : SaveableStateHolder {

    @Suppress("TestFunctionName")
    @Composable
    override fun SaveableStateProvider(key: Any, content: @Composable () -> Unit) = Unit
    override fun removeState(key: Any) = Unit
}

@Parcelize
class TestNavigationKey : NavigationKey
