# NavHost

Let's suppose we have a bottom tab navigation and we want to switch between the tabs and keep their history. Guia allows that behavior using a `NavHost`:

First we declare our different keys for each stack (or tab):

```kotlin
@Parcelize
object HomeStackKey: StackKey

@Parcelize
object FeedStackKey: StackKey
```

Then we can create our navigators and our nav host:

```kotlin
val homeNavigator = rememberNavigator()
val feedNavigator = rememberNavigator()

val navHost = rememberNavHost(
    initialKey = HomeStackKey, // Optional.
    entries = setOf(
        StackEntry(HomeStackKey, homeNavigator),
        StackEntry(FeedStackKey, feedNavigator)
    )
)
```

Then we can render our `NavHost` state using `NavHost.NavContainer`:

```kotlin
fun NavHost.NavContainer(
    modifier: (StackKey) -> Modifier = { Modifier },
    bottomSheetScrimColor: @Composable (StackKey) -> Color = {
        MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
    },
    bottomSheetContainer: StackKeyContainer = { _, content -> content() },
    dialogContainer: StackKeyContainer = { _, content -> content() },
    transitionSpec: AnimatedContentScope<StackEntry?>.() -> ContentTransform = {
        EnterTransition.None with ExitTransition.None
    }
)
```

As you can see we can have different nav container params based on our stack key.

### Switch navigators

To switch which navigator is currently active we can use `setActive`:

```kotlin
navHost.setActive(FeedStackKey)
```

### Navigating using NavHost

We can also directly navigate using the `NavHost` using `currentNavigator` or `navigator(key)` functions:

```kotlin
navHost.currentNavigator?.push(SomeKey())

// Although we aren't on HomeStackKey, we can still navigate in that context
navHost.navigator(HomeStackKey).push(SomeKey())
```
