# Containers

Now we can finally render the state of our navigator! For that, we use `NavContainer`

```kotlin
navigator.NavContainer()
```

By default, `NavContainer()` contains a `ScreenContainer`, `BottomSheetContainer` and a `DialogContainer` and will render our keys based on their navigation nodes.

### ScreenContainer



### BottomSheetContainer

```kotlin
fun Navigator.NavContainer(
    ..
    bottomSheetScrimColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.32F),
    bottomSheetContainer: Container = { content -> content() },
    ..
)
```

We can override the above parameters to provide defaults for our bottom sheets. In Guia, bottom sheets don't have any surface by design. This is to allow develoeprs to choose their choice of containers. For example, Material 2 or Material 3 surfaces. Every [BottomSheet](navigation-node/bottom-sheet.md) can also define its own scrim color but we can override `bottomSheetScrimColor` to provide a sensible default.

