//package com.roudikk.navigator.core
//
//import android.os.Parcelable
//import com.roudikk.navigator.Navigator
//import kotlinx.parcelize.Parcelize
//
//sealed class BackStackStrategy : Parcelable {
//
//    /**
//     * When a stack only has a single destination left, it will stop handling back presses
//     */
//    @Parcelize
//    object Default : BackStackStrategy()
//
//    /**
//     * When a stack only has a single destination left, it will go back to the initial stack,
//     * When the initial stack only has a single destination lef,t ti will stop handling back
//     * presses
//     * **/
//    @Parcelize
//    object BackToInitialStack : BackStackStrategy()
//
//    /**
//     * Back press will navigate between stacks,
//     */
//    @Parcelize
//    object CrossStackHistory : BackStackStrategy()
//}
//
///**
// * This allows us to remember the history of the switching between stacks to enable
// * [BackStackStrategy.CrossStackHistory]
// *
// * For ex:
// *
// * For a given list of navigation keys: key0, key1, key2
// * And navigation nodes: node0, node1, node2, node3
// * Starting stack, node: node0 to key0
// * And each key has the relative node with index as its starting screen.
// *
// * Navigating in this sequence:
// * - [Navigator.navigate] node3
// * - [Navigator.navigateToStack] key1
// * - [Navigator.navigate] node1
// * - [Navigator.navigate] node2
// * - [Navigator.navigateToStack] key2
// * - [Navigator.navigate] node3
// *
// * Would result in this stack history:
// * - key0 to node0
// * - key0 to node3
// * - key1 to node1
// * - key1 to node1
// * - key1 to node2
// * - key2 to node2
// * - key2 to node3
// */
//@Parcelize
//data class NavHistoryEntry(
//    val stackKey: StackKey,
//    val navigationNodeKey: String,
//    val destinationId: String
//) : Parcelable
