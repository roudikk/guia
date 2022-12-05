package com.roudikk.navigator.navhost

import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.core.StackKey

internal fun NavHostSaver(
    saveableStateHolder: SaveableStateHolder,
    navigatorKeyMap: HashMap<StackKey, Navigator>
) = Saver<NavHost, StackKey>(
    save = { it.activeKey },
    restore = { NavHost(it, saveableStateHolder, navigatorKeyMap) }
)
