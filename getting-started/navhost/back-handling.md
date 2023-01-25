# Back Handling

Guia provides common use cases for back handling for bottom tab navigation.

### Back to Default Stack

```kotlin
navHost.DefaultStackBackHandler(HomeStackKey)
```

Now whenever we are on a tab that isn't `HomeStackKey` the tab will be changed to that key.

### Stack History

```kotlin
navHost.StackHistoryBackHandler()
```

Now whenever we call `setActive` , that handler will remember the sequence of tab changing and goes back to those tabs as we click back.
