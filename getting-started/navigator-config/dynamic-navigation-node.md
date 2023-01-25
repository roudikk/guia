# Dynamic Navigation Node

We can even define a dynamic `NavigationKey` that renders a different navigation node based on the current screen width. Even if we change the navigation node from a `Dialog` to a `BottomSheet` we can persist the same `ViewModel`, same UI state of the content itself!

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
