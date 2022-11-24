//package com.roudikk.navigator.core
//
//import android.os.Parcelable
//import com.roudikk.navigator.Navigator
//import kotlinx.parcelize.Parcelize
//
///**
// * A stack is used to hold a list of destinations.
// *
// * @property key, unique key for the stack.
// * @property destinations, the list of destinations in the stack.
// * @property currentNodeKey, the last destination's navigation node key.
// */
//@Parcelize
//data class NavigationStack(
//    val key: StackKey,
//    val destinations: List<Destination>
//) : Parcelable {
//
//    val currentNodeKey: String
//        get() = destinations.last().navigationNode.key
//}
//
///**
// * Default navigation stack, used on [Navigator] with [NavigationConfig.SingleStack]
// */
//internal val DefaultNavigationStack = NavigationStack(
//    key = DefaultStackKey,
//    destinations = emptyList()
//)
