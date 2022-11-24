//package com.roudikk.navigator.core
//
//import android.os.Parcelable
//import com.roudikk.navigator.Navigator
//import com.roudikk.navigator.animation.NavEnterExitTransition
//import com.roudikk.navigator.animation.NavTransition
//import kotlinx.parcelize.Parcelize
//
///**
// * Navigation Config used to define a [Navigator] configuration.
// */
//sealed class NavigationConfig : Parcelable {
//
//    abstract val defaultTransition: NavTransition
//
//    /**
//     * Use when the navigation consists of a single stack.
//     *
//     * @property initialNavigationNode, the initial navigation node for the stack.
//     */
//    @Parcelize
//    data class SingleStack(
//        val initialNavigationNode: NavigationNode,
//        override val defaultTransition: NavTransition = NavTransition.None
//    ) : NavigationConfig()
//
//    /**
//     * Use when the navigation consists of multiple stacks with their own backstack.
//     *
//     * @property entries, the list of stack entries.
//     * @property initialStackKey, the initial stack to navigate to when navigation starts.
//     * @property backStackStrategy, the back stack strategy for multi stack navigation.
//     */
//    @Parcelize
//    data class MultiStack(
//        val entries: List<NavigationStackEntry>,
//        val initialStackKey: StackKey,
//        override val defaultTransition: NavTransition = NavTransition.None,
//        val stackEnterExitTransition: NavEnterExitTransition = NavEnterExitTransition.None,
//        val backStackStrategy: BackStackStrategy = BackStackStrategy.Default
//    ) : NavigationConfig() {
//
//        /**
//         * Multi stack entry.
//         *
//         * @property key, [StackKey] for current entry.
//         * @property initialNavigationNode, the first node to show for that entry.
//         */
//        @Parcelize
//        data class NavigationStackEntry(
//            val key: StackKey,
//            val initialNavigationNode: NavigationNode
//        ) : Parcelable
//    }
//}
