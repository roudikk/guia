# Navigation Node

A `NavigationNode` is the UI representation of an entry in our navigation flow.

It's a very simple interface:

```kotlin
interface NavigationNode {
    val content: @Composable () -> Unit
}
```

By default, Guia provides 3 conventional navigation node implementations.

Navigation nodes don't provide argument passing or can be navigated to directly. They simply render a UI, passing arguments and navigating is done using [Navigation Keys](navigation-key.md) which will see in the next step.
