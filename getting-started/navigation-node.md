# Navigation Node

A `NavigationNode` is the UI representation of an entry in our navigation flow.

It's a very simple interface:

```kotlin
interface NavigationNode {
    val content: @Composable () -> Unit
}
```

By default, Guia provides 3 conventional navigation node implementations.

### Screen

A `Screen` doesn't necessarily mean a full screen page. The dimensions of the screen are controlled by the container it's hosted in and its own dimensions. But it's a convenient name for defining a UI for a navigation entry that isn't a `BottomSheet` or a `Dialog`

```kotlin
class Screen(
    override val content: @Composable () -> Unit
) : NavigationNode
```

### BottomSheet

A `BottomSheet`
