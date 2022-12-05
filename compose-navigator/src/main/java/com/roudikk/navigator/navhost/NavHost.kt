package com.roudikk.navigator.navhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.StackKey

@Composable
fun rememberNavHost(
    initialKey: StackKey,
    navigatorKeyMap: HashMap<StackKey, Navigator>,
    initialize: @DisallowComposableCalls (NavHost) -> Unit = {}
): NavHost {
    val saveableStateHolder = rememberSaveableStateHolder()
    return rememberSaveable(
        saver = NavHostSaver(saveableStateHolder, navigatorKeyMap)
    ) {
        NavHost(
            initialKey = initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorKeyMap = navigatorKeyMap
        ).apply(initialize)
    }
}

class NavHost(
    initialKey: StackKey,
    internal val saveableStateHolder: SaveableStateHolder,
    internal val navigatorKeyMap: HashMap<StackKey, Navigator>
) {

    init {
        require(navigatorKeyMap.contains(initialKey))
    }

    var activeKey by mutableStateOf(initialKey)
        private set

    val activeNavigator by derivedStateOf {
        requireNotNull(navigatorKeyMap[activeKey])
    }

    val keyedNavigators: List<Pair<StackKey, Navigator>>
        get() = navigatorKeyMap.map { it.key to it.value }

    fun setActive(stackKey: StackKey) {
        require(navigatorKeyMap.containsKey(stackKey)) {
            "$stackKey does not exist in this NavHost, must be provided when calling rememberNavHost"
        }

        activeKey = stackKey
    }
}
