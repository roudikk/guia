# Multi module navigation

Let's suppose we have two features: Home and Profile

First, we can create these modules:

`:feature:home` and `:feature:home:navigation`

`:feature:profile` and `:feature:profile:navigation`

And ofcourse we have our `:app` module.



Our `:feature:home` gradle file will depend on `:feature:profile:navigation` module. So we only have access to `ProfileKey`

Inside `:feature:home:navigation` we can declare our keys:

```kotlin
HomeNavigation.kt

@Parcelize
class HomeKey: NavigationKey
```

Then inside our `:feature:home` module:

```kotlin
HomeNavigationBuilder.kt

fun NavigatorConfigBuilder.homeNavigation() {
    screen<HomeKey> { HomeScreen() }
}

@Composable
fun HomeScreen() {
    val navigator = requireLocalNavigator()
    Column {
        Text("Home Screen!")
        Button(onClick = { navigator.push(ProfileKey("profileId")) }) {
            Text("Navigate to profile")
        }
    }
}
```
