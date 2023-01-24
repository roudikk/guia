# Navigation Operations

Guia provides an extensive list of navigation operations.

#### currentKey

```kotlin
val Navigator.currentKey
```

Returns the current `NavigationKey`

#### currentEntry

```kotlin
val Navigator.currentEntry
```

Returns the current `BackStackEntry`

#### navigate

```kotlin
fun Navigator.navigate(
    navigationKey: NavigationKey
) ..
```

Adds a new key to the backstack.

#### replaceLast

```kotlin
fun Navigator.replaceLast(
    navigationKey: NavigationKey
) ..
```

Adds a new key to the backstack.

#### replaceUpTo

```kotlin
fun Navigator.replaceUpTo(
    navigationKey: NavigationKey,
    inclusive: Boolean = true,
    predicate: (NavigationKey) -> Boolean
) ..
```

Loops through navigation keys from the top of the backstack until the predicate is satisfied and replaces all those keys with a new key.
