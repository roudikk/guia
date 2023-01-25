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

Note that a `NavigationKey` that uses [WithNode](../navigation-key.md#self-hosted-navigationkey) don't need to define a representation inside `NavigatorConfig`
