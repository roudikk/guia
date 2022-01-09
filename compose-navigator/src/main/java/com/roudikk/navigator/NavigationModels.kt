package com.roudikk.navigator

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import com.roudikk.navigator.animation.NavigationEnterTransition
import com.roudikk.navigator.animation.NavigationExitTransition
import com.roudikk.navigator.animation.navigationFadeIn
import com.roudikk.navigator.animation.navigationFadeOut
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Represents a navigation node in the navigation tree.
 */
interface NavigationNode : Parcelable {

    /**
     * The key for a navigation node, make sure this is unique across nodes or the navigator might
     * run into unexpected behaviours. By default this uses the class name as a key, this means
     * when creating an instance using object: NavigationNode for ex, the key will be empty, in
     * that case make sure to override key with something more meaningful.
     */
    val key: String
        get() = this::class.java.simpleName

    /**
     * The key for listening to results passed to this navigation node.
     */
    val resultsKey: String
        get() = "${key}_Results"

    /**
     * Composable UI of the navigation node.
     */
    @Composable
    fun AnimatedVisibilityScope.Content()

    companion object {
        inline fun <reified T : NavigationNode> key(): String = T::class.java.simpleName
        inline fun <reified T : NavigationNode> resultsKey() = "${key<T>()}_Results"
    }

    /**
     * Convenience function to listen to results in navigation node without having to pass
     * results key.
     */
    fun Navigator.nodeResults() = results(resultsKey)
}

/**
 * A screen representation of a [NavigationNode].
 */
interface Screen : NavigationNode

/**
 * A Dialog representation of a [NavigationNode].
 *
 * @property dialogOptions, extra dialog options.
 */
interface Dialog : NavigationNode {

    val dialogOptions: DialogOptions
        get() = DialogOptions()
}

/**
 * A Bottom sheet representation of a [NavigationNode].
 *
 * @property bottomSheetOptions, extra bottom sheet options.
 */
interface BottomSheet : NavigationNode {

    val bottomSheetOptions: BottomSheetOptions
        get() = BottomSheetOptions()
}

/**
 * Options used in a [Dialog].
 *
 * @property dismissOnClickOutside, whether or not to dismiss the dialog when clicking outside
 * its bounds.
 * @property dismissOnBackPress, whether or not to dismiss the dialog when pressing the back
 * button.
 */
data class DialogOptions(
    val dismissOnClickOutside: Boolean = true,
    val dismissOnBackPress: Boolean = true
)

/**
 * Options used in a [BottomSheet]
 *
 * @property dismissOnHidden, whether or not to dismiss the bottom sheet when
 * it reaches a hidden state.
 */
data class BottomSheetOptions(
    val dismissOnHidden: Boolean = true
)

/**
 * Navigation state representing the current state of a [Navigator].
 *
 * @property navigationStacks, list of stacks in the current state, either a singleton if
 * navigator is using a [NavigationConfig.SingleStack] or multiple entries when using
 * [NavigationConfig.MultiStack].
 * @property currentStackKey, the current [NavigationKey].
 * @property transitionPair, the current transition based on the last navigation operation.
 * @property overrideBackPress, whether or not to override back press.
 * @property currentStack, the current stack matching [currentStackKey].
 */
@Parcelize
data class NavigationState(
    val navigationStacks: List<NavigationStack>,
    val currentStackKey: NavigationKey,
    val transitionPair: NavigationTransitionPair,
    val overrideBackPress: Boolean
) : Parcelable {

    val currentStack: NavigationStack
        get() = navigationStacks.first { it.key == currentStackKey }
}

/**
 * Holder for a pair of [NavigationEnterTransition] and [NavigationExitTransition].
 */
@Parcelize
data class NavigationTransitionPair(
    val enter: NavigationEnterTransition,
    val exit: NavigationExitTransition
) : Parcelable

infix fun NavigationEnterTransition.to(
    that: NavigationExitTransition
) = NavigationTransitionPair(this, that)

/**
 * Used as a key for a [NavigationStack], extend to define custom navigation keys.
 */
@Parcelize
open class NavigationKey : Parcelable

/**
 * Represents an entry in the navigation history.
 *
 * @property navigationNode, navigation node for this entry.
 * @property navOptions, navigation options for this entry.
 * @property id, unique identifier of the destination.
 */
@Parcelize
data class Destination(
    val navigationNode: NavigationNode,
    val navOptions: NavOptions = NavOptions(),
    val id: String = UUID.randomUUID().toString(),
    val dataKey: String = UUID.randomUUID().toString()
) : Parcelable {

    val combinedKey: String
        get() = id + dataKey

    override fun equals(other: Any?): Boolean {
        return other is Destination && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * A stack is used to hold a list of destinations.
 *
 * @property key, unique key for the stack.
 * @property destinations, the list of destinations in the stack.
 * @property currentNodeKey, the last destination's navigation node key.
 */
@Parcelize
data class NavigationStack(
    val key: NavigationKey,
    val destinations: List<Destination>
) : Parcelable {

    val currentNodeKey: String
        get() = destinations.last().navigationNode.key
}

/**
 * Convenience default navigation key.
 */
@Parcelize
internal object DefaultNavigationKey : NavigationKey()

/**
 * Convenience default navigation stack.
 */
internal val DefaultNavigationStack = NavigationStack(
    key = DefaultNavigationKey,
    destinations = emptyList()
)

/**
 * Convenience default navigation transitions.
 */
internal val DefaultNavTransition = NavTransition()

/**
 * Navigation transition for a destination
 *
 * @property enter, used for the new destination when navigating towards it.
 * @property exit, used as the old destination's animation when navigating
 * towards the new destination.
 * @property popEnter, used for the old destination's animation when navigation back to it.
 * @property popExit, used for the current destination's animation when navigation out of it.
 */
@Parcelize
data class NavTransition(
    val enter: NavigationEnterTransition = navigationFadeIn(),
    val exit: NavigationExitTransition = navigationFadeOut(),
    val popEnter: NavigationEnterTransition = navigationFadeIn(),
    val popExit: NavigationExitTransition = navigationFadeOut()
) : Parcelable

/**
 * Navigation Config used to define a [Navigator] configuration.
 */
sealed class NavigationConfig : Parcelable {

    /**
     * Use when the navigation consists of a single stack.
     *
     * @property initialNavigationNode, the initial navigation node for the stack.
     */
    @Parcelize
    data class SingleStack(
        val initialNavigationNode: NavigationNode
    ) : NavigationConfig()

    /**
     * Use when the navigation consists of multiple stacks with their own backstack.
     *
     * @property entries, the list of stack entries.
     * @property initialStackKey, the initial stack to navigate to when navigation starts.
     * @property backStackStrategy, the back stack strategy for multi stack navigation.
     */
    @Parcelize
    data class MultiStack(
        val entries: List<NavigationStackEntry>,
        val initialStackKey: NavigationKey,
        val backStackStrategy: BackStackStrategy
    ) : NavigationConfig() {

        /**
         * Multi stack entry.
         *
         * @property key, [NavigationKey] for current entry.
         * @property initialNavigationNode, the first node to show for that entry.
         */
        @Parcelize
        data class NavigationStackEntry(
            val key: NavigationKey,
            val initialNavigationNode: NavigationNode
        ) : Parcelable
    }
}

/**
 * Used with [Navigator] navigation operations.
 *
 * @property [navTransition], used for enter/exit/popEnter/popExit transitions for given
 * @property [launchMode], destination launch mode
 * [NavigationNode.key] so the only node present in history is that new node.
 */
@Parcelize
data class NavOptions(
    val launchMode: LaunchMode = LaunchMode.DEFAULT,
    val navTransition: NavTransition = NavTransition()
) : Parcelable

enum class LaunchMode {

    /**
     * Navigates to a navigation node with no rules.
     */
    DEFAULT,

    /**
     * If the top most navigation node of the current stack is already of the same type, the navigator
     * will not navigate to a new instance.
     */
    SINGLE_TOP,

    /**
     * When using single instance, if there's an existing navigation node in the backstack
     * of any stack defined in the navigator configuration, it will be brought to the front
     * with the updated data.
     */
    SINGLE_INSTANCE
}

/**
 * This allows us to remember the history of the switching between stacks to enable
 * [BackStackStrategy.CrossStackHistory]
 *
 * For ex:
 *
 * For a given list of navigation keys: key0, key1, key2
 * And navigation nodes: node0, node1, node2, node3
 * Starting stack, node: node0 to key0
 * And each key has the relative node with index as its starting screen.
 *
 * Navigating in this sequence:
 * - [Navigator.navigate] node3
 * - [Navigator.navigateToStack] key1
 * - [Navigator.navigate] node1
 * - [Navigator.navigate] node2
 * - [Navigator.navigateToStack] key2
 * - [Navigator.navigate] node3
 *
 * Would result in this stack history:
 * - key0 to node0
 * - key0 to node3
 * - key1 to node1
 * - key1 to node1
 * - key1 to node2
 * - key2 to node2
 * - key2 to node3
 */
@Parcelize
data class StackHistoryEntry(
    val navigationKey: NavigationKey,
    val navigationNodeKey: String
) : Parcelable

/**
 * Used to hold the state of a [Navigator] for state saving/restoration.
 */
@Parcelize
internal data class NavigatorState(
    val navigationState: NavigationState,
    val navigationConfig: NavigationConfig,
    val stackHistory: List<StackHistoryEntry>
) : Parcelable

sealed class BackStackStrategy : Parcelable {

    /**
     * When a stack only has a single destination left, it will stop handling back presses
     */
    @Parcelize
    object Default : BackStackStrategy()

    /**
     * When a stack only has a single destination left, it will go back to the initial stack,
     * When the initial stack only has a single destination lef,t ti will stop handling back
     * presses
     * **/
    @Parcelize
    class BackToInitialStack(
        val transitions: NavigationTransitionPair = NavigationTransitionPair(
            navigationFadeIn(), navigationFadeOut()
        )
    ) : BackStackStrategy()

    /**
     * Back press will navigate between stacks,
     */
    @Parcelize
    class CrossStackHistory(
        val transitions: NavigationTransitionPair = NavigationTransitionPair(
            navigationFadeIn(), navigationFadeOut()
        )
    ) : BackStackStrategy()
}
