@file:Suppress("TooManyFunctions")

package com.roudikk.navigator.extensions

import androidx.compose.runtime.derivedStateOf
import com.roudikk.navigator.core.BackStackEntry
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.entry

val Navigator.currentEntry: BackStackEntry
    get() = backStack.last()

/**
 * Returns the current [NavigationKey]
 */
val Navigator.currentKey: NavigationKey
    get() = currentEntry.navigationKey

/**
 * Adds a new key to the backstack.
 *
 * @param navigationKey, the new key to be added.
 */
fun Navigator.navigate(
    navigationKey: NavigationKey
) {
    setBackstack(backStack + navigationKey.entry())
}

/**
 * Replaces the last key in the backstack with a new key.
 *
 * @param navigationKey, the key to replace the last key in the backstack.
 */
fun Navigator.replaceLast(
    navigationKey: NavigationKey
) {
    setBackstack(backStack.dropLast(1) + navigationKey.entry())
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
    val newBackstack = backStack.dropLastWhile { !predicate(it.navigationKey) }.toMutableList()
    if (inclusive) newBackstack.removeLast()
    newBackstack.add(navigationKey.entry())
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

enum class Match {
    First,
    Last
}

/**
 * Moves a navigation key that matches the given condition to the top
 *
 * @param match, whether should start matching from top or the bottom of the backstack.
 * @param predicate, condition to be met by the navigation key.
 * @return true if there was a navigation key that matched the predicate.
 */
fun Navigator.moveToTop(
    match: Match = Match.Last,
    predicate: (NavigationKey) -> Boolean
): Boolean {
    val existingEntry = when (match) {
        Match.Last -> backStack.lastOrNull { predicate(it.navigationKey) }
        Match.First -> backStack.firstOrNull { predicate(it.navigationKey) }
    }

    return existingEntry?.let {
        setBackstack(
            backStack.toMutableList().apply {
                remove(existingEntry)
                add(existingEntry)
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
    match: Match = Match.Last,
) = moveToTop(
    predicate = { it::class == Key::class },
    match = match
)

/**
 * Navigates to a navigation key and removes all navigation keys that are of the same type from the
 * backstack.
 *
 * @param navigationKey, the new navigation key.
 * @param useExisting, if true then we check the backstack first for a matching navigation key
 * and use that instance instead of [navigationKey]
 */
inline fun <reified Key : NavigationKey> Navigator.singleInstance(
    navigationKey: Key,
    match: Match = Match.Last,
    useExisting: Boolean = true,
) {
    val existingEntry = if (useExisting) {
        when (match) {
            Match.First -> backStack.firstOrNull { it.navigationKey is Key }
            Match.Last -> backStack.lastOrNull { it.navigationKey is Key }
        }
    } else {
        null
    }
    val newBackStack = backStack.toMutableList()
    newBackStack.removeAll { it.navigationKey is Key }
    newBackStack.add(existingEntry ?: navigationKey.entry())
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
    val existingEntry = backStack.find { predicate(it.navigationKey) } ?: return false
    var newBackStack = backStack.dropLastWhile { it != existingEntry }
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
    setBackstack(navigationKey.entry())
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
) = backStack.none { predicate(it.navigationKey) }

fun Navigator.any(
    predicate: (NavigationKey) -> Boolean
) = backStack.any { predicate(it.navigationKey) }
