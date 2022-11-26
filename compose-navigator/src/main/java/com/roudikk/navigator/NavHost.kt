package com.roudikk.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.StackKey

internal val LocalNavHost = staticCompositionLocalOf<NavHost?> { error("Must be provided") }

@Composable
fun findNavHost() = LocalNavHost.current

private fun navHostSaver(
    saveableStateHolder: SaveableStateHolder,
    navigatorKeyMap: HashMap<StackKey, Navigator>
) = Saver<NavHost, StackKey>(
    save = { it.activeKey },
    restore = { NavHost(it, saveableStateHolder, navigatorKeyMap) }
)

@Composable
fun rememberNavHost(
    initialKey: StackKey,
    navigatorKeyMap: HashMap<StackKey, Navigator>
): NavHost {
    val saveableStateHolder = rememberSaveableStateHolder()
    return rememberSaveable(
        saver = navHostSaver(saveableStateHolder, navigatorKeyMap)
    ) {
        NavHost(
            initialKey = initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorKeyMap = navigatorKeyMap
        )
    }
}

class NavHost(
    initialKey: StackKey,
    internal val saveableStateHolder: SaveableStateHolder,
    private val navigatorKeyMap: HashMap<StackKey, Navigator>
) {

    init {
        require(navigatorKeyMap.contains(initialKey))
    }

    var activeKey by mutableStateOf(initialKey)
        private set

    val activeNavigator by derivedStateOf {
        requireNotNull(navigatorKeyMap[activeKey])
    }

    fun setActive(stackKey: StackKey) {
        require(navigatorKeyMap.containsKey(stackKey)) {
            "$stackKey does not exist in this NavHost, must be provided when calling rememberNavHost"
        }

        activeKey = stackKey
    }
}

@Composable
fun NavHost.NavContainer(
    modifier: Modifier = Modifier,
    bottomSheetOptions: com.roudikk.navigator.compose.BottomSheetOptions
) {
    saveableStateHolder.SaveableStateProvider(key = activeKey) {
        activeNavigator.NavContainer(
            modifier = modifier,
            bottomSheetOptions = bottomSheetOptions,
        )
    }
}
