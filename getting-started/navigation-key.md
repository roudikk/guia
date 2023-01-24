# Navigation Key

A `NavigationKey` represents an entry in our navigation flow. The key itself does not have any UI representation or UI logic. It can have arguments which is how argument passing works using Guia. All arguments must be `Parcelable`, since the key is saved and restored.

To create a key:

```kotlin
@Parcelize
class ProfileKey(val profileId: String): NavigationKey
```

The reason why the key is not tied to the UI directly is to make multi module navigation easier.&#x20;

We will see later how we can tie a key to [NavigationNode ](navigation-node.md)and render some UI.

However, not all projects are multi module, to make it easier to have support for both multi module and single module applications, or even modules that have their own navigation flows and don't require this separation within that module, we can use `NavigationKey.WithNode<NavigationNode>`:

```kotlin
@Parcelize
class ProfileKey(val profileId: String): NavigationKey.WithNode<Screen> {

    override fun navigationNode() = Screen { 
        Text("Profile: $profileId")
    }
}
```
