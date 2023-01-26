# NavHost Multi module

In [NavHost](../navhost/) we saw how we can create a component that can manage the state of multiple navigators at the same time. In this section we will learn how to do that in a multi module project.

First, if you haven't read [Multi Module Navigation](../multi-module-navigation.md), read that first then come back, it will give a bit more context about the example being used below.

We will use the same structure that we had in [Multi Module Navigation](../multi-module-navigation.md), where we have two features: Profile and Home, but instead of them being individual screens, now we will be using them in a `NavHost`.

We will add two more features: Onboarding and BottomNav. Onboarding will be the first screen when we open the app, BottomNav will be the screen holding the `NavHost`.&#x20;

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
ProfileNavigationBuilder.kt

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





