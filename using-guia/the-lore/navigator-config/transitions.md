# Transitions

Guia uses Compose's `EnterTransition` and `ExitTransition` for animation.

The order of deciding which transition to run goes like this:

* Check if the current transition is being [overriden ](transitions.md#overriding-transitions)
* Check if there's a key transition, a transition for a specific [NavigationKey ](../navigation-key.md)
* Check if there's a node transition, a transition for a specific type of [NavigationNode](../navigation-node/)
* Finally fall back to a default transition

The above can be set through the `NavigatorConfig`

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
    // Add some logic, this returns EnterExitTransition.
    defaultTransition { previousKey, newKey, isPop -> 
        if (isPop) {
            MaterialSharedAxisTransitionX.popEnterExit
        } else {
            MaterialSharedAxisTransitionX.enterExit
        }
    }
    
    // Key transitions
    keyTransition<MySpecificKey> { -> CrossFadeTransition }
    
    // Node transitions
    nodeTransition<Screen> { -> MaterialSharedAxisTransitionX }
    nodeTransition<BottomSheet> { -> CrossFadeTransition }
    nodeTransition<Dialog> { -> VerticalSlideTransition }
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

### Animating elements with navigation transitions

All navigation nodes are animated within an `AnimatedContent` which provides an `AnimatedVisibilityScope`. We can gain access to that scope in our Composables using  `NavigationVisibilityScope`:

```kotlin
@Composable
fun HomeScreen() {
    NavigationVisibilityScope {
        Text(
            text = "I animate with the navigation transition",
            modifier = Modifier
                .animateEnterExit(
                     enter = slideInVertically { it * 3 } + fadeIn(),
                     exit = slideOutVertically { it } + fadeOut()
                 )          
        )
    }
}
```
