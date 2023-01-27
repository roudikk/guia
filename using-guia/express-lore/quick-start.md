# Quick Start

First, let's declare some destinations, in Guia, they are called a [NavigationKey](../the-lore/navigation-key.md)

<pre class="language-kotlin"><code class="lang-kotlin"><strong>@Parcelize
</strong>class HomeKey: NavigationKey

@Parcelize
class ProfileKey(val profileId: String): NavigationKey
</code></pre>

Then we declare a [Navigator](../the-lore/navigator/):

```kotlin
val navigator = rememberNavigator(initialKey = HomeKey()) // Initial Key is optional
```

To link a [NavigationKey](../the-lore/navigation-key.md) to a Composable we can use the `builder` parameter in `rememberNavigator`:

```kotlin
val navigator = rememberNavigator(initialKey = HomeKey()) {
    screen<HomeKey> { HomeScreen() }
    bottomSheet<ProfileKey> { key -> ProfileScreen(profileId = key.profileId) }
    dialog<DialogKey> { DialogScreen() }
}
```

Alternatively, a key can host its own Composable, called a [NavigationNode](../the-lore/navigation-node/), then we don't need to declare it in our builder, the builder is convenient in [Multi Module projects](../the-lore/multi-module-navigation/)

```kotlin
class DetailsBottomSheetKey(
    val item: String
) : NavigationKey.WithNode<BottomSheet> {

    override fun navigationNode() = BottomSheet {
        Text(text = "Item: $item")
    }
}
```

To get the instance of our navigator and do some navigation:

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

Finally we render the navigator's state:

```kotlin
navigator.NavContainer()
```

We can also add some [Transitions](../the-lore/navigator-config/transitions.md), type-safe [Results](../the-lore/results.md), [bottom-nav](../the-lore/navhost/) behaviour and more. Check [The Lore](../the-lore/) for more comprehensive usage of Guia.
