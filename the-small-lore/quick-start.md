# Quick Start

First, let's declare some destinations, in Guia, they are called a [NavigationKey](../the-lore/navigation-key.md)

```kotlin
@Parcelize
class HomeKey: NavigationKey

@Parcelize
class ProfileKey(val profileId: String): NavigationKey
```

Then we declare a [Navigator](../the-lore/navigator/):

```kotlin
val navigator = rememberNavigator()
```

To link a [NavigationKey](../the-lore/navigation-key.md) to a Composable we can use the `builder` parameter in `rememberNavigator`:

```kotlin
val navigator = rememberNavigator {
    screen<HomeKey> { HomeScreen() }
    bottomSheet<ProfileKey> { key -> ProfileScreen(profileId = key.profileId) }
    dialog<DialogKey> { DialogScreen() }
}
```

Alternatively, a key can host its own Composable, called a [NavigationNode](../the-lore/navigation-node/), then we don't need to delcare it in our builder, the builder if very convenient in [Multi Module projects](../the-lore/multi-module-navigation.md)

```kotlin
class DetailsBottomSheetKey(
    val item: String
) : NavigationKey.WithNode<BottomSheet> {

    override fun navigationNode() = BottomSheet {
        Text(text = "Item: $item")
    }
}
```

To get instance to our navigator and do some navigation:

_(For a list of all default operations provided by Guia check_ [_Navigation Operations_](../the-lore/navigator/navigation-operations.md)_)_

```kotlin
@Composable
fun HomeScreen() {
    val navigator = requireLocalNavigator()
    
    // On Button click or some other event
    navigator.push(ProfileKey("some_id"))
    
    // We can also go back by calling pop
    navigator.pop()
}
```

We can also add some [Transitions](../the-lore/navigator-config/transitions.md) and pass type-safe [Results](../the-lore/results.md). Check [The Lore](broken-reference) for more comprehensive usage of Guia.
