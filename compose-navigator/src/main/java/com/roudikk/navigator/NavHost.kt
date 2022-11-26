package com.roudikk.navigator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

    fun setActive(stackKey: StackKey) {
        require(navigatorKeyMap.containsKey(stackKey)) {
            "$stackKey does not exist in this NavHost, must be provided when calling rememberNavHost"
        }

        activeKey = stackKey
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHost.NavContainer(
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentScope<StackKey>.() -> ContentTransform = {
        EnterTransition.None with ExitTransition.None
    },
    bottomSheetOptions: com.roudikk.navigator.compose.BottomSheetOptions
) {
    AnimatedContent(
        targetState = activeKey,
        transitionSpec = transitionSpec
    ) { targetKey ->
        saveableStateHolder.SaveableStateProvider(key = targetKey) {
            remember(targetKey) { requireNotNull(navigatorKeyMap[targetKey]) }.NavContainer(
                modifier = modifier,
                bottomSheetOptions = bottomSheetOptions,
            )
        }
    }
}
