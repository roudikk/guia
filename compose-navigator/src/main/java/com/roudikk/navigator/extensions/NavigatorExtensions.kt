package com.roudikk.navigator.extensions

import androidx.compose.runtime.derivedStateOf
import com.roudikk.navigator.compose.animation.EnterExitTransition
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator

fun Navigator.navigate(
    navigationKey: NavigationKey,
    overrideTransition: EnterExitTransition? = null
) {
    setBackstack(
        backStack + navigationKey,
        overrideTransition = overrideTransition
    )
}

fun Navigator.replaceLast(
    navigationKey: NavigationKey,
    overrideTransition: EnterExitTransition? = null
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
    inclusive: Boolean = false,
    predicate: (NavigationKey) -> Boolean,
): Boolean {
    val existingKey = backStack.find(predicate) ?: return false
    var newBackStack = backStack.dropLastWhile { it != existingKey }
    if (inclusive) newBackStack = newBackStack.drop(1)
    setBackstack(newBackStack)
    return true
}

@JvmName("popToWithKey")
inline fun <reified Key : NavigationKey> Navigator.popTo(
    inclusive: Boolean = false,
    crossinline predicate: (Key) -> Boolean,
) = popTo(
    predicate = { it is Key && predicate(it) },
    inclusive = inclusive
)

inline fun <reified Key : NavigationKey> Navigator.popTo(
    inclusive: Boolean = false
) = popTo(
    predicate = { it::class == Key::class },
    inclusive = inclusive
)

fun Navigator.removeAll(
    predicate: (NavigationKey) -> Boolean
) {
    setBackstack(backStack.toMutableList().apply { removeAll(predicate) })
}

fun Navigator.popToRoot() {
    setBackstack(backStack[0])
}

fun Navigator.setRoot(
    navigationKey: NavigationKey
) {
    setBackstack(navigationKey)
}

fun Navigator.popBackstack(): Boolean {
    if (backStack.size == 1) return false
    setBackstack(backStack.dropLast(1))
    return true
}

fun Navigator.canGoBack() = derivedStateOf {
    backStack.size > 1
}