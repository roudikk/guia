# Back Handling

Each navigator has a `overrideBackPress` that is stateful and can be updated to allow overriding the back press. It's set to `true` by default and it [pops](navigation-operations.md#pop) the back stack as we click back.

We can override back press in our Composables by using `NavBackHandler`:

```kotlin
@Composable
fun HomeScreen() { 
    NavBackHandler(enabled = true) {
        // Now the navigator will not pop the back stack.
    }
}
```

You can also initialize the navigator with `overrideBackPress = false` and use your own logic for back handling based on the backstack.
