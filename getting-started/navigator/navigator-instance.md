# Navigator Instance

[NavContainer](../containers.md) provides the navigator it's using to all its children using Composition Locals.

We can use the below to get the current navigator inside a Composable.

<pre class="language-kotlin"><code class="lang-kotlin"><strong>@Composable
</strong><strong>fun HomeScreen() { 
</strong>    // Returns an optional navgiator
    val navigator = localNavigator() 

    // Returns a none-null navigator, throws an exception if one isn't provided.
    val navigator = requireLocalNavigator()
<strong>}
</strong></code></pre>

For instances where we are nesting a navigator inside another navigator's `NavContainer` and we want the direct parent we can use:

```kotlin
@Compoasble
fun HomeScreen() {
    // Returns an optional navgiator
    val parentNavigator = localParentNavigator()
    
    // Returns a none-null navigator, throws an exception if one isn't provided.
    val parentNavigator = requireLocalParentNavigator()
}
```

{% hint style="info" %}
Make sure to hoist the navigation operations to another Composable. To make previews easier.
{% endhint %}
