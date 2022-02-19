package com.roudikk.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.roudikk.navigator.animation.NavEnterExitTransition
import com.roudikk.navigator.animation.NavTransition
import com.roudikk.navigator.animation.to
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.*
import com.roudikk.navigator.core.NavigationNode.Companion.key
import com.roudikk.navigator.core.NavigationNode.Companion.resultsKey
import com.roudikk.navigator.savedstate.NavigatorSaver
import com.roudikk.navigator.savedstate.NavigatorState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Remembers and returns a [Navigator].
 *
 * @see [NavigationConfig] for the different navigator configurations.
 *
 * @param navigationConfig, navigation config for the [Navigator], this cannot be changed later.
 */
@Composable
fun rememberNavigator(
    navigationConfig: NavigationConfig = NavigationConfig.SingleStack(EmptyNavigationNode),
    initializer: @DisallowComposableCalls (Navigator) -> Unit = {}
) = rememberSaveable(saver = NavigatorSaver) { Navigator(navigationConfig).apply(initializer) }

/**
 * Remembers and returns a single stack [Navigator].
 *
 * @param initialNavigationNode, the initial navigation node to render.
 * If the navigator should start with an empty node to be replaced later, use [EmptyNavigationNode]
 * @param defaultTransition, default transition used when no transition is given when navigating.
 */
@Composable
fun rememberNavigator(
    initialNavigationNode: NavigationNode,
    defaultTransition: NavTransition = NavTransition.None,
    initializer: @DisallowComposableCalls (Navigator) -> Unit = {}
) = rememberNavigator(
    navigationConfig = NavigationConfig.SingleStack(
        initialNavigationNode = initialNavigationNode,
        defaultTransition = defaultTransition,
    ),
    initializer = initializer
)

/**
 * Main component of the navigation system.
 *
 * To start use one of the [rememberNavigator] functions to create a navigator instance.
 * To render the state of a [Navigator] use a [NavContainer].
 * To define a screen use one of [Screen], [Dialog] or [BottomSheet].
 */
class Navigator internal constructor(
    @PublishedApi
    internal val navigationConfig: NavigationConfig = NavigationConfig
        .SingleStack(EmptyNavigationNode)
) {

    /**
     * Coroutine scope used for [Navigator] coroutine operations.
     *
     * @see [currentStackKeyFlow]
     * @see [resultsFlow]
     * @see [sendResult]
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /**
     * The navigation stack history across all the stacks defined in [NavigationConfig.SingleStack]
     * or [NavigationConfig.MultiStack].
     *
     * @see [NavHistoryEntry] for more information on how this works.
     */
    private var mutableNavHistory = mutableListOf<NavHistoryEntry>()
    internal val navHistory: List<NavHistoryEntry>
        get() = mutableNavHistory

    /**
     * The current state of the navigator as a [StateFlow].
     */
    private val mutableStateFlow = MutableStateFlow(
        NavigationState(
            navigationStacks = mutableStateListOf(DefaultNavigationStack),
            currentStackKey = DefaultNavigationStack.key,
            transition = navigationConfig.defaultTransition.enter
                    to navigationConfig.defaultTransition.exit,
            overrideBackPress = true
        )
    )
    val stateFlow: StateFlow<NavigationState> = mutableStateFlow

    /**
     * The current state of the navigator.
     */
    val currentState: NavigationState
        get() = mutableStateFlow.value

    /**
     * Current stack key.
     */
    val currentStackKey: StackKey
        get() = stateFlow.value.currentStackKey
    val currentStackKeyFlow: StateFlow<StackKey> = stateFlow.map {
        it.currentStackKey
    }.stateIn(scope, SharingStarted.Lazily, stateFlow.value.currentStackKey)

    /**
     * Current stack's current node key.
     */
    val currentNodeKey: String
        get() = stateFlow.value.currentStack.currentNodeKey

    /**
     * Current destinations for current stack.
     */
    private val currentDestinations
        get() = currentState.currentStack.destinations.toMutableList()

    /**
     * [resultsChannel] is used for guaranteed delivery of the result
     * [resultsFlow] used to collect results.
     *
     * @see sendResult
     * @see results
     */
    private val resultsChannel = Channel<Pair<Any, Any>>()
    private val resultsFlow: Flow<Pair<Any, Any>> = resultsChannel
        .receiveAsFlow()
        .shareIn(scope, started = SharingStarted.WhileSubscribed())

    init {
        initialize(navigationConfig)
    }

    private fun initialize(navigationConfig: NavigationConfig) {
        mutableNavHistory.clear()
        when (navigationConfig) {
            is NavigationConfig.MultiStack -> initialize(
                navigationStacks = navigationConfig.entries.map {
                    NavigationStack(
                        key = it.key,
                        destinations = listOf(
                            Destination(
                                navigationNode = it.initialNavigationNode,
                                transition = navigationConfig.defaultTransition
                            )
                        )
                    )
                },
                initialStackKey = navigationConfig.initialStackKey
            )
            is NavigationConfig.SingleStack -> initialize(
                initialNavigationNode = navigationConfig.initialNavigationNode
            )
        }
    }

    /**
     * Initializes the [Navigator] with a single stack using [DefaultNavigationStack]
     */
    private fun initialize(initialNavigationNode: NavigationNode) {
        val destination = Destination(
            navigationNode = initialNavigationNode,
            transition = navigationConfig.defaultTransition
        )

        mutableStateFlow.value = NavigationState(
            navigationStacks = listOf(
                DefaultNavigationStack.copy(destinations = listOf(destination))
            ),
            currentStackKey = DefaultNavigationStack.key,
            transition = navigationConfig.defaultTransition.enter to
                    navigationConfig.defaultTransition.exit,
            overrideBackPress = true
        )

        mutableNavHistory.add(destination.navHistoryEntry())
    }

    /**
     * Initializes the [Navigator] with multiple stacks, [initialStackKey] must be part
     * of [navigationStacks].
     */
    private fun initialize(
        navigationStacks: List<NavigationStack>,
        initialStackKey: StackKey
    ) {
        check(navigationStacks.any { it.key == initialStackKey }) {
            "Initial stack must be in the list of provided navigation stacks"
        }

        mutableStateFlow.value = NavigationState(
            navigationStacks = navigationStacks,
            currentStackKey = initialStackKey,
            transition = navigationConfig.defaultTransition.enter to
                    navigationConfig.defaultTransition.exit,
            overrideBackPress = true
        )

        mutableNavHistory.add(currentDestinations.last().navHistoryEntry())
    }

    /**
     * Navigate to a given destination.
     *
     * @param navigationNode, the new node to navigate to.
     * @param transition, transition animation.
     *
     *  The new node is added as a new [Destination] along with its [transition] on top
     *  of [NavigationStack.destinations] in [NavigationState.currentStack].
     */
    fun navigate(
        navigationNode: NavigationNode,
        transition: NavTransition = navigationConfig.defaultTransition
    ) {
        val destinations = currentDestinations
        val destination = Destination(navigationNode, transition)
        destinations.add(destination)
        mutableNavHistory.add(destination.navHistoryEntry())
        updateStateWithDestinations(destinations, false)
    }

    /**
     * Replaces the current last destination in the current stack.
     *
     * @param navigationNode, the new navigation node.
     * @param transition, transition animation.
     */
    fun replaceLast(
        navigationNode: NavigationNode,
        transition: NavTransition = navigationConfig.defaultTransition
    ) {
        val destinations = currentDestinations
        val destination = Destination(navigationNode, transition)
        destinations.removeLast()
        mutableNavHistory.removeLast()
        destinations.add(destination)
        mutableNavHistory.add(destination.navHistoryEntry())
        updateStateWithDestinations(destinations, false)
    }

    /**
     * Replace all entries matching [predicate].
     *
     * @param navigationNode, the new navigation node to replace root.
     * @param transition, transition animation.
     * @param inclusive, pop the last [NavigationNode] not matching [predicate] too.
     * @param predicate, return true for the up-to [NavigationNode].
     */
    fun replaceUpTo(
        navigationNode: NavigationNode,
        transition: NavTransition = navigationConfig.defaultTransition,
        inclusive: Boolean = false,
        predicate: (NavigationNode) -> Boolean,
    ) {
        var destinations = currentDestinations
        val destination = Destination(navigationNode, transition)

        destinations = destinations
            .dropLastWhile { !predicate(it.navigationNode) }
            .toMutableList()

        if (inclusive) destinations.removeLast()

        mutableNavHistory.removeAll { entry ->
            !destinations.any { it.id == entry.destinationId } &&
                    entry.stackKey == currentStackKey
        }

        destinations.add(destination)
        mutableNavHistory.add(destination.navHistoryEntry())
        updateStateWithDestinations(destinations, false)
    }

    /**
     * Replace all entries in the back stack until the first occurrence of [key].
     *
     * @param navigationNode, the new navigation node to replace root.
     * @param transition, transition animation.
     * @param inclusive, pop the [NavigationNode] with [key] too.
     */
    inline fun <reified T : NavigationNode> replaceUpTo(
        navigationNode: NavigationNode,
        transition: NavTransition = navigationConfig.defaultTransition,
        inclusive: Boolean = false
    ) = replaceUpTo(
        predicate = { it.key == key<T>() },
        navigationNode = navigationNode,
        transition = transition,
        inclusive = inclusive
    )

    /**
     * Moves the navigation node matching [predicate] to the top.
     *
     * @param transition, transition animation.
     * @param matchLast, whether or not to matching from the top of the back stack ro the start.
     * @param predicate, whether or not the [NavigationNode] matches.
     *
     * @return true if a destination was found in the back stack.
     */
    fun moveToTop(
        matchLast: Boolean = true,
        transition: NavTransition? = navigationConfig.defaultTransition,
        predicate: (NavigationNode) -> Boolean
    ): Boolean {
        val destinations = currentDestinations

        val destination = if (matchLast) {
            destinations.findLast { predicate(it.navigationNode) }
        } else {
            destinations.find { predicate(it.navigationNode) }
        }

        if (destinations.last() == destination) return true

        if (destination != null) {
            destinations.remove(destination)
            destinations.add(
                destination.copy(
                    transition = transition ?: navigationConfig.defaultTransition
                )
            )

            mutableNavHistory.removeAll { it.destinationId == destination.id }
            mutableNavHistory.add(destination.navHistoryEntry())
            updateStateWithDestinations(destinations, false)
            return true
        }
        return false
    }

    /**
     * Moves the navigation node matching [key] to the top.
     *
     * @param transition, transition animation.
     * @param matchLast, whether or not to matching from the top of the back stack ro the start.
     *
     * @return true if a destination was found in the back stack.
     */
    inline fun <reified T : NavigationNode> moveToTop(
        matchLast: Boolean = true,
        transition: NavTransition? = null,
    ) = moveToTop(
        predicate = { it.key == key<T>() },
        transition = transition,
        matchLast = matchLast
    )

    /**
     * Navigates to a single instance of [NavigationNode].
     *
     * @param useExistingInstance, if true, then use an existing navigation node if any matches [key].
     * @param navigationNode, the new navigation node.
     * @param transition, transition animation.
     */
    fun singleInstance(
        navigationNode: NavigationNode,
        useExistingInstance: Boolean = true,
        transition: NavTransition = navigationConfig.defaultTransition
    ) {
        val destinations = currentDestinations
        val existingDestination = if (useExistingInstance) {
            destinations.lastOrNull { it.navigationNode.key == navigationNode.key }
        } else {
            null
        }
        destinations.removeAll { it.navigationNode.key == navigationNode.key }
        mutableNavHistory.removeAll { entry ->
            !destinations.any { it.id == entry.destinationId } &&
                    entry.stackKey == currentStackKey
        }

        val destination = existingDestination ?: Destination(
            navigationNode = navigationNode,
            transition = transition
        )

        destinations.add(destination)
        mutableNavHistory.add(destination.navHistoryEntry())
        updateStateWithDestinations(destinations, false)
    }

    /**
     * Navigates to [NavigationNode] if the current top destination doesn't have the same [key].
     *
     * @param navigationNode, the navigation node.
     * @param transition, transition animation.
     */
    fun singleTop(
        navigationNode: NavigationNode,
        transition: NavTransition = navigationConfig.defaultTransition
    ) {
        if (currentNodeKey == navigationNode.key) return
        navigate(navigationNode, transition)
    }

    /**
     * Check if any node in the back stack matches predicate.
     *
     * @param predicate, whether or not the given navigation node satisfies the condition.
     */
    fun any(predicate: (NavigationNode) -> Boolean): Boolean {
        return currentDestinations.any { predicate(it.navigationNode) }
    }

    /**
     * Pop to a specific navigation node
     *
     * @param key, used to find the navigation node in history matching this key. If none exist,
     * nothing happens.
     * @param inclusive, if set to true, the navigation node with given [key] will also be removed
     * from history.
     *
     * @return true, if the navigation state changed after popping.
     * @return false, if the navigation state is unchanged.
     */
    fun popTo(key: String, inclusive: Boolean = false): Boolean {
        var destinations = currentDestinations
        val destination = destinations.find { it.navigationNode.key == key }
            ?: return false

        destinations = destinations.dropLastWhile { it != destination }.toMutableList()
        mutableNavHistory = mutableNavHistory.dropLastWhile { it.destinationId != destination.id }
            .toMutableList()

        if (inclusive) {
            destinations.removeLast()
            mutableNavHistory.removeLast()
        }

        return updateStateWithDestinations(destinations, true)
    }

    /**
     * Convenience method to pop to a node using its [NavigationNode.Companion.key] as key.
     *
     * This only works if [NavigationNode.key] was not overridden.
     */
    inline fun <reified T : NavigationNode> popTo(inclusive: Boolean = false) =
        popTo(key<T>(), inclusive)

    /**
     * Pops all the way back to the root [NavigationNode] for [NavigationState.currentStack].
     */
    fun popToRoot() {
        val destination = currentDestinations[0]
        mutableNavHistory = mutableNavHistory.dropLastWhile {
            it.stackKey == currentStackKey && it.destinationId != destination.id
        }.toMutableList()
        updateStateWithDestinations(listOf(destination), true)
    }

    /**
     * Clears the history for [NavigationState.currentStack] and sets a new root node.
     *
     * @param navigationNode, the new root node for the current stack.
     * @param transition, transition animation.
     */
    fun setRoot(
        navigationNode: NavigationNode,
        transition: NavTransition = navigationConfig.defaultTransition
    ) {
        val destinations = currentDestinations
        val destination = Destination(navigationNode, transition)
        destinations.clear()
        destinations.add(destination)
        mutableNavHistory.removeAll { it.stackKey == currentStackKey }
        mutableNavHistory.add(destination.navHistoryEntry())
        updateStateWithDestinations(destinations, false)
    }

    /**
     * Navigates to a given [StackKey]
     *
     * @param key, the new stack navigation key, must be present in [NavigationState.navigationStacks].
     * @param transition, enter/exit transitions for switching between stacks.
     * @param addToKeyHistory, when this is true and navigator is using [BackStackStrategy.CrossStackHistory].
     * when navigating to a different stack and then pressing the back button (Assuming
     * [NavigationState.overrideBackPress] is true) the navigator will navigate back to the previous
     * back stack, however if it's set to false, the navigator will not go back to previous stack.
     */
    private fun navigateToStack(
        key: StackKey,
        transition: NavEnterExitTransition = requireNotNull(navigationConfig as? NavigationConfig.MultiStack) {
            "Stack navigation can only be done on a Navigator initialised with NavigationConfig.MultiStack"
        }.stackEnterExitTransition,
        addToKeyHistory: Boolean = true
    ) {
        val navigationStacks = currentState.navigationStacks

        check(navigationStacks.any { it.key == key }) {
            """
                Given key: $key, is not part of the navigation stacks defined.
                Make sure to call rememberNavigator(NavigationConfig.MultiStack) with the given key.
            """.trimIndent()
        }

        val newStack = navigationStacks.first { it.key == key }

        mutableStateFlow.value = NavigationState(
            navigationStacks = navigationStacks,
            currentStackKey = newStack.key,
            transition = transition,
            overrideBackPress = currentState.overrideBackPress
        )

        if (addToKeyHistory) {
            mutableNavHistory.add((newStack.destinations.last().navHistoryEntry()))
        }
    }

    /**
     * Navigates to a given [StackKey]
     *
     * @param key, the new stack navigation key, must be present in [NavigationState.navigationStacks].
     * @param transition, enter/exit transitions for switching between stacks.
     */
    fun navigateToStack(
        key: StackKey,
        transition: NavEnterExitTransition = requireNotNull(navigationConfig as? NavigationConfig.MultiStack) {
            "Stack navigation can only be done on a Navigator initialised with NavigationConfig.MultiStack"
        }.stackEnterExitTransition
    ) = navigateToStack(key = key, transition = transition, addToKeyHistory = true)

    /**
     * Pops navigator back stack.
     *
     * When using [NavigationConfig.SingleStack], the last destination entry in
     * [NavigationState.currentStack] is removed.
     *
     * When using [NavigationConfig.MultiStack], the behavior differs based on
     * [NavigationConfig.MultiStack.backStackStrategy].
     *
     * @see [BackStackStrategy] for multi stack back stack behaviour.
     *
     * @return true, if the navigation state changed after popping backstack.
     * @return false, if the navigation state is unchanged.
     */
    fun popBackStack(): Boolean {
        return when (navigationConfig) {
            is NavigationConfig.MultiStack -> popBackStackMultiStack(navigationConfig)
            is NavigationConfig.SingleStack -> defaultPopBackStack()
        }
    }

    /**
     * Pops back stack of a [NavigationConfig.MultiStack].
     *
     * @see [BackStackStrategy] for multi stack back stack behaviour.
     */
    private fun popBackStackMultiStack(navigationConfig: NavigationConfig.MultiStack): Boolean {
        return when (navigationConfig.backStackStrategy) {
            is BackStackStrategy.BackToInitialStack -> {
                if (currentDestinations.size == 1 &&
                    currentStackKey != navigationConfig.initialStackKey
                ) {
                    navigateToStack(
                        key = navigationConfig.initialStackKey,
                        transition = navigationConfig.stackEnterExitTransition,
                        addToKeyHistory = false
                    )
                    return true
                }
                return defaultPopBackStack()
            }
            is BackStackStrategy.CrossStackHistory -> {
                if (navHistory.size == 1) return false
                val historyStackKey = navHistory[navHistory.lastIndex - 1].stackKey
                if (currentStackKey != historyStackKey) {
                    mutableNavHistory.removeLast()
                    navigateToStack(
                        key = historyStackKey,
                        transition = navigationConfig.stackEnterExitTransition,
                        addToKeyHistory = false
                    )
                    return true
                }
                return defaultPopBackStack()
            }
            BackStackStrategy.Default -> defaultPopBackStack()
        }
    }

    /**
     * Whether or not the navigator is able to navigate back.
     *
     * - Single stack, when there's more than one destination in the current stack
     * - Multi stack:
     *      - Default, when there's more than one destination in the current stack
     *      - BackToInitialStack, when the current stack is not the initial stack or the current
     *          stack contains more than one destination
     *      - CrossStackHistory, when stack history contains more than one entry
     */
    fun canGoBack(): Boolean {
        return when (val navigationConfig = navigationConfig) {
            is NavigationConfig.MultiStack -> {
                when (navigationConfig.backStackStrategy) {
                    is BackStackStrategy.BackToInitialStack -> {
                        currentDestinations.size > 1 || currentStackKey != navigationConfig.initialStackKey
                    }
                    is BackStackStrategy.CrossStackHistory -> navHistory.size > 1
                    BackStackStrategy.Default -> currentDestinations.size > 1
                }
            }
            is NavigationConfig.SingleStack -> currentDestinations.size > 1
        }
    }

    /**
     * Enables/Disables automatic back press handling.
     *
     * @param enabled, whether or not to enable auto back press handling.
     */
    fun overrideBackPress(enabled: Boolean) {
        mutableStateFlow.value = mutableStateFlow.value.copy(overrideBackPress = enabled)
    }

    /**
     * Saves the state of the navigator in a [NavigatorState] that can be used
     * to restore the state of the [Navigator] on app state restoration using [restore].
     */
    internal fun save(): NavigatorState = NavigatorState(
        navigationState = stateFlow.value,
        navigationConfig = navigationConfig,
        stackHistory = mutableNavHistory
    )

    /**
     * Restores the state of the navigator.
     *
     * @param navigatorState, used to restore the state of the navigator saved using [save].
     */
    internal fun restore(navigatorState: NavigatorState) {
        mutableNavHistory = navigatorState.stackHistory.toMutableList()
        mutableStateFlow.value = navigatorState.navigationState
    }

    /***
     * Sends key/result pair to [resultsChannel]
     */
    fun sendResult(result: Pair<String, Any>) = scope.launch {
        resultsChannel.send(result)
    }

    /**
     * Sends result using [NavigationNode.Companion.resultsKey] as key.
     *
     * Only works if [NavigationNode] didn't override its [NavigationNode.key].
     */
    inline fun <reified T : NavigationNode> sendResult(result: Any) {
        sendResult(resultsKey<T>() to result)
    }

    /**
     * Listens to results using [NavigationNode.Companion.resultsKey] as key.
     *
     * Only works if [NavigationNode] didn't override its [NavigationNode.key].
     */
    inline fun <reified T : NavigationNode> results() = results(resultsKey<T>())

    /**
     * Subscribe to results for a given [key].
     */
    fun results(key: String) = resultsFlow
        .filter { it.first == key }
        .map { it.second }

    private fun defaultPopBackStack(): Boolean {
        val destinations = currentDestinations
        if (destinations.size == 1) return false

        destinations.removeLast()
        mutableNavHistory.removeLast()

        updateStateWithDestinations(destinations, true)
        return true
    }

    private fun updateStateWithDestinations(
        newDestinations: List<Destination>,
        popping: Boolean
    ): Boolean {
        if (newDestinations == currentDestinations) return false
        val navigationStacks = currentState.navigationStacks.toMutableList()
        val currentStack = currentState.currentStack

        val newStack = currentStack.copy(destinations = newDestinations)
        navigationStacks[navigationStacks.indexOfFirst { it.key == newStack.key }] = newStack

        val transition = if (popping) {
            currentStack.destinations.last().transition.popEnterExit
        } else {
            newStack.destinations.last().transition.enterExit
        }

        mutableNavHistory.removeAll { entry ->
            !navigationStacks.map { it.destinations }.flatten().any {
                it.id == entry.destinationId
            }
        }

        mutableStateFlow.value = NavigationState(
            navigationStacks = navigationStacks,
            currentStackKey = newStack.key,
            transition = transition,
            overrideBackPress = currentState.overrideBackPress
        )
        return true
    }

    private fun Destination.navHistoryEntry() = NavHistoryEntry(
        stackKey = currentStackKey,
        destinationId = id,
        navigationNodeKey = navigationNode.key
    )
}
