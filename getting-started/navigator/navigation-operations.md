# Navigation Operations

Guia provides an extensive list of navigation operations.

### currentKey

```kotlin
val Navigator.currentKey
```

Returns the current `NavigationKey`

### currentEntry

```kotlin
val Navigator.currentEntry
```

Returns the current `BackStackEntry`

### push

```kotlin
fun Navigator.push(
    navigationKey: NavigationKey
)
```

Adds a new key to the backstack.

### pop

```kotlin
fun Navigator.pop(): Boolean
```

Pops the last entry in the backstack.

### replaceLast

```kotlin
fun Navigator.replaceLast(
    navigationKey: NavigationKey
)
```

Adds a new key to the backstack.

### replaceUpTo

```kotlin
fun Navigator.replaceUpTo(
    navigationKey: NavigationKey,
    inclusive: Boolean = true,
    predicate: (NavigationKey) -> Boolean
)
```

Loops through navigation keys from the top of the backstack until the predicate is satisfied and replaces all those keys with a new key.

### replaceUpTo \[Key]

```kotlin
inline fun <reified Key : NavigationKey> Navigator.replaceUpTo(
    navigationKey: NavigationKey,
    inclusive: Boolean = false
)
```

Replaces all navigation keys in the backstack until a key of type \[Key] is reached.

### moveToTop

```kotlin
fun Navigator.moveToTop(
    match: Match = Match.Last,
    predicate: (NavigationKey) -> Boolean
): Boolean
```

Moves a navigation key that matches the given condition to the top

### moveToTop \[Key]

```kotlin
inline fun <reified Key : NavigationKey> Navigator.moveToTop(
    match: Match = Match.Last,
)
```

Moves a navigation key of type \[Key] to the top of backstack.

### singleInstance

```kotlin
inline fun <reified Key : NavigationKey> Navigator.singleInstance(
    navigationKey: Key,
    match: Match = Match.Last,
    checkForExisting: Boolean = false,
)
```

Navigates to a navigation key and removes all navigation keys that are of the same type from the backstack.

### singleTop

```kotlin
inline fun <reified Key : NavigationKey> Navigator.singleTop(
    navigationKey: Key
)
```

Navigates to \[navigationKey] if the [currentKey](navigation-operations.md#currentkey) is not of the same type.

### popTo

```kotlin
fun Navigator.popTo(
    inclusive: Boolean = false,
    predicate: (NavigationKey) -> Boolean,
)
```

Pops to a \[NavigationKey] matching \[predicate]

### popTo \[Key]

```kotlin
inline fun <reified Key : NavigationKey> Navigator.popTo(
    inclusive: Boolean = false,
    crossinline predicate: (Key) -> Boolean = { true },
)
```

Pops to a \[NavigationKey] of the same type \[Key].

### removeAll

```kotlin
fun Navigator.removeAll(
    predicate: (NavigationKey) -> Boolean
)
```

Removes all navigation keys matching \[predicate].

### removeAll \[Key]

```kotlin
inline fun <reified Key : NavigationKey> Navigator.removeAll()
```

Removes all navigation keys that are of type \[Key].

### popToRoot

```kotlin
fun Navigator.popToRoot()
```

Pops to the root of the backstack.

### setRoot

```kotlin
fun Navigator.setRoot(
    navigationKey: NavigationKey
) 
```

Clears the backstack and sets a new root \[NavigationKey]

### canGoback

```kotlin
fun Navigator.canGoBack(): Boolean
```

Whether or not the navigator has more than one element and can pop back stack.

### none

```kotlin
fun Navigator.none(
    predicate: (NavigationKey) -> Boolean
)
```

Checks if none of the navigation keys matches the condition.

### none \[Key]

```kotlin
inline fun <reified Key : NavigationKey> Navigator.none()
```

Checks if none of the navigation keys is of type \[Key].

### any

```kotlin
fun Navigator.any(
    predicate: (NavigationKey) -> Boolean
)
```

Checks if any of the navigation keys matches the condition.

### any \[Key]

```kotlin
inline fun <reified Key : NavigationKey> Navigator.any()
```

Checks if any of the navigation keys is of type \[Key].
