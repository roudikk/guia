# Navigator

A `Navigator` is basically a backstack manager. The only public API a `Navigator` has is `setBackstack` which takes in the new backstack.

All [navigation operations](navigation-operations.md) are extension functions that just call `setBackstack`, check that page for an extensive list of already provided operations, or create your own.

To create a navigator:

```kotlin
val myNavigator = rememberNavigator()
```

A backstack can be empty, which is why we don't need to provide some initial key, but we can do so by providing `initialKey`:

<pre class="language-kotlin"><code class="lang-kotlin"><strong>@Parcelize
</strong><strong>class HomeKey: NavigationKey
</strong>
val myNavigator = rememberNavigator(initialKey = HomeKey())
</code></pre>

Now we can use the `Navigator` to update our current backstack:

```kotlin
myNavigator.setBackstack(HomeKey().entry(), ProfileKey("MyProfileId").entry())
```

You can see we call `.entry()` inside our `setBackstack` call. This is because the back stack is not a list of `NavigationKey` but a list of `BackstackEntry` which is a unique entry of a given key inside our backstack, it simply provides a unique id for a given key, that way we can differentiate two keys of the same type inside our backstack. That id is later used for lifecycle management and state saving/restoration.

Next, we will learn how to tie a `NavigationKey` to a `NavigationNode` inside our `Navigator` using [NavigatorConfig](../../navigator-config.md).
