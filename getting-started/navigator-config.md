# Navigator Config

Going back to our `rememberNavigator` call, we can also provide a `NavigatorConfig`. This is where we link a `NavigationKey` to a `NavigationNode`, define transition animations, etc.

For example:

```kotlin
val myNavigator = rememberNavigator {
    screen<HomeKey> { Text("This is a screen!") }
    bottomSheet<ProfileKey> { key -> Text("This is a profile for: ${key.profileId}"}
    dialog<MyDialogKey> { Text("This is a dialog!") }
}
```

Note that a `NavigationKey` that uses [WithNode](navigation-key.md#self-hosted-navigationkey) don't need to define a representation inside `NavigatorConfig`

### Dynamic Navigation Nodes

We can even define a dynamic `NavigationKey` that renders a different navigation node based on the current screen width. Even if we change the navigation node from a `Dialog` to a `BottomSheet` we can persist the same `ViewModel`, same UI state of the content itself!

```kotlin
val navigator = rememberNavigator { 
    if (screenWidth <= 600) {
        dialog<DynamicKey> { Text("I'm now a dialog") }
    } else {
        bottomSheet<DynamicKey> { Text("I'm now a dialog") }
    }
}

@Composable
fun DynamicContent() {
    // This saveable text will be persistent even if we change from a dialog to a bottomsheet!
    val someText = rememberSaveable { mutableStateOf("Text") }
}
```
