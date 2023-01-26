# NavHost Multi module

In [NavHost](../navhost/) we saw how we can create a component that can manage the state of multiple navigators at the same time. In this section we will learn how to do that in a multi module project.

First, if you haven't read [Multi Module Navigation](./), read that first then come back, it will give a bit more context about the example being used below.

We will use the same structure that we had in [Multi Module Navigation](./), where we have two features: Profile and Home, but instead of them being individual screens, now we will be using them in a `NavHost`.

We will add two more features: Onboarding and BottomNav. Onboarding will be the first screen when we open the app, BottomNav will be the screen holding the `NavHost`.&#x20;

### Updating Home and Profile modules

Now that those features are tabs, we can also add a `StackKey` to their respective navigation modules:

```kotlin
HomeNavigation.kt
@Parcelize
object HomeStackKey: StackKey

ProfileNavigation.kt
@Parcelize
object ProfileStackKey: StackKey
```

### Onboarding feature module

`feature:onboarding`  will depend on `feature:onboarding:navigation` and `feature:bottomnav:navigation`

First, inside `:feature:onboarding:navigation` module we can declare our onboarding key:

```kotlin
OnboardingNavigation.kt

@Parcelize
class OnboardingKey(): NavigationKey
```

Then, inside our `:feature:onboarding` we can create an extension function on `NavigatorConfigBuilder`  to tie our key to a Composable:

```kotlin
OnboardingNavigationBuilder.kt

fun NavigatorConfigBuilder.onboardingNavigation() {
    screen<OnboardingKey> { key -> OnboardingScreen() }
}

class OnboardingScreen() {
    val navigator = requireLocalNavigator()
    Column {
        Text("Onboarding!")
        Button(onClick = { navigator.push(BottomNavKey()) }) {
            Text("Go to Bottom nav")
        }
    }
}
```

### BottomNav feature module

`feature:bottomnav`  will depend on `feature:bottomnav:navigation` , `feature:home:navigation` and `feature:profile:navigation`

First, inside `feature:bottomnav:navigation` module we can declare our bottom nav key:

```kotlin
BottomNavNavigation.kt

@Parcelize
class BottomNavKey: NavigationKey
```

Then, inside our `:feature:bottomnav` we can create an extension function on `NavigatorConfigBuilder`  to tie our key to a Composable, however here we will actually be expecting the navigation for both home and profile. We can pass those as parameters to our `bottomNavNavigation` function.

```kotlin
OnboardingNavigationBuilder.kt

fun NavigatorConfigBuilder.bottomNavNavigation(
    homeNavigation: NavigatorConfigBuilder.() -> Unit,
    profileNavigation: NavigatorConfigBuilder.() -> Unit,
) {
    screen<BottomNavKey> { key -> 
        BottomNavScreen(
            homeNavigation = homeNavigation,
            profileNavigation = profileNavigation
        ) 
    }
}
```

Then we pass them down to our screen where we will create a `NavHost`:

<pre class="language-kotlin"><code class="lang-kotlin">class BottomNavScreen(
    homeNavigation: NavigatorConfigBuilder.() -> Unit = {},
    profileNavigation: NavigatorConfigBuilder.() -> Unit = {}
) {
    val homeNavigator = rememberNavigator { homeNavigation() }
    val profileNavigator = rememberNavigator { profileNavigation() }
    
    val navHost = rememberNavHost(
        initialKey = HomeStackKey,
        entries = setOf(
            StackEntry(HomeStackKey, homeNavigator),
            StackEntry(ProfileStackKey, profileNavigator)
        )
<strong>    )
</strong><strong>    
</strong><strong>    navHost.NavContainer()
</strong>}
</code></pre>

### App module

Finally, we can tie everything up together in our app module!

```kotlin
val navigator = rememberNavigator(
    initialKey = OnboardingKey()
) {
    onboardingNavigation()
    bottomNavNavigation(
        homeNavigation = { homeNavigation() },
        profileNavigation = { profileNavigation() }
    )    
}

navigator.NavContainer()
```
