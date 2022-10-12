# Compose Navigator

Navigator tailored to work nicely with composable screens.

|           |  Features  |
|-----------|-------------|
:tada: | Simple API
:recycle: | State restoration
:train: | Nested navigation
:link: | Deep Linking
:back: | Multiple back stack strategies
:twisted_rightwards_arrows: | Support for Enter/Exit compose transitions
:rocket: | Extensive navigation operations
:phone: | Result passing between navigation nodes

### Table of Contents

1. [Installation](#installation)
2. [Navigation Nodes](#navigation-nodes)  
    1. [Lifecycle](#lifecycle)
    2. [Reusing NavigatioNode](#reuse-navigation-node)
3. [Navigator](#navigator)
4. [NavContainer](#nav-container)
5. [Navigation Operations](#navigation-operations)
6. [Animations](#animations)
    1. [Animating between navigation nodes](#animations-nodes)
    2. [Animating between navigation stacks](#animations-stacks)
    3. [Animating navigation node elements](#animations-elements)
7. [Back Stack Management](#back-stack-management)
8. [State Restoration](#state-restoration)
9. [Result passing](#result-passing)
10. [Nested Navigation](#nested-navigation)
11. [Deeplinks](#deeplinks)
12. [ViewModels](#view-models)
13. [Previews](#previews)
14. [UI Tests](#ui-tests)

## Installation <a name="installation" />

```gradle
dependencies {
    implementation("com.roudikk.compose-navigator:compose-navigator:2.0.2")
}
```
For proguard rules check [consumer-rules.pro](https://github.com/roudikk/compose-navigator/blob/master/compose-navigator/consumer-rules.pro)

Compose navigator usses `Parcelable` interfaces, it's recommended to use `Parcelize` in your project. `build.gradle`:

```gradle
plugins {
    // groovy
    id 'kotlin-parcelize'
    
    // kotlin
    id("kotlin-parcelize")
}
```

## Navigation nodes <a name="navigation-nodes"/>

Screen:

```kotlin
@Parcelize
class MyScreen(val myData: String) : Screen {
    
    @Composable
    override fun Content() {
        
    }
}
```

Dialog:

```kotlin
@Parcelize
class MyDialog(val myData: String) : Dialog {

    override val dialogOptions: DialogOptions
        get() = DialogOptions(
            modifier: Modifier = Modifier.widthIn(max = 300.dp), // The Dialog container Modifier
            dismissOnBackPress = true, // When set to false, back press will not cancel this dialog
            dismissOnClickOutside = true, // When set to false, clicking outside the dialog doesn't dismiss it
            securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit // Policy setting for the window
        )

    @Composable
    override fun Content() {

    }
}
```

Bottom Sheet:

```kotlin
@Parcelize
class MyBottomSheet(val myData: String) : BottomSheet {

    override val bottomSheetOptions: BottomSheetOptions
        get() = BottomSheetOptions(
            modifier = Modifier, // Controls the BottomSheet's outside Composable.
            confirmStateChange = { true } // When set to { false }, bottom sheet state changes will be ignored and animate back to the last locked state. 
        )
    
    @Composable
    override fun Content() {

    }
}
```

Bottom sheets do not have a default surface as a background. This is to
let developers choose which composable is the parent of a bottom sheet 
(For ex: Surface2 or Surface3) inside their own implementation.

However, to make it easier to have a consistent bottom sheet design
across all bottom sheets (if that's the case), you can override 
`bottomSheetOptions` inside `NavContainer` to provide a common composable parent 
to all bottom sheets.

### Lifeycle <a name="lifecycle"/>

Each `NavigationNode` will have a corresponding `BackStackEntry` when added to the backstack.

A `BackStackEntry` is a `LifecycleOwner`, `ViewModelStoreOwner` and a `SavedStateRegistryOwner` which means
every navigation node has its own lifecycle and can have its own scoped ViewModels and supporting `SavedStateHandle`.

In addition, the library provides `LifecycleEffect` to listen to lifecycle events:

```kotlin
/**
 * Lifecycle listener for a [NavigationNode]
 *
 * @param onEnter, called when the node enters composition, this can be called when the node is initially rendered
 * or when the node is revisited.
 * @param onResume, called when the [NavigationNode] is resumed. This is called right after [onEnter]
 * and when the activity is resumed.
 * @param onPause, called when the [NavigationNode] is paused. This is called right before [onExit]
 * and when the activity is paused.
 * @param onExit, called when the node leaves composition. This doesn't mean the node is necessarily
 * not going to be revisited.
 * @param onDestroy, called the node is completely destroyed, this means the node will never be
 * revisited again.
 */
@Composable
fun NavigationNode.LifecycleEffect(
    onEnter: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onExit: () -> Unit = {},
    onDestroy: () -> Unit = {}
)
```

### Reusing Navigation Nodes <a name="reuse-navigation-node"/>

A `NavigationNode` can be a `Screen`, `Dialog` and a `BottomSheet` at the same time! 

To decide which type it is when navigation, use `NavigationNode.asScreen()/asDialog()/asBottomSheet()`.

Please note that if a `NavigationNode` does support multiple types then you must use the `asX()` when navigating, otherwise the navigator will not know which type you want and might result in weird behavior.

## Navigator <a name="navigator"/>

A Navigator is the essential component for navigation and can be used to navigate between navigation nodes.

To initialize a navigator call:

```kotlin
val myNavigator = rememberNavigator(initialNavigationNode = MyScreen())
```
`rememberNavigator` can also take a `NavigationConfig` which can either be `SingleStack` or `MultiStack`.

For multi-stacks navigation with history for each stack (For ex: Bottom navigation),
each stack should have a unique `StackKey`

To initialize a Navigator with multiple stacks call:

```kotlin
val myNavigator = remmeberNavigator(
    navigationConfig = with(
        listOf(
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = AppStackKey.Stack1,
                initialNavigationNode = Stack1Screen()
            ),
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = AppStackKey.Stack2,
                initialNavigationNode = Stack2Screen()
            ),
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = AppStackKey.Stack3,
                initialNavigationNode = Stack3Screen()
            )
        )
    ) {
        NavigationConfig.MultiStack(
            entries = this,
            initialStackKey = this[0].key,
            backStackStrategy = BackStackStrategy.Default,
            defaultTransition = MaterialSharedAxisTransitionX, // Optional
            stackEnterExitTransition = navFadeIn() to navFadeOut() // Optional
        )
    }
)
```

## NavContainer <a name="nav-container"/>

To render the current state of a `Navigator`, call:

```kotlin
NavContainer(navigator = myNavigator)
```

For nested navigation, simply nest `NavContainer` in the `Content` of a parent `NavigationNode`:

```kotlin
class BottomNavScreen {
    @Composable
    override fun Content() {
        val bottomTabNavigator = rememberNavigator(navigationConfig = NavigationConfig.MultiStack..)
        NavContainer(bottomTabNavigator)
    }
}
```

## Navigation operations <a name="navigation-operations"/>

```kotlin

// Navigate to a navigation node, 
requireNavigator().navigate(navigationNode, transition) // Navigates the new node in the current stack.

// Navigate to a different stack
requireNavigator().navigateToStack(stackKey, transition) // Navigates to a stack with stack key.

// Pop back stack
requireNavigator().popBackStack() // Pops the last node from the current stack.

// Pop to
requireNavigator().popTo<NavigationNode>(inclusive)
requireNavigator().popTo(navigationNodeKey, inclusive) // In case overriding key inside NavigationNode.

// Pop to root
requireNavigator().popToRoot() // This will navigate to the root of the current stack.

// Set root
requireNavigator().setRoot(navigationNode, transition) // Replaces the root of the current stack.

// Replace last
requireNavigator().replaceLast(navigationNode, transition) // Replaces the last node.

// Replace Up To
requireNavigator().replaceUpTo(navigationNode, transition, inclusive, predicate) // Replaces all nodes until the node matching predicate.
requireNavigator().replaceUpTo<NavigationNode>(navigationNode, transition, inclusive) // Replaces all nodes until the node matching navigationNode.key.

// Move To Top
requireNavigator().moveToTop(matchLast, transition, predicate) // Moves the node matching predicate to the top of the stack, returns true if one exists.
requireNavigator().moveToTop<NavigationNode>(matchLast, transition) // Moves the node with a matching key to the top of the stack, returns true if one exists.

// Single instance
requireNavigator().singleInstance(navigatioNode, useExistingInstance, transition) // If useExistingInstance is true, then move the existing node to the top else creates a new instance, if useExistingInstance is false, then always navigate to a new instance, clearing the backstack of any matching keys.

// Single top
requireNavigator().singleTop(navigationNode, transition) // Only navigate if the top most node doesn't have the same key as navigationNode.

// Check if you can navigate back
requireNavigator().canGoBack()

// Any
requireNavigator().any(predicate) // Returns true if any navigation node in the current stack matches predicate condition.
```

## Animations <a name="animations"/>

Compose navigator provides a one to one match of all the `EnterTransition` and `ExitTransition` defined in compose-animation.
Prepend `nav` to compose equivalent function to find the navigation version of it.

For ex: `fadeIn()` -> `navFadeIn()`

`EnterTransition` is converted to `NavEnterTranstion` and `ExitTransition` is converted to `NavExitTransition`

Animation specs supported currently are: Tween, Snap and Spring. 
Prepend `nav` to compose equivalent function to find the navigation version of it.

For ex: `tween()` -> `navTween()`

### Animating between navigation nodes <a name="animations-nodes"/>

Example:

```kotlin
val MaterialSharedAxisTransitionX = NavTransition(
    enter = navSlideInHorizontally { (it * 0.2f).toInt() }
            + navFadeIn(animationSpec = navTween(300)),

    exit = navSlideOutHorizontally { -(it * 0.1f).toInt() }
            + navFadeOut(animationSpec = navTween(150)),

    popEnter = navSlideInHorizontally { -(it * 0.1f).toInt() }
            + navFadeIn(animationSpec = navTween(300)),

    popExit = navSlideOutHorizontally { (it * 0.2f).toInt() }
            + navFadeOut(animationSpec = navTween(150))
)
```

Usage:

```kotlin
requireNavigator().navigate(
  navigatioNode = navigationNode, 
  transition = MaterialSharedAxisTransitionX
)
```  

`NavigationConfig` contains `defaultTransition` to define a default transition if none was provided in the navigation operation.

### Animating between stacks <a name="animations-stacks"/>

Animating between stack changes can be done by using the `transition` parameter inside `navigatToStack`

For ex:

```kotlin
findNavigator().navigateToStack(stackKey, navFadeIn() to navFadeOut())
```

`NavigationConfig.MultiStack` contains `stackEnterExitTransition` for default stack transition. 

### Animating navigation node elements with screen transitions <a name="animations-elements"/>

`Content` function inside a `NavigatioNode` has reference to the `AnimatedVisibilityScope` used by the `AnimatedContent` that handles all transitions between navigation nodes.

To get access the `AnimatedVisibilityScope` use `LocalNavigationAnimation.current`

This means Composables inside navigation nodes can have enter/exit transitions based on the node's enter/exit state, using the `animateEnterExit` modifier.

For ex:

```kotlin
@Parcelize
class MyScreen : Screen {

    @Composable
    override fun Content() = with(LocalNavigationAnimation.current) {
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

## Back stack management <a name="back-stack-management"/>

`NavContainer` uses Compose's `BackHandler` to override back presses, it's defined before the navigation node's composable so navigation nodes can override back press handling by providing their own `BackHandler`

For Multi stack navigation, `NavigationConfig.MultiStack` provides 3 possible back stack strategies:

When the stack reaches its initial node then pressing the back button:

- Default: back press will no longer be handled by the navigator.
- BackToInitialStack: 
  - if the current stack is not the initial stack defined in `NavigationConfig.MultiStack` then the navigator will navigate back to the initial stack
  - If the current stack is the initial stack, then back press will no longer be handled by navigator
- CrossStackHistory:
  - When navigating between stacks, this strategy will navigate back between stacks based on `navigate/navigateToStack` operations
  
## State restoration <a name="state-restoration"/>

`NavContainer` uses `rememberSaveableStateHolder()` to remember composables ui states.

`NavigatorSaver` handles saving/restoring the navigator state upon application state saving/restoration internally.

Using `rememberSavable` inside your navigation node composables will remember the values of those fields.

## Result passing <a name="result-passing"/>

`Navigator` uses a coroutine `Channel` to pass results between navigation nodes.

A Result can be of any type.

Sending/receiving results are done by the key of the navigation node or a given string key:

```kotlin
    // Navigator.kt
    // Listening to results
    fun results(key: String) // Return results for a key in case of overriding the default key inside the navigation node
    inline fun <reified T : NavigationNode> results() // Convenience function that uses the default key for a NavigationNode
    
    // Sending results
    fun sendResult(result: Pair<String, Any>) // Sends result for a given navigation node key
    inline fun <reified T : NavigationNode> sendResult(result: Any) // Convenience function that uses the default key for a NavigationNode
    
    // Additionally, navigation node has an extension function on Navigator to make it even easier to listen to results
    
   // NavigationNode
   fun Navigator.nodeResults() = results(resultsKey)
```

Usage ex:

```kotlin
@Parcelize
class Screen1 : Screen {

    @Composable
    override fun Content() {
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
    override fun Content() {
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
  
## Nested Navigation <a name="nested-navigation"/>

Compose navigator offers navigator fetching functions:

- `findNavigator()` returns the closest navigator in navigation hierarchy, nullable
- `requireNavigator()` returns the closes navigator in navigation hierarchy, throws error if none exist.
- `findParentNavigator()` returns the parent navigator of the current navigator, nullable

You can nest navigators by calling `NavContainer()` inside a screen that is contained inside a parent `NavContainer()`

```kotlin

val parentNavigator = rememberNavigator(FirstScreen())

findNavigator() // Returns null

NavContainer(parentNavigator)
    
// FirstScreen.kt
 override fun Content() {
    findNavigator() // Returns parentNavigator
    findParentNavigator() // Returns null
        
    val nestedNavigator = rememberNavigator(NestedScreen())
    NavContainer(nestedNavigator) // Renders NestedScreen
}

// NestedSCreen.kt
override fun Content() {
    findNavigator() // Returns nestedNavigator
    findParentNavigator() // Returns parentNavigator
}
            
// NavContainer in NestedScreen will override the back press of NavContainer in FirstScreen until it can no longer go back
// Then NavContainer in FirstScreen will take over back press handling.
// Both navigators can use any navigation node defined anywhere.
```

## Deeplinks <a name="deeplinks"/>

`rememberNavigator` has an `initializer` argument which can be used to initialize the state of the navigator, this can be used
to start the navigator with navigation nodes given the initial activity's intent.

For more details on how deeplinking can be implemented check [DeepLinkViewModel](https://github.com/roudikk/compose-navigator/blob/master/sample/src/main/java/com/roudikk/navigator/sample/DeepLinkViewModel.kt)

## ViewModels <a name="view-models"/>

Each Navigation node is wrapped around a `BackStackEntry` that has its own Lifecycle, viewModelStoreOwner and savedStateRegistry.

This means calling `viewModel()` inside a Navigation Node will provide a `ViewModel` tied to the node's lifecycle and will be disposed
when the `NavigationNode` is no longer used.

To use a singleton `ViewModel` across multiple `NavigationNode`, it's recommended to define a `LocalNavHostViewModelStoreOwner`:

```kotlin
val LocalNavHostViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner> {
    error("Must be provided")
}
```

Which can then be used in your main activity to provide the activity's `ViewModelStoreOwner`:

```kotlin
CompositionLocalProvider(
    LocalNavHostViewModelStoreOwner provides requireNotNull(LocalViewModelStoreOwner.current)
) {
    NavContainer(navigator = myNavigator)
}
```

And to retrieve the `ViewModel`:

```kotlin
val sharedViewModel = viewModel<SharedViewModel>(viewModelStoreOwner = LocalNavHostViewModelStoreOwner.current)
```

## Previews

It's recommended to separate the navigation logic from the composable previews.

Instead of doing:

```kotlin
@Composable
fun MyComposable() {
    val navigator = requireNavigator()
    
    Button(onClick = { navigator.navigator(SomeScreen()) }) {
        Text("Navigate")
    }
}
```

You should do:

```kotlin
@Composable
fun MyComposable(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Navigate")
    }
}
```

And delegate the navigation to the caller instead.

## UI tests <a name="ui-tests" />

For Individual navigation nodes, it's recommended to test that the actions that perform navigation operations to be lambdas rather than use the navigation component.

This will make it easier to preview the composables and easier to assert that actions have been performed, for ex:

```kotlin
@Composable
private fun HomeContent(
    onItemSelected: (String) -> Unit = {} // This can be easily tested in unit tests
) {
    // Content
}
```

However, when testing UI flows across multiple navigation nodes, Compose Navigator adds a test tag
using the navigation node key to all navigation nodes in the Compose tree, making it easy to test whether
a navigation node is displayed, using `ComposeTestRule.onNodeWithTag(tag).assertIsDisplayed()`, for ex:

```kotlin
    @Test
    fun details_newRandomItem_addsToStack() {
        rule.navigateDetails()
        rule.onNodeWithText("New random item").performClick()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<DetailsScreen>()).assertIsDisplayed()
        rule.activity.onBackPressed()
        rule.onNodeWithTag(key<HomeScreen>()).assertIsDisplayed()
    }
```

Check the sample app UI tests for more examples.

License
=======

    Copyright 2022 Roudi Korkis Kanaan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
