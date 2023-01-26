# Dialog

A `Dialog` will be rendered inside a `Dialog` Composable.

```kotlin
class Dialog(
    override val content: @Composable () -> Unit
) : NavigationNode {
    ..
    var dialogOptions by mutableStateOf(DialogOptions())
    ..
}
```

Similar to [BottomSheet](bottom-sheet.md), a `Dialog` has stateful `DialogOptions` that can be updated at any time.

```kotlin
data class DialogOptions(
    val dismissOnClickOutside: Boolean = true,
    val dismissOnBackPress: Boolean = true,
    val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
)
```

Navigating between 2 Dialogs will also animate the changes within the dialog container that hosts them.

### Updating Dialog state

Note that `DialogOptions` currently are not saved/restored so make sure you have a backing saveable state if needed in your Composable.

```kotlin
@Composable
fun MyDialog() {
    val dialog = requireLocalDialog() // Get the local dialog node
    var dismissOnBackPress by rememberSaveable { mutableStateOf(false)) }
    
    LaunchedEffect(dismissOnBackPress) {
        dialog.dialogOptions = dialog.dialogOptions.copy(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnBackPress
        )
    }
    
    Button(onClick = { 
        dismissOnBackPress = !dismissOnBackPress
    }) {
        Text(text = "Toggle Back Press!")
    }
}
```
