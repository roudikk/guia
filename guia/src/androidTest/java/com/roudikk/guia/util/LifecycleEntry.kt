package com.roudikk.guia.util

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStore
import com.roudikk.guia.core.BackstackEntry
import com.roudikk.guia.lifecycle.LifecycleEntry

@Composable
fun rememberLifecycleEntry(
    backstackEntry: BackstackEntry,
    saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    viewModelStore: ViewModelStore = ViewModelStore()
): LifecycleEntry {
    val application = LocalContext.current.applicationContext as Application

    return remember {
        LifecycleEntry(
            backstackEntry = backstackEntry,
            saveableStateHolder = saveableStateHolder,
            application = application,
            viewModelStore = viewModelStore
        )
    }
}
