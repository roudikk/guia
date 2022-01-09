package com.roudikk.navigator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.android.awaitFrame

/**
 * Used to store in-memory navigators so you can query for a navigator
 * regardless of the level of navigation nesting.
 *
 * @see findNavigator()
 */
private val navigators: HashMap<String, Navigator> = hashMapOf()

private val LocalNavigator = compositionLocalOf<Navigator?> {
    null
}

private val LocalParentNavigator = compositionLocalOf<Navigator?> { null }

@ExperimentalNavigatorApi
@Composable
fun NavHost(
    key: String = Navigator.defaultKey,
    navigationConfig: NavigationConfig,
    content: @Composable () -> Unit
) {
    val parentNavigator = LocalNavigator.current

    val navigator = rememberSaveable(key = key, saver = Navigator.Saver) {
        Navigator().apply {
            initialize(navigationConfig)
        }
    }

    navigators[key] = navigator

    CompositionLocalProvider(
        LocalNavigator provides navigator,
        LocalParentNavigator provides parentNavigator
    ) {
        content()
    }
}

@ExperimentalNavigatorApi
@Composable
fun findNavigator(key: String? = null): Navigator {
    if (key == null) return checkNotNull(LocalNavigator.current) {
        "No navigator has been registered, call NavHost before using findNavigator"
    }

    return requireNotNull(navigators[key]) {
        "No navigator has been registered for key: $key, call NavHost with given key"
    }
}

@ExperimentalNavigatorApi
@Composable
fun findParentNavigator(): Navigator? {
    return LocalParentNavigator.current
}

@ExperimentalNavigatorApi
@Composable
fun findDefaultNavigator() = findNavigator(Navigator.defaultKey)

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)
@ExperimentalNavigatorApi
@Composable
fun NavContainer(
    modifier: Modifier = Modifier,
    bottomSheetSetup: BottomSheetSetup = BottomSheetSetup()
) {
    val navigator = findNavigator()
    val parentNavigator = findParentNavigator()

    val savableStateHolder = rememberSaveableStateHolder()

    val state by navigator.stateFlow.collectAsState()
    val parentState = parentNavigator?.stateFlow?.collectAsState()

    val currentDestination = state.currentStack.destinations.last()

    val allowBottomSheetStateChange = currentDestination.navigationNode !is BottomSheet ||
            currentDestination.navigationNode.bottomSheetOptions.dismissOnHidden

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { allowBottomSheetStateChange }
    )

    val parentShowingBottomSheet = parentState?.value?.currentStack?.destinations?.last()
        ?.navigationNode is BottomSheet

    BackHandler(
        navigator.canGoBack()
                && state.overrideBackPress
                && !parentShowingBottomSheet
    ) {
        navigator.popBackStack()
    }

    AnimatedContent(
        targetState = state.currentStack.destinations
            .last { it.navigationNode is Screen },
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        transitionSpec = {
            state.transitionPair.enter.toComposeEnterTransition() with
                    state.transitionPair.exit.toComposeExitTransition()
        }
    ) { destination ->
        savableStateHolder.SaveableStateProvider(
            destination.id
        ) { with(destination.navigationNode) { Content() } }
    }

    ModalBottomSheetLayout(
        modifier = Modifier
            .testTag("NavContainerBottomSheet")
            .fillMaxSize(),
        sheetState = bottomSheetState,
        scrimColor = bottomSheetSetup.scrimColor,
        sheetShape = RoundedCornerShape(0.dp),
        sheetBackgroundColor = Color.Transparent,
        sheetElevation = 0.dp,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                bottomSheetSetup.bottomSheetContainer {
                    val localDensity = LocalDensity.current
                    val bottomSheetDestination = currentDestination.takeIf {
                        it.navigationNode is BottomSheet
                    }
                    var contentHeightPixels by remember {
                        mutableStateOf(with(localDensity) {
                            1.dp.toPx()
                        })
                    }
                    val contentHeightDp = with(localDensity) { contentHeightPixels.toDp() }

                    AnimatedContent(
                        modifier = Modifier.fillMaxWidth(),
                        targetState = bottomSheetDestination to currentDestination,
                        transitionSpec = {
                            // Only animate bottom sheet content when navigating between
                            // bottom sheet destinations.
                            if (
                                initialState.second.navigationNode !is BottomSheet
                                && targetState.first != null
                            ) {
                                EnterTransition.None
                            } else {
                                state.transitionPair.enter.toComposeEnterTransition()
                            } with if (
                                initialState.first != null
                                && targetState.second.navigationNode !is BottomSheet
                            ) {
                                ExitTransition.None
                            } else {
                                state.transitionPair.exit.toComposeExitTransition()
                            }
                        }
                    ) { (targetDestination, _) ->
                        if (targetDestination != null) {
                            Box(
                                modifier = Modifier.onGloballyPositioned {
                                    contentHeightPixels = it.size.height.toFloat()
                                }
                            ) {
                                savableStateHolder.SaveableStateProvider(
                                    key = targetDestination.id
                                ) {
                                    with(targetDestination.navigationNode) { Content() }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .height(contentHeightDp)
                            )
                        }

                        LaunchedEffect(transition.isRunning) {
                            repeat(3) { awaitFrame() }
                            if (targetDestination == null && !transition.isRunning) {
                                contentHeightPixels = with(localDensity) { 1.dp.toPx() }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }

    if (currentDestination.navigationNode is Dialog) {
        Dialog(
            onDismissRequest = { navigator.popBackStack() },
            properties = with(currentDestination.navigationNode.dialogOptions) {
                DialogProperties(
                    dismissOnBackPress = dismissOnBackPress,
                    dismissOnClickOutside = dismissOnClickOutside,
                    // This is used because there's a bug with updating content of a Dialog
                    usePlatformDefaultWidth = false,
                )
            }
        ) {
            AnimatedContent(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .animateContentSize(),
                targetState = currentDestination,
                transitionSpec = {
                    state.transitionPair.enter.toComposeEnterTransition() with
                            state.transitionPair.exit.toComposeExitTransition()
                }
            ) { destination ->
                savableStateHolder.SaveableStateProvider(
                    destination.id,
                ) { with(destination.navigationNode) { Content() } }
            }
        }
    }

    LaunchedEffect(currentDestination) {
        if (currentDestination.navigationNode is BottomSheet) {
            bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
        } else {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(bottomSheetState.isVisible) {
        if (!bottomSheetState.isVisible && currentDestination.navigationNode is BottomSheet) {
            navigator.popBackStack()
        }
    }
}

/**
 * Provide extra bottom sheet options.
 *
 * @property scrimColor the scrim color behind the bottom sheet and on top of the content behind it.
 * @property bottomSheetContainer use this when the navigation requires animating between content
 * of two bottom sheets using [Navigator.navigate] instead of animating the transitions between 2
 * bottom sheets, this container will be the parent of all the bottom sheets defined in the app.
 */
data class BottomSheetSetup(
    val scrimColor: Color = Color.Black.copy(alpha = 0.4F),

    val bottomSheetContainer: @Composable (
        content: @Composable () -> Unit
    ) -> Unit = { content ->
        Box {
            content()
        }
    }
)

/**
 * Compose saver for [Navigator].
 *
 * Saves and restores the state of a navigator.
 */
val Navigator.Companion.Saver: Saver<Navigator, *>
    get() = Saver(
        save = { it.save() },
        restore = { Navigator().apply { restore(it) } }
    )

@RequiresOptIn("This API is experimental and is likely to change in the future.")
annotation class ExperimentalNavigatorApi