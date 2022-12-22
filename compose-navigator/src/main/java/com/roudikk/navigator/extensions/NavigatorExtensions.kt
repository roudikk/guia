@file:Suppress("TooManyFunctions")

package com.roudikk.navigator.extensions

import androidx.compose.runtime.derivedStateOf
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator

/**
 * Returns the current [NavigationKey]
 */
val Navigator.currentKey: NavigationKey
    get() = backStack.last()

/**
 * Adds a new key to the backstack.
 *
 * @param navigationKey, the new key to be added.
 */
fun Navigator.navigate(
    navigationKey: NavigationKey
) {
    setBackstack(backStack + navigationKey)
}

/**
 * Replaces the last key in the backstack with a new key.
 *
 * @param navigationKey, the key to replace the last key in the backstack.
 */
fun Navigator.replaceLast(
    navigationKey: NavigationKey
) {
    setBackstack(backStack.dropLast(1) + navigationKey)
}

/**
 * Loops through navigation keys from the top of the backstack until the predicate is satisfied
 * and replaces all those keys with a new key.
 *
 * @param navigationKey, the key to replace with in the backstack.
 * @param inclusive, if true will also replace the navigation key that satisfied the [predicate].
 * @param predicate, condition to be met by the last navigation key to be replaced in the backstack.
 */
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

/**
 * Replaces all navigation keys in the backstack until a key of type [Key]
 * is reached.
 *
 * @see replaceUpTo
 */
inline fun <reified Key : NavigationKey> Navigator.replaceUpTo(
    navigationKey: NavigationKey,
    inclusive: Boolean = false
) = replaceUpTo(
    navigationKey = navigationKey,
    inclusive = inclusive,
    predicate = { it::class == Key::class }
)

/**
 * Moves a navigation key that matches the given condition to the top
 *
 * @param matchLast, whether should start matching from top or the bottom of the backstack.
 * @param predicate, condition to be met by the navigation key.
 * @return true if there was a navigation key that matched the predicate.
 */
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
        setBackstack(
            backStack.toMutableList().apply {
                remove(navigationKey)
                add(navigationKey)
            }
        )
        true
    } ?: false
}

/**
 * Moves a navigation key of type [Key] to the top of backstack.
 *
 * @see moveToTop
 */
inline fun <reified Key : NavigationKey> Navigator.moveToTop(
    matchLast: Boolean = true,
) = moveToTop(
    predicate = { it::class == Key::class },
    matchLast = matchLast
)

/**
 * Navigates to a navigation key and removes all navigation keys that match the given condition.
 *
 * @param navigationKey, the new navigation key.
 * @param predicate, the condition to be met by a navigation key in the backstack.
 * @param useExistingInstance, if true then we check the backstack first for a matching navigation key
 * and use that instance instead of [navigationKey]
 */
inline fun <reified Key : NavigationKey> Navigator.singleInstance(
    navigationKey: Key,
    useExistingInstance: Boolean = true,
) {
    val existingKey = backStack
        .lastOrNull { it is Key }
        .takeIf { useExistingInstance }
    val newBackStack = backStack.toMutableList()
    newBackStack.removeAll { it is Key }
    newBackStack.add(existingKey ?: navigationKey)
    setBackstack(newBackStack)
}

/**
 *
 */
fun Navigator.singleTop(
    navigationKey: NavigationKey,
    predicate: (NavigationKey) -> Boolean = { it::class == navigationKey::class },
) {
    if (currentKey.let(predicate)) return
    navigate(navigationKey)
}

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

fun Navigator.none(
    predicate: (NavigationKey) -> Boolean
) = backStack.none(predicate)

fun Navigator.any(
    predicate: (NavigationKey) -> Boolean
) = backStack.any(predicate)
