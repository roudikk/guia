# Compose Navigator

Navigator tailored to work nicely with composable screens.

Note: This is currently experimental and API is very likely to change.

|           |  Features  |
|-----------|-------------|
:tada: | Simple API
:recycle: | State restoration
:train: | Nested navigations
:back: | Multiple back stack strategies
:twisted_rightwards_arrows: | Support for Enter/Exit compose transitons
:rocket: | Different launch modes
:phone: | Result passing between navigation nodes


## Navigation nodes

Screen:

```kotlin
@Parcelize
class MyScreen(val myData: String) : Screen {
    
    @Composable
    override fun AnimatedVisibilityScope.Content() {
        
    }
}
```

Dialog:

```kotlin
@Parcelize
class MyDialog(val myData: String) : Dialog {

    override val dialogOptions: DialogOptions
        get() = DialogOptions(
            dismissOnBackPress = true, // When set to false, back press will not cancel this dialog
            dismissOnClickOutside = true // When set to false, clicking outside the dialog doesn't dismiss it
        )

    @Composable
    override fun AnimatedVisibilityScope.Content() {

    }
}
```

Bottom Sheet:

```kotlin
@Parcelize
class MyBottomSheet(val myData: String) : BottomSheet {

    override val bottomSheetOptions: BottomSheetOptions
        get() = BottomSheetOptions(
            dismissOnHidden = true // When set to false, swiping down the bottom sheet will not dismiss it. 
        )
    
    @Composable
    override fun AnimatedVisibilityScope.Content() {

    }
}
```

Bottom sheets do not get a default surface as a background. This is to enable the developer to choose which composable is the parent of a bottom sheet (For ex: Surface2 or Surface3) inside their own implementation.

However, to make it easier to have a consistent bottom sheet design across all bottom sheets (if that's the case), you can override `bottomSheetSetup` inside `NavContainer` to provide a common composable parent to all bottom sheets.


## Usage

For a single stack navigation:

```kotlin
NavHost(navigationConfig = NavigationConfig.SingleStack(FirstScreen())) {
    // Inside this scope you have access to the navigator using findNavigator()
    
    NavContainer() // This will draw the FirstScreen's composable immediately
}
```

For multi-stacks navigation with history for each stack (For ex: Bottom navigation)

Each stack should have a unique `NavigationKey`:

```kotlin
sealed class AppNavigationKey : NavigationKey() {

    @Parcelize
    object Home : AppNavigationKey()

    @Parcelize
    object Profile : AppNavigationKey()

    @Parcelize
    object Settings : AppNavigationKey()
}
```

Then define your `NavHost`:

```kotlin
val stackEntries = listOf(
    NavigationConfig.MultiStack.NavigationStackEntry(
        key = AppNavigationKey.Home,
        initialNavigationNode = HomeScreen()
    ),
    NavigationConfig.MultiStack.NavigationStackEntry(
        key = AppNavigationKey.Profile,
        initialNavigationNode = ProfileScreen()
    ),
    NavigationConfig.MultiStack.NavigationStackEntry(
        key = AppNavigationKey.Settings,
        initialNavigationNode = SettingsScreen()
    )
)

NavHost(
      navigationConfig = NavigationConfig.MultiStack(
          entries = stackEntries,
          initialStackKey = stackEntries[0].key,
          backStackStrategy = BackStackStrategy.BackToInitialStack()
      ),
) {

    NavContainer() // This will draw the initial stack's initial screen immediately
    
    val navigator = findNavigator()
    val currentStackKey by navigator.currentKeyFlow.collectAsState()
    
    // Use currentStackKey to change which tab is selected in case of a bottom navigation
}
```


## Navigation operations

```kotlin
// Note: enter/exit/popEnter/popExit animations can be defined in NavOptions along with SingleTop flag.

// Navigate to a navigation node, 
findNavigator().navigate(navigationNode, navOptions) 

// Navigate to a different stack
findNavigator().navigateToStack(stackKey, transitions, addKeyToHistory)

// Pop back stack
findNavigator().popBackStack() 

// Pop to
findNavigator().popTo<Screen>(inclusive) 
findNavigator().popTo(navigationNodeKey, inclusive) // In case overriding key inside NavigationNode

// Pop to root
findNavigator().popToRoot() // This will navigate to the root of the current stack
findNavigator().setRoot(navigationNode, navOptions)

// Check if you can navigate back
findNavigator().canGoBack()
```

## Launch Modes

Launch mode can be specified using the `navOptions.launchMode` parameter of `navigate` function. Available Launch modes are:

- Single Top: If the current top most navigation node has the same key, no additional navigation happens.
- Single instance: Clears the entire backstack of navigation nodes matching same key and launches a new instance on top.

Note: Currently there launch modes don't provide `newIntent` equivalent behaviour so the content will not restore the state of an existing navigation node.

## Animations

`EnterTransition` and `ExitTransition` are not savable in a bundle and cannot be saved/restored when the state of the app is saved/restored. 
They are sealed and final so there is no easy way to extend them and make them savable.

Compose navigator provides a one to one match of all the `EnterTransition` and `ExitTransition` defined.
Prepend `navigation` to the compose equivalent function to find the navigation version of it.

For ex: `fadeIn()` -> `navigationFadeIn()`

`EnterTransition` is converted to `NavigationEnterTransition`
`ExitTransition` is converted to `NavigationExitTransition`

Animation specs supported currently are: Tween, Snap and Spring, prepend `navigation` to compose equivalent.

For ex: `tween()` -> `navigationTween()`

### Animating between navigation nodes

Example:

```kotlin
val MaterialSharedAxisTransitionX = NavTransition(
    enter = navigationSlideInHorizontally { (it * 0.2f).toInt() }
            + navigationFadeIn(animationSpec = navigationTween(300)),

    exit = navigationSlideOutHorizontally { -(it * 0.1f).toInt() }
            + navigationFadeOut(animationSpec = navigationTween(150)),

    popEnter = navigationSlideInHorizontally { -(it * 0.1f).toInt() }
            + navigationFadeIn(animationSpec = navigationTween(300)),

    popExit = navigationSlideOutHorizontally { (it * 0.2f).toInt() }
            + navigationFadeOut(animationSpec = navigationTween(150))
)
```

Usage:

```kotlin
findNavigator().navigate(
  navigatioNode = navigationNode, 
  navOptions = navOptions(
      navTransition = MaterialSharedAxisTransitionX,
  )
)
```  

### Animating between stacks

Animating between stack changes can be done by using the `transitions` paramter inside `navigatToStack`

For ex:

```kotlin
findNavigator().navigateToStack(stackKey, navigationFadeIn() to NavigationFadeOut())
```

### Animating navigation node elements with screen transitions

`Content` function inside a `NavigatioNode` has reference to the `animatedVisibilityScope` used by the `AnimatedContent` that handles all transitions between navigation nodes.

This means composables inside navigation nodes can have enter/exit transitions based on the node's enter/exit state, using the `animateEnterExit` modifier.

For ex:

```kotlin
@Parcelize
class MyScreen : Screen {

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        Text(
            modifier = Modifier
                .animateEnterExit(
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ),
            text = "I animate with this screen's enter/exit transitions!"
        )
    }
}
```

## Back stack management

`NavContainer` uses composes's `BackHandler` to override back presses, it's defined before the navigation node composables so navigation nodes can override back press handling by providing their own `BackHandler`

For Multi stack navigation, `NavigationConfig.MultiStack` provides 3 possible back stack strategies:

When the stack reaches its initial node then pressing the back button:

- Default: back press will no longer be handled by the navigator.
- BackToInitialStack: 
  - if the current stack is not the initial stack defined in `NavigationConfig.MultiStack` then the navigator will navigate back to the initial stack
  - If the current stack is the initial stack, then back press will no longer be handled by navigator
- CrossStackHistory:
  - When navigating between stacks, this strategy will navigate back between stacks based on `navigate/navigateToStack` operations
  
## State restoration

`NavContainer` uses `rememberSaveableStateHolder()` to remember composables ui states.

`Navigator.Saver` handles saving/restoring the navigator state upon application state saving/restoration.

## Result passing

`Navigator` uses coroutine flows to pass results between navigation nodes.

A Result can be of any type.

Sending/receiving results are done by the type of the navigation node:

```kotlin
    // Navigator.kt
    // Listening to results
    fun results(key: String) // Returns results for a key in case of overriding the default key inside the navigation node
    inline fun <reified T : NavigationNode> results() // Covenience function that uses the default key for a NavigationNode
    
    // Sending results
    fun sendResult(result: Pair<String, Any>) // Sends result for a given navigation node key
    inline fun <reified T : NavigationNode> sendResult(result: Any) // Covenience function that uses the default key for a NavigationNode
    
    // Additionally, navigation node has an extension function on Navigator to make it even easir to listen to results
    
   // NavigationNode
   fun Navigator.nodeResults() = results(resultsKey)
```

Usage ex:

```kotlin
@Parcelize
class Screen1 : Screen {

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        val context = LocalContext.current
        val navigator = findNavigator()

        Button(onClick = { navigator.navigate(Screen2()) }) {
            Text(text = "Navigate")
        }

        LaunchedEffect(Unit) {
            navigator.nodeResults()
                .onEach {
                    Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                }
                .launchIn(this)
        }
    }
}

@Parcelize
class Screen2 : Screen {

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        val navigator = findNavigator()

        Button(onClick = {
            navigator.sendResult<Screen1>("Hello!")
            navigator.popBackStack()
        }) {
            Text(text = "Send Result")
        }
    }
}
```
  
## Nested Navigation

Compose navigator offers 3 navigator fetching functions:

- `findNavigator()` returns the closest navigator in navigation hierarchy
- `findParentNavigator()` returns the parent navigator of the current navigator, nullable
- `findDefaultNavigator()` returns the default navigator using `Navigator.defaultKey`

The first `NavHost` should usually use the default key (By not overriding the `key` parameter)

All nested `NavHost` must provide a unique key to differentiate between them.

```kotlin
// NavHost1
NavHost(
    navigationConfig = NavigationConfig.SingleStack(FirstScreen())
) {
    findNavigator() // Returns navigator for NavHost1
    findParentNavigator() // Returns null
    findDefaultNavigator() // Returns navigator for NavHost1 if 'key' parameter was not overridden in 'NavHost'

    // NavHost2
    NavHost(
        key = "Nested Navigation",
        navigationConfig = NavigationConfig.SingleStack(NestedFirstScreen())
    ) {
        findNavigator() // Returns navigator for NavHost2
        findParentNavigator() // Returns navigator for NavHost1
        findDefaultNavigator() // Returns navigator for NavHost1 if 'key' parameter was not overridden in 'NavHost'
    }
            
    // NavHost2 will override the back press of NavHost1 until it can no longer go back
    // Then NavHost1 will take over back press handling.
    // Both NavHost1 and NavHost2 can use any navigation node defined anywhere.
}
```
