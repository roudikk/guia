# Dynamic Navigation Node

We can define a dynamic `NavigationKey` that renders a different navigation node based on the current screen width for example. Even if we change the navigation node from a `Dialog` to a `BottomSheet` we can persist the same `ViewModel` and `SavedStateRegistry`. To share some UI State between the two types, make sure to add a `key` to your `rememberSaveable` call, since they are rendered in different containers, they have different `compositionKeyHash` which is used to automatically key the `rememberSaveable` calls.

```kotlin
val navigator = rememberNavigator { 
    if (screenWidth <= 600) {
        dialog<DynamicKey> { DynamicContent() }
    } else {
        bottomSheet<DynamicKey> { DynamicContent() }
    }
}

@Composable
fun DynamicContent() {
    // This saveable text will be persistent even if we change from a dialog to a bottomsheet!
    val someText = rememberSaveable(key = "text-key") { mutableStateOf("Text") }
}
```
