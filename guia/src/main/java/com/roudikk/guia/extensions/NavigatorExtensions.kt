@file:Suppress("TooManyFunctions")

package com.roudikk.guia.extensions

import androidx.compose.runtime.derivedStateOf
import com.roudikk.guia.core.BackstackEntry
import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.entries
import com.roudikk.guia.core.entry

/**
 * Returns the current [BackstackEntry]
 */
val Navigator.currentEntry: BackstackEntry?
    get() = backstack.lastOrNull()

/**
 * Returns the current [NavigationKey]
 */
val Navigator.currentKey: NavigationKey?
    get() = currentEntry?.navigationKey

/**
 * Adds a new key to the backstack.
 *
 * @param navigationKey, the new key to be added.
 */
fun Navigator.push(
    navigationKey: NavigationKey
) {
    setBackstack(backstack + navigationKey.entry())
}

/**
 * Adds multiple new keys to the backstack.
 *
 * @param navigationKeys, the new keys to be added.
 */
fun Navigator.push(
    vararg navigationKeys: NavigationKey
) {
    setBackstack(backstack + navigationKeys.toList().entries())
}

/**
 * Replaces the last key in the backstack with a new key.
 *
 * @param navigationKey, the key to replace the last key in the backstack.
 */
fun Navigator.replaceLast(
    navigationKey: NavigationKey
) {
    setBackstack(backstack.dropLast(1) + navigationKey.entry())
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
    val newBackstack = backstack.dropLastWhile { !predicate(it.navigationKey) }.toMutableList()
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
        Match.Last -> backstack.lastOrNull { predicate(it.navigationKey) }
        Match.First -> backstack.firstOrNull { predicate(it.navigationKey) }
    }

    return existingEntry?.let {
        setBackstack(
            backstack.toMutableList().apply {
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
 * @param match, whether should start matching from top or the bottom of the backstack.
 * @param checkForExisting, if true then we check the backstack first for a matching navigation key
 * and use that instance instead of [navigationKey]
 */
inline fun <reified Key : NavigationKey> Navigator.singleInstance(
    navigationKey: Key,
    match: Match = Match.Last,
    checkForExisting: Boolean = false,
) {
    val existingEntry = if (checkForExisting) {
        when (match) {
            Match.First -> backstack.firstOrNull { it.navigationKey is Key }
            Match.Last -> backstack.lastOrNull { it.navigationKey is Key }
        }
    } else {
        null
    }
    val newBackstack = backstack.toMutableList()
    newBackstack.removeAll { it.navigationKey is Key }
    newBackstack.add(existingEntry ?: navigationKey.entry())
    setBackstack(newBackstack)
}

/**
 * Navigates to [navigationKey] if the [currentKey] is not of the same type.
 *
 * @param navigationKey, the new navigation key.
 */
inline fun <reified Key : NavigationKey> Navigator.singleTop(
    navigationKey: Key
) {
    if (currentKey is Key) return
    push(navigationKey)
}

/**
 * Pops to a [NavigationKey] matching [predicate].
 *
 * @param inclusive, whether to pop the [NavigationKey] that matches the [predicate] too.
 * @param predicate, condition to be met by the navigation key.
 *
 * @return true, if a navigation key matching the [predicate] was found.
 */
fun Navigator.popTo(
    inclusive: Boolean = false,
    predicate: (NavigationKey) -> Boolean,
): Boolean {
    val existingEntry = backstack.find { predicate(it.navigationKey) } ?: return false
    var newBackstack = backstack.dropLastWhile { it != existingEntry }
    if (inclusive) {
        newBackstack = newBackstack.dropLast(1)
    }
    setBackstack(newBackstack)
    return true
}

/**
 * Pops to a [NavigationKey] of the same type [Key].
 *
 * @param inclusive, whether to pop the last [NavigationKey] of type [Key] too
 * @param predicate, optional extra condition for the navigation key that matches type [Key]
 *
 * @return true, if a navigation key of same type [Key] and matching the [predicate] was found.
 *
 * Has same [JvmName] as [popTo] so updating it to resolve naming conflict.
 */
@JvmName("popToKey")
inline fun <reified Key : NavigationKey> Navigator.popTo(
    inclusive: Boolean = false,
    crossinline predicate: (Key) -> Boolean = { true },
) = popTo(
    predicate = { it is Key && predicate(it) },
    inclusive = inclusive
)

/**
 * Removes all navigation keys matching [predicate].
 *
 * @param predicate, condition to be met by the navigation key to be removed.
 */
fun Navigator.removeAll(
    predicate: (NavigationKey) -> Boolean
) {
    setBackstack(
        backstack.toMutableList().apply {
            removeAll { predicate(it.navigationKey) }
        }
    )
}

/**
 * Removes all navigation keys that are of type [Key].
 */
inline fun <reified Key : NavigationKey> Navigator.removeAll() {
    removeAll { it is Key }
}

/**
 * Pops to the root of the backstack.
 */
fun Navigator.popToRoot() {
    setBackstack(backstack[0])
}

/**
 * Clears the backstack and sets a new root [NavigationKey]
 *
 * @param navigationKey, the new root key.
 */
fun Navigator.setRoot(
    navigationKey: NavigationKey
) {
    setBackstack(navigationKey.entry())
}

/**
 * Pops the last entry in the backstack.
 *
 * @return true if the backstack has more than one entry and the last entry was removed.
 */
fun Navigator.pop(): Boolean {
    if (backstack.size == 1) return false
    setBackstack(backstack.dropLast(1))
    return true
}

/**
 * Whether or not the navigator has more than one element and can pop back stack.
 *
 * @return true if the backstack has more than one element.
 */
fun Navigator.canGoBack() = derivedStateOf {
    backstack.size > 1
}

/**
 * Checks if none of the navigation keys matches the condition
 *
 * @param predicate, condition to be mey by the navigation key.
 */
fun Navigator.none(
    predicate: (NavigationKey) -> Boolean
) = backstack.none { predicate(it.navigationKey) }

/**
 * Checks if none of the navigation keys is of type [Key]
 */
inline fun <reified Key : NavigationKey> Navigator.none() = none { it is Key }

/**
 * Checks if any of the navigation keys matches the condition
 *
 * @param predicate, condition to be mey by the navigation key.
 */
fun Navigator.any(
    predicate: (NavigationKey) -> Boolean
) = backstack.any { predicate(it.navigationKey) }

/**
 * Checks if any of the navigation keys is of type [Key]
 */
inline fun <reified Key : NavigationKey> Navigator.any() = any { it is Key }
