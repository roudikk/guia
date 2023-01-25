# Containers

Now we can finally render the state of our navigator! For that, we use `NavContainer`

```kotlin
navigator.NavContainer()
```

`NavContainer` contains a `ScreenContainer`, `BottomSheetContainer` and a `DialogContainer` and will render our keys based on their navigation nodes and all the transitions we defined.

### ScreenContainer

This will render all `Screen` navigation nodes. The dimensions of the container are bound to the `NavContainer`.

### BottomSheetContainer

This will render all `BottomSheet` navigation nodes.

```kotlin
fun Navigator.NavContainer(
    ..
    bottomSheetScrimColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.32F),
    bottomSheetContainer: Container = { content -> content() },
    ..
)
```

We can override the above parameters to provide defaults for our bottom sheets. In Guia, bottom sheets don't have any surface by design. This is to allow Devs to choose their choice of containers. For example, Material 2 or Material 3 surfaces. Every [BottomSheet](navigation-node/bottom-sheet.md) can also define its own scrim color, but we can override `bottomSheetScrimColor` to provide a sensible default.

### DialogContainer

This will render all `Dialog` navigation nodes.

```kotlin
fun Navigator.NavContainer(
    ..
    dialogContainer: Container = { content -> content() }
)
```

We can override the above parameter to provide a surface for our dialogs. In Guia, dialogs don't have any surface by default, for the same reasoning mentioned in [BottomSheetContainer](containers.md#bottomsheetcontainer).
