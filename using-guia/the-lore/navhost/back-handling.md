# Back Handling

Guia provides common use cases for back handling for bottom tab navigation.

### Back to Default Stack

```kotlin
navHost.DefaultStackBackHandler(HomeStackKey)
```

Now whenever we are on a tab that isn't `HomeStackKey` and we click back, the tab will be changed to that key.

### Stack History

```kotlin
navHost.StackHistoryBackHandler()
```

Now whenever we call `setActive` , that handler will remember the sequence of tabs being active and goes back to those tabs as we click back.

### Create your own

None of the back handlers mentioned above using anything internal to the library. They simply check the state of `NavHost.currentEntry` and the navigators' backstacks and react to it to form a back handling strategy. So you're free to create your own back strategy.
