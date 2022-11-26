package com.roudikk.navigator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.roudikk.navigator.compose.BottomSheetSetup
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.StackKey

internal val LocalNavHost = staticCompositionLocalOf<NavHost?> { error("Must be provided") }

@Composable
fun findNavHost() = LocalNavHost.current

@Composable
fun requireNavHost() = requireNotNull(LocalNavHost.current) {
    "No NavHost found, Call requireNavHost inside a NavigationKey hosted by a NavHost."
}

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

    val keyedNavigators: List<Pair<StackKey, Navigator>>
        get() = navigatorKeyMap.map { it.key to it.value }

    fun setActive(stackKey: StackKey) {
        require(navigatorKeyMap.containsKey(stackKey)) {
            "$stackKey does not exist in this NavHost, must be provided when calling rememberNavHost"
        }

        activeKey = stackKey
    }
}

@Composable
fun NavHost.DefaultStackBackHandler(stackKey: StackKey) {
    BackHandler(stackKey != activeKey) {
        setActive(stackKey)
    }
}

// A1 A2 A3
// A3 A0 A1 A2

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

    LaunchedEffect(key1 = backKey) {
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHost.NavContainer(
    modifier: (StackKey) -> Modifier = { Modifier },
    transitionSpec: AnimatedContentScope<StackKey>.() -> ContentTransform = {
        EnterTransition.None with ExitTransition.None
    },
    bottomSheetSetup: (StackKey) -> BottomSheetSetup
) {
    CompositionLocalProvider(LocalNavHost provides this) {
        AnimatedContent(
            targetState = activeKey,
            transitionSpec = transitionSpec
        ) { targetKey ->
            saveableStateHolder.SaveableStateProvider(key = targetKey) {
                remember(targetKey) { requireNotNull(navigatorKeyMap[targetKey]) }.NavContainer(
                    modifier = modifier(targetKey),
                    bottomSheetOptions = bottomSheetSetup(targetKey),
                )
            }
        }
    }
}
