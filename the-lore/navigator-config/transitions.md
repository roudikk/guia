# Transitions

Guia uses Compose's `EnterTransition` and `ExitTransition` for animation.

First we need to declare a `NavigationTranstion`, for example:

```kotlin
val MaterialSharedAxisTransitionX = NavigationTransition(
    enterExit = slideInHorizontally { (it * 0.2f).toInt() } + fadeIn(
        animationSpec = tween(300)
    ) to slideOutHorizontally { -(it * 0.1f).toInt() } + fadeOut(
        animationSpec = tween(150)
    ),

    popEnterExit = slideInHorizontally { -(it * 0.1f).toInt() } + fadeIn(
        animationSpec = tween(300)
    ) to slideOutHorizontally { (it * 0.2f).toInt() } + fadeOut(
        animationSpec = tween(150)
    ),
)
```

This will slide the content left/right with some fading in/out as the navigation keys are navigated.

Second, we need to update our `NavigatorConfig` to add the transition:

```kotlin
val navigator = rememberNavigator { 
    // Provide a default transition between all keys
    defaultTransition { -> MaterialSharedAxisTransitionX }
    
    // Provide a transtion based on what the previous key and the new key are.
    defaultTransition { previousKey, newKey -> 
        // Some logic
    }
    
    // Define transitions for specific Navigation Keys. If one doesn't exist
    // when navigating to navigation key of a certain type, the navigator will 
    // fall back to defaultTransition
    transition<DetailsBottomSheetKey> { -> CrossFadeTransition }
    transition<DetailsDialogKey> { -> VerticalSlideTransition }
    transition<DynamicDetailsKey> { -> CrossFadeTransition }
    transition<DetailsKey> { -> MaterialSharedAxisTransitionX }
}
```

### Overriding transitions

We can override a specific transition before we set a backstack, for specific cases where we need to simply override a single `setBackstack` 's transition.

To do so we can use `overrideTransition`:

```kotlin
// Override the next Screen transition
navigator.overrideTransition<Screen>(CrossFadeTransition)
navigator.setBackstack(..)

// Override the next BottomSheet transition
navigator.overrideTransition<BottomSheet>(VerticalSlideTransition)
navigator.setBackstack(..)

// Override the next Dialogtransition
navigator.overrideTransition<Dialog>(MaterialSharedAxisTransitionX)
navigator.setBackstack(..)
```
