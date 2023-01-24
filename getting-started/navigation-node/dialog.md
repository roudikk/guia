# Dialog

A `Dialog` will be rendered inside, well, a `Dialog` Composable.

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
    val modifier: Modifier = Modifier.widthIn(max = 350.dp),
    val dismissOnClickOutside: Boolean = true,
    val dismissOnBackPress: Boolean = true,
    val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
)
```
