package com.roudikk.navigator

import androidx.compose.runtime.derivedStateOf
import com.roudikk.navigator.core.Destination
import com.roudikk.navigator.core.NavigationNode

private fun NavigationKey<NavigationNode>.notFoundError(): String {
    return "NavigationKey: $this was not declared. " +
            "Call associate<MyKey, MyNavigationNode> {} inside your Navigator rules."
}

private fun Navigator.navigationNodeForKey(
    navigationKey: NavigationKey<NavigationNode>
): NavigationNode {
    return navigatorRules.associations[navigationKey]?.invoke()
        ?: error(navigationKey.notFoundError())
}

fun Navigator.navigate(
    navigationNode: NavigationNode
) {
    setBackstack(backStack + Destination(navigationNode))
}

fun Navigator.navigate(
    navigationKey: NavigationKey<NavigationNode>
) = navigate(navigationNodeForKey(navigationKey))

fun Navigator.replaceLast(
    navigationNode: NavigationNode
) {
    setBackstack(backStack.drop(1) + Destination(navigationNode))
}

fun Navigator.replaceLast(
    navigationKey: NavigationKey<NavigationNode>
) = replaceLast(navigationNodeForKey(navigationKey))

fun Navigator.replaceUpTo(
    navigationNode: NavigationNode,
    inclusive: Boolean = true,
    predicate: (NavigationNode) -> Boolean
) {
    val newBackstack = backStack.dropLastWhile {
        !predicate(it.navigationNode)
    }.toMutableList()

    if (inclusive) newBackstack.removeLast()

    newBackstack.add(Destination(navigationNode))
    setBackstack(newBackstack)
}

fun Navigator.replaceUpTo(
    navigationKey: NavigationKey<NavigationNode>,
    inclusive: Boolean = true,
    predicate: (NavigationNode) -> Boolean
) = replaceUpTo(
    navigationNode = navigationNodeForKey(navigationKey),
    inclusive = inclusive,
    predicate = predicate
)

inline fun <reified T : NavigationNode> Navigator.replaceUpTo(
    navigationNode: NavigationNode,
    inclusive: Boolean = false
) = replaceUpTo(
    navigationNode = navigationNode,
    inclusive = inclusive,
    predicate = { it.key == NavigationNode.key<T>() }
)

inline fun <reified T : NavigationNode> Navigator.replaceUpTo(
    navigationKey: NavigationKey<T>,
    inclusive: Boolean = false
) = replaceUpTo(
    navigationNode = NavigationNode.key<T>(),
    inclusive = inclusive,
    predicate = { it.key == NavigationNode.key<T>() }
)

fun Navigator.moveToTop(
    matchLast: Boolean = true,
    predicate: (NavigationNode) -> Boolean
): Boolean {
    val destination = if (matchLast) {
        backStack.findLast { predicate(it.navigationNode) }
    } else {
        backStack.find { predicate(it.navigationNode) }
    }

    return destination?.let {
        setBackstack(backStack.toMutableList().apply {
            remove(destination)
            add(destination)
        })
        true
    } ?: false
}

inline fun <reified T : NavigationNode> Navigator.moveToTop(
    matchLast: Boolean = true,
) = moveToTop(
    predicate = { it.key == NavigationNode.key<T>() },
    matchLast = matchLast
)

fun Navigator.singleInstance(
    navigationNode: NavigationNode,
    useExistingInstance: Boolean = true,
) {
    val existingDestination = backStack.lastOrNull { it.navigationNode.key == navigationNode.key }
        .takeIf { useExistingInstance }
    val newBackStack = backStack.toMutableList()
    newBackStack.removeAll { it.navigationNode.key == navigationNode.key }
    val destination = existingDestination ?: Destination(navigationNode)
    newBackStack.add(destination)
    setBackstack(newBackStack)
}

fun Navigator.singleTop(
    navigationNode: NavigationNode
) {
    if (backStack.lastOrNull()?.navigationNode?.key == navigationNode.key) return
    navigate(navigationNode)
}

fun Navigator.any(
    predicate: (NavigationNode) -> Boolean
) = backStack.map { it.navigationNode }.any(predicate)

fun Navigator.popTo(
    key: String,
    inclusive: Boolean = false
): Boolean {
    val destination = backStack.find { it.navigationNode.key == key } ?: return false
    var newBackStack = backStack.dropLastWhile { it.id != destination.id }
    if (inclusive) newBackStack = newBackStack.drop(1)
    setBackstack(newBackStack)
    return true
}

inline fun <reified T : NavigationNode> Navigator.popTo(
    inclusive: Boolean = false
) = popTo(
    key = NavigationNode.key<T>(),
    inclusive = inclusive
)

fun Navigator.popToRoot() {
    setBackstack(backStack[0])
}

fun Navigator.setRoot(
    navigationNode: NavigationNode
) {
    setBackstack(Destination(navigationNode))
}

fun Navigator.popBackStack() {
    setBackstack(backStack.drop(1))
}

fun Navigator.canGoBack() = derivedStateOf {
    backStack.size > 1
}



