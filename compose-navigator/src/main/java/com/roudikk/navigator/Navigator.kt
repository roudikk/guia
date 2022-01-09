package com.roudikk.navigator

import com.roudikk.navigator.NavigationNode.Companion.key
import com.roudikk.navigator.NavigationNode.Companion.resultsKey
import com.roudikk.navigator.animation.NavigationEnterTransition
import com.roudikk.navigator.animation.NavigationExitTransition
import com.roudikk.navigator.animation.navigationFadeIn
import com.roudikk.navigator.animation.navigationFadeOut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class Navigator {

    companion object {
        /**
         * Default key used to create a default [Navigator] instance.
         */
        const val defaultKey = "Navigator_DefaultKey"
    }

    /**
     * Whether or not this [Navigator] has been initialized.
     *
     * True if [initialize] was called with a [NavigationConfig].
     */
    private var initialized = false

    /**
     * Coroutine scope used for [Navigator] coroutine operations.
     *
     * @see [currentKeyFlow]
     * @see [resultsFlow]
     * @see [sendResult]
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /**
     * ]
     */
    private lateinit var navigationConfig: NavigationConfig

    /**
     * The navigation stack history across all the stacks defined in [NavigationConfig.SingleStack]
     * or [NavigationConfig.MultiStack].
     *
     * @see [StackHistoryEntry] for more information on how this works.
     */
    private var mutableStackHistory = mutableListOf<StackHistoryEntry>()
    val stackHistory: List<StackHistoryEntry>
        get() = mutableStackHistory

    /**
     * The current state of the navigator.
     */
    private val mutableStateFlow = MutableStateFlow(
        NavigationState(
            navigationStacks = listOf(DefaultNavigationStack),
            currentStackKey = DefaultNavigationStack.key,
            transitionPair = DefaultNavTransition.enter to DefaultNavTransition.exit,
            overrideBackPress = true
        )
    )
    val stateFlow: StateFlow<NavigationState> = mutableStateFlow

    /**
     * Current stack key.
     */
    val currentKey: NavigationKey
        get() = stateFlow.value.currentStackKey
    val currentKeyFlow: StateFlow<NavigationKey> = stateFlow.map {
        it.currentStackKey
    }.stateIn(scope, SharingStarted.Lazily, stateFlow.value.currentStackKey)

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

    /**
     * Initializes the [Navigator]
     *
     * @param navigationConfig, configuration used to initialize the [Navigator].
     * @see [NavigationConfig] for the different available configs.
     *
     * @param forceInitialize, by default calling [initialize] multiple times will be ignored but
     * the navigator can be re-initialized by setting this param to true.
     */
    internal fun initialize(navigationConfig: NavigationConfig, forceInitialize: Boolean = false) {
        if (!forceInitialize && initialized) return

        this.navigationConfig = navigationConfig

        when (navigationConfig) {
            is NavigationConfig.MultiStack -> initialize(
                navigationStacks = navigationConfig.entries.map {
                    NavigationStack(
                        key = it.key,
                        destinations = listOf(Destination(it.initialNavigationNode))
                    )
                },
                initialStackKey = navigationConfig.initialStackKey
            )
            is NavigationConfig.SingleStack -> initialize(
                initialNavigationNode = navigationConfig.initialNavigationNode
            )
        }
        initialized = true

        mutableStackHistory.clear()
        mutableStackHistory.add(
            StackHistoryEntry(
                mutableStateFlow.value.currentStackKey,
                mutableStateFlow.value.currentStack.currentNodeKey
            )
        )
    }

    /**
     * Initializes the [Navigator] with a single stack using [DefaultNavigationStack]
     * and [DefaultNavTransition].
     */
    private fun initialize(initialNavigationNode: NavigationNode) {
        mutableStateFlow.value = NavigationState(
            navigationStacks = listOf(
                DefaultNavigationStack.copy(
                    destinations = listOf(Destination(initialNavigationNode))
                )
            ),
            currentStackKey = DefaultNavigationStack.key,
            transitionPair = DefaultNavTransition.enter to DefaultNavTransition.exit,
            overrideBackPress = true
        )
    }

    /**
     * Initializes the [Navigator] with multiple stacks, [initialStackKey] must be part
     * of [navigationStacks].
     */
    private fun initialize(
        navigationStacks: List<NavigationStack>,
        initialStackKey: NavigationKey
    ) {
        check(navigationStacks.any { it.key == initialStackKey }) {
            "Initial stack must be in the list of provided navigation stacks"
        }
        mutableStateFlow.value = NavigationState(
            navigationStacks = navigationStacks,
            currentStackKey = initialStackKey,
            transitionPair = DefaultNavTransition.enter to DefaultNavTransition.exit,
            overrideBackPress = true
        )
    }

    /**
     * Navigate to a given destination.
     *
     * @param navigationNode, the new node to navigate to.
     * @param navOptions, navigation options.
     * @see [NavOptions]
     *
     *  The new node is added as a new [Destination] along with its [navOptions] on top
     *  of [NavigationStack.destinations] in [NavigationState.currentStack].
     */
    fun navigate(navigationNode: NavigationNode, navOptions: NavOptions = NavOptions()) {
        val currentState = mutableStateFlow.value
        val currentStack = currentState.currentStack
        var navigationStacks = currentState.navigationStacks.toMutableList()

        var shouldAnimate = true
        val newStack = when (navOptions.launchMode) {
            LaunchMode.DEFAULT -> {
                mutableStackHistory.add(StackHistoryEntry(currentStack.key, navigationNode.key))

                currentStack.copy(
                    destinations = currentStack.destinations
                        .toMutableList()
                        .apply {
                            add(
                                Destination(
                                    navigationNode = navigationNode,
                                    navOptions = navOptions
                                )
                            )
                        }
                )
            }
            LaunchMode.SINGLE_TOP -> {
                if (currentStack.currentNodeKey == navigationNode.key) {
                    shouldAnimate = false
                }

                val newDestination = Destination(
                    navigationNode = navigationNode,
                    navOptions = navOptions
                )

                currentStack.copy(
                    destinations = currentStack.destinations
                        .toMutableList()
                        .apply {
                            if (currentStack.currentNodeKey == navigationNode.key) {
                                removeLast()
                            }
                            add(newDestination)
                        }
                )
            }
            LaunchMode.SINGLE_INSTANCE -> {
                if (currentStack.currentNodeKey == navigationNode.key) {
                    shouldAnimate = false
                }

                val newDestination = Destination(
                    navigationNode = navigationNode,
                    navOptions = navOptions
                )

                navigationStacks = navigationStacks.map {
                    it.copy(destinations = it.destinations.toMutableList().apply {
                        removeAll { destination ->
                            destination.navigationNode.key == navigationNode.key
                        }
                    })
                }.toMutableList()

                // Remove all previous destinations matching node key from history
                mutableStackHistory.removeAll { it.navigationNodeKey == navigationNode.key }
                mutableStackHistory.add(StackHistoryEntry(currentStack.key, navigationNode.key))

                currentStack.copy(destinations = currentStack.destinations
                    .toMutableList()
                    .apply {
                        removeAll { destination ->
                            destination.navigationNode.key == navigationNode.key
                        }
                        add(newDestination)
                    }
                )
            }
        }

        navigationStacks[navigationStacks.indexOfFirst { it.key == newStack.key }] = newStack
        val newState = NavigationState(
            currentStackKey = currentStack.key,
            navigationStacks = navigationStacks,
            transitionPair = if (shouldAnimate) {
                newStack.destinations.last().navOptions.navTransition.enter to
                        newStack.destinations.last().navOptions.navTransition.exit
            } else {
                NavigationEnterTransition.None to NavigationExitTransition.None
            },
            overrideBackPress = currentState.overrideBackPress
        )
        mutableStateFlow.value = newState
    }

    /**
     * Navigates to a given [NavigationKey]
     *
     * @param key, the new stack navigation key, must be present in [NavigationState.navigationStacks].
     * @param transitions, enter/exit transitions for switching between stacks.
     * @param addToKeyHistory, when this is true and navigator is using [BackStackStrategy.CrossStackHistory].
     * when navigating to a different stack and then pressing the back button (Assuming
     * [NavigationState.overrideBackPress] is true) the navigator will navigate back to the previous
     * back stack, however if it's set to false, the navigator will not go back to previous stack.
     */
    fun navigateToStack(
        key: NavigationKey,
        transitions: NavigationTransitionPair = navigationFadeIn() to navigationFadeOut(),
        addToKeyHistory: Boolean = true
    ) {
        val currentState = mutableStateFlow.value
        val navigationStacks = currentState.navigationStacks

        check(navigationStacks.any { it.key == key }) {
            """
                Given key: $key, is not part of the navigation stacks defined.
                Make sure to call initialize(navigationStacks) with the given key.
            """.trimIndent()
        }

        val newStack = navigationStacks.first { it.key == key }

        mutableStateFlow.value = NavigationState(
            navigationStacks = navigationStacks,
            currentStackKey = newStack.key,
            transitionPair = transitions,
            overrideBackPress = currentState.overrideBackPress
        )

        if (addToKeyHistory) {
            mutableStackHistory.add(StackHistoryEntry(newStack.key, newStack.currentNodeKey))
        }
    }

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
        val currentState = mutableStateFlow.value

        return when (val navigationConfig = navigationConfig) {
            is NavigationConfig.MultiStack -> {
                when (val backStackStrategy = navigationConfig.backStackStrategy) {
                    is BackStackStrategy.BackToInitialStack -> {
                        if (currentState.currentStack.destinations.size == 1
                            && currentState.currentStackKey != navigationConfig.initialStackKey
                        ) {
                            navigateToStack(
                                key = navigationConfig.initialStackKey,
                                transitions = backStackStrategy.transitions,
                                addToKeyHistory = false
                            )
                            true
                        } else {
                            defaultPopBackStack()
                        }
                    }
                    is BackStackStrategy.CrossStackHistory -> {
                        val lastStackKey = if (mutableStackHistory.size == 1) {
                            mutableStackHistory[0].navigationKey
                        } else {
                            mutableStackHistory[mutableStackHistory.lastIndex - 1].navigationKey
                        }
                        if (mutableStackHistory.size > 1) {
                            if (currentState.currentStackKey != lastStackKey) {
                                mutableStackHistory.removeLastButFirst()
                                navigateToStack(
                                    key = lastStackKey,
                                    transitions = backStackStrategy.transitions,
                                    addToKeyHistory = false
                                )
                                true
                            } else {
                                defaultPopBackStack()
                            }
                        } else {
                            defaultPopBackStack()
                        }
                    }
                    BackStackStrategy.Default -> defaultPopBackStack()
                }
            }
            is NavigationConfig.SingleStack -> defaultPopBackStack()
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
        val currentState = mutableStateFlow.value

        return when (val navigationConfig = navigationConfig) {
            is NavigationConfig.MultiStack -> {
                when (navigationConfig.backStackStrategy) {
                    is BackStackStrategy.BackToInitialStack -> {
                        currentState.currentStack.destinations.size > 1 ||
                                currentState.currentStackKey != navigationConfig.initialStackKey
                    }
                    is BackStackStrategy.CrossStackHistory -> stackHistory.size > 1
                    BackStackStrategy.Default -> currentState.currentStack.destinations.size > 1
                }
            }
            is NavigationConfig.SingleStack -> currentState.currentStack.destinations.size > 1
        }
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
        val currentDestinations = mutableStateFlow.value.currentStack.destinations
        val screen = currentDestinations.find { it.navigationNode.key == key }
            ?: return false

        val newDestinations = currentDestinations.subList(
            fromIndex = 0,
            toIndex = currentDestinations.indexOf(screen) + if (inclusive) 0 else 1
        )

        val newStackHistory = mutableStackHistory.subList(
            fromIndex = 0,
            toIndex = currentDestinations.indexOf(screen) + if (inclusive) 0 else 1
        )

        mutableStackHistory = newStackHistory

        return updateStateWithDestinations(newDestinations)
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
        val currentStackKey = mutableStateFlow.value.currentStackKey
        val rootDestination = mutableStateFlow.value.currentStack.destinations[0]

        while (mutableStackHistory.lastOrNull()?.navigationKey == currentStackKey) {
            mutableStackHistory.removeLast()
        }

        mutableStackHistory.add(
            StackHistoryEntry(
                currentStackKey,
                rootDestination.navigationNode.key
            )
        )

        updateStateWithDestinations(listOf(rootDestination))
    }

    /**
     * Clears the history for [NavigationState.currentStack] and sets a new root node.
     *
     * @param navigationNode the new root node for the current stack.
     * @param navOptions navigation options.
     * @see [NavOptions]
     */
    fun setRoot(navigationNode: NavigationNode, navOptions: NavOptions = NavOptions()) {
        val currentState = mutableStateFlow.value
        val currentStack = currentState.currentStack
        val navigationStacks = currentState.navigationStacks.toMutableList()

        val newStack = currentStack.copy(
            destinations = listOf(Destination(navigationNode, navOptions))
        )
        val newState = NavigationState(
            currentStackKey = currentStack.key,
            navigationStacks = navigationStacks,
            transitionPair = newStack.destinations.last().navOptions.navTransition.enter to
                    currentStack.destinations.last().navOptions.navTransition.exit,
            overrideBackPress = currentState.overrideBackPress
        )
        navigationStacks[navigationStacks.indexOfFirst { it.key == newStack.key }] = newStack

        mutableStackHistory.removeAll { it.navigationKey == currentStack.key }
        mutableStackHistory.add(StackHistoryEntry(currentStack.key, navigationNode.key))

        mutableStateFlow.value = newState
    }

    /**
     * Enables/Disables automatic back press handling.
     *
     * @param enabled, whether or not to enable auto back press handling.
     */
    fun overrideBackPress(enabled: Boolean) {
        mutableStateFlow.value = mutableStateFlow.value.copy(
            overrideBackPress = enabled
        )
    }

    /**
     * Saves the state of the navigator in a [NavigatorState] that can be used
     * to restore the state of the [Navigator] on app state restoration using [restore].
     */
    internal fun save() = NavigatorState(
        navigationState = stateFlow.value,
        navigationConfig = navigationConfig,
        stackHistory = mutableStackHistory
    )

    /**
     * Restores the state of the navigator.
     *
     * @param navigatorState, used to restore the state of the navigator saved using [save].
     */
    internal fun restore(navigatorState: NavigatorState) {
        navigationConfig = navigatorState.navigationConfig
        mutableStackHistory = navigatorState.stackHistory.toMutableList()
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

    private fun MutableList<StackHistoryEntry>.removeLastButFirst() {
        if (size > 1) {
            removeLast()
        }
    }

    private fun updateStateWithDestinations(newDestinations: List<Destination>): Boolean {
        val currentState = mutableStateFlow.value
        val navigationStacks = mutableStateFlow.value.navigationStacks.toMutableList()

        val newStack = currentState.currentStack.copy(destinations = newDestinations)
        navigationStacks[navigationStacks.indexOfFirst { it.key == newStack.key }] = newStack

        mutableStateFlow.value = NavigationState(
            navigationStacks = navigationStacks,
            currentStackKey = newStack.key,
            transitionPair = currentState.currentStack.destinations.last().navOptions.navTransition.popEnter to
                    currentState.currentStack.destinations.last().navOptions.navTransition.popExit,
            overrideBackPress = currentState.overrideBackPress
        )

        return true
    }

    private fun defaultPopBackStack(): Boolean {
        val currentState = mutableStateFlow.value

        if (currentState.currentStack.destinations.size == 1) return false

        mutableStackHistory.removeLastButFirst()

        updateStateWithDestinations(
            currentState.currentStack.destinations
                .toMutableList()
                .apply { removeLast() }
        )
        return true
    }
}
