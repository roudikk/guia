# Navigation Key

A `NavigationKey` represents a node in your navigation hierarchy. The key itself does not have any UI representation or UI logic. It's simply a unique key for a current back stack entry.

To create a key:

```kotlin
@Parcelize
class ProfileKey(val profileId: String): NavigationKey
```

