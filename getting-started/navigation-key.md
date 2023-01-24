# Navigation Key

A `NavigationKey` represents a block in your navigation hierarchy. The key itself does not have any UI representation or UI logic. It can have arguments which is how argument passing works using Guia. All arguments must be parcelable, since the key is saved and restored.

To create a key:

```kotlin
@Parcelize
class ProfileKey(val profileId: String): NavigationKey
```

The reason why the key is not tied to the UI directly is to make multi module navigation easier.&#x20;

We will see later how we can tie a key to `Composable` and render some UI.
