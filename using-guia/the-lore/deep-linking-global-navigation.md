# Deep Linking / Global Navigation

Both [Navigator](navigator/) and [NavHost](navhost/) can be initialized before they are eventually rendered.

```kotlin
val navigator = rememberNavigator(
    initialize = { navigator -> 
        // Set an initial back stack
    }
)

val navHost = rememberNavHost(
    initialize = { navHost ->
        // Set active entry / update navigators
    }
)
```

The [sample deeplinking example](../../sample/feature-common/src/main/java/com/roudikk/guia/sample/feature/common/deeplink/GlobalNavigator.kt) shows one way of handling deeplinking when an activity is created and when a new intent is received while the activity is already created.

{% hint style="info" %}
The [GlobalNavigator](../../sample/feature-common/src/main/java/com/roudikk/guia/sample/feature/common/deeplink/GlobalNavigator.kt) example can also be used for global navigation. This is very handy where we want to navigate from a deeply nested area of our navigation hierarchy to another with no direct navigation link.
{% endhint %}
