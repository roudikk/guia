# UI Tests

Each key has a tag associated with it that can identify it when running Compose UI tests, it wraps the [NavigationNode](navigation-node/)'s Composable with a `Modifier.tag()`.

To check whether a screen is visible you can check if its tag is being displayed, this is handy when running UI tests for a flow of screens.

```kotlin
rule.onNodeWithTag(NavigationKey.tag<HomeKey>()).assertIsDisplayed()
```

And the tag itself can be overriden, for example if we have multiple instances of the same key differentiated by some id we can override the tag in the key:

```kotlin
@Parcelize
class DetailsKey(val item: String) : NavigationKey {
    companion object {
        fun tag(item: String) = "DetailsKey_$item"
    }

    override fun tag() = tag(item)
}
```

Then we will be able to test different instances of that key:

```kotlin
rule.onNodeWithTag(DetailsKey.tag("some_id")).assertIsDisplayed()
rule.onNodeWithTag(DetailsKey.tag("some_other_id")).assertIsNotDisplayed()
```
