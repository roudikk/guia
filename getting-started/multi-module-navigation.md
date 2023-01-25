# Multi module navigation

Let's suppose we have two features: Home and Profile

First, we can create these modules:

`:feature:home` and `:feature:home:navigation`

`:feature:profile` and `:feature:profile:navigation`

And ofcourse we have our `:app` module.

### Profile feature module

`:feature:profile` will depend on `feature:profile:navigation`

First, inside `:feature:profile:navigation` module we can declare our profile key:

```kotlin
ProfileNavigation.kt

@Parcelize
class ProfileKey(val profileId: String): NavigationKey
```

Then, inside our `:feature:profile` we can create an extension function on `NavigatorConfigBuilder`  to tie our key to a Composable:

```kotlin
ProfileNavigationBuilder.kt

fun NavigatorConfigBuilder.profileNavigation() {
    screen<ProfileKey> { key -> ProfileScreen(profileId = key.profileId) }
}

class ProfileScreen(val profileId: String) {
    val navigator = requireLocalNavigator()
    Column {
        Text("Profile for: $profileId")
        Button(onClick = { navigator.pop() }) {
            Text("Go back home")
        }
    }
}
```

### Home feature module

Our `:feature:home` gradle file will depend on `:feature:profile:navigation` and `:feature:home:navigation` modules. So we only have access to `ProfileKey`

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

### app module

Finally we can link those features in our `:app` module, since it depends on all those modules above:

```kotlin
val navigator = rememberNavigator(initialKey = HomeKey()) {
    homeNavigation()
    profileNavigation()
}

navigator.NavContainer()
```

