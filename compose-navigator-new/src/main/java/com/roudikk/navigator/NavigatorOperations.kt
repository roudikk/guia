package com.roudikk.navigator

import androidx.compose.runtime.derivedStateOf

fun Navigator.navigate(
    navigationKey: NavigationKey
) {
    setBackstack(backStack + navigationKey)
}

fun Navigator.replaceLast(
    navigationKey: NavigationKey
) {
    setBackstack(backStack.dropLast(1) + navigationKey)
}

fun Navigator.replaceUpTo(
    navigationKey: NavigationKey,
    inclusive: Boolean = true,
    predicate: (NavigationKey) -> Boolean
) {
    val newBackstack = backStack.dropLastWhile { !predicate(it) }.toMutableList()

    if (inclusive) newBackstack.removeLast()

    newBackstack.add(navigationKey)
    setBackstack(newBackstack)
}

inline fun <reified Key : NavigationKey> Navigator.replaceUpTo(
    navigationKey: NavigationKey,
    inclusive: Boolean = false
) = replaceUpTo(
    navigationKey = navigationKey,
    inclusive = inclusive,
    predicate = { it::class == Key::class }
)

fun Navigator.moveToTop(
    matchLast: Boolean = true,
    predicate: (NavigationKey) -> Boolean
): Boolean {
    val navigationKey = if (matchLast) {
        backStack.findLast { predicate(it) }
    } else {
        backStack.find { predicate(it) }
    }

    return navigationKey?.let {
        setBackstack(backStack.toMutableList().apply {
            remove(navigationKey)
            add(navigationKey)
        })
        true
    } ?: false
}

inline fun <reified Key : NavigationKey> Navigator.moveToTop(
    matchLast: Boolean = true,
) = moveToTop(
    predicate = { it::class == Key::class },
    matchLast = matchLast
)

fun Navigator.singleInstance(
    navigationKey: NavigationKey,
    predicate: (NavigationKey) -> Boolean = { it::class == navigationKey::class },
    useExistingInstance: Boolean = true,
) {
    val existingKey = backStack.lastOrNull { predicate(it) }
        .takeIf { useExistingInstance }
    val newBackStack = backStack.toMutableList()
    newBackStack.removeAll { predicate(it) }
    val newKey = existingKey ?: navigationKey
    newBackStack.add(newKey)
    setBackstack(newBackStack)
}

fun Navigator.singleTop(
    navigationKey: NavigationKey,
    predicate: (NavigationKey) -> Boolean = { it::class == navigationKey::class },
) {
    if (backStack.last().let(predicate)) return
    navigate(navigationKey)
}

fun Navigator.any(
    predicate: (NavigationKey) -> Boolean
) = backStack.any(predicate)

fun Navigator.popTo(
    predicate: (NavigationKey) -> Boolean,
    inclusive: Boolean = false
): Boolean {
    val existingKey = backStack.find(predicate) ?: return false
    var newBackStack = backStack.dropLastWhile { it != existingKey }
    if (inclusive) newBackStack = newBackStack.drop(1)
    setBackstack(newBackStack)
    return true
}

inline fun <reified Key : NavigationKey> Navigator.popTo(
    inclusive: Boolean = false
) = popTo(
    predicate = { it::class == Key::class },
    inclusive = inclusive
)

fun Navigator.popToRoot() {
    setBackstack(backStack[0])
}

fun Navigator.setRoot(
    navigationKey: NavigationKey
) {
    setBackstack(navigationKey)
}

fun Navigator.popBackStack() {
    setBackstack(backStack.dropLast(1))
}

fun Navigator.canGoBack() = derivedStateOf {
    backStack.size > 1
}
