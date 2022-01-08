# Compose Navigator

Navigator tailored to work nicely with composable screens.


|           |  Features  |
|-----------|-------------|
:tada: | Simple API
:recycle: | State restoration
:train: | Back stack handling and nested navigations
:twisted_rightwards_arrows: | Support for Enter/Exit compose transitons
:phone: | Result passing between navigation nodes


## Navigation nodes

Screen:

```kotlin
@Parcelize
class MyScreen(val myData: String) : Screen {
    
    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        
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
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {

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
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {

    }
}
```


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


## Navigation operations:

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

## Animations:

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

`Content` function inside a `NavigatioNode` has reference to the `animatedVisibilityScope` used by the `AnimatedContent` that handles all transitions between navigatio nodes.

This means composables inside navigatio nodes can have enter/exit transitions based on the node's enter/exit state, using the `animateEnterExit` modifier.

For ex:

```kotlin
@Parcelize
class MyScreen : Screen {

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        with(animatedVisibilityScope) {
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
}
```

## Back stack management

`NavContainer` uses composes's `BackHandler` to override back presses, it's defined before the navigatio node composables so navigation nodes can override back press handling by providing their own `BackHandler`

For Multi stack navigation, `NavigationConfig.MultiStack` provides 3 possible back stack strategies:

When the stack reaches its initial node then pressing the back button:

- Default: back press will no longer be handled by the navigator.
- BackToInitialStack: 
  - if the current stack is not the initial stack defined in `NavigationConfig.MultiStack` then the navigator will navigate back to the initial stack
  - If the current stack is the initial stack, then back press will no longer be handled by navigator
- CrossStackHistory:
  - When navigating between stacks, this strategy will navigate back between stacks based on `navigate/navigateToStack` operations
  
  
## Nested Navigation


