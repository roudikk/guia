# NavHost Multi module

In [NavHost](../navhost/) we saw how we can create a component that can manage the state of multiple navigators at the same time. In this section we will learn how to do that in a multi module project.

First, if you haven't read [Multi Module Navigation](../multi-module-navigation.md), read that first then come back, it will give a bit more context about the example being used below.

We will use the same structure that we had in [Multi Module Navigation](../multi-module-navigation.md), where we have two features: Profile and Home, but instead of them being individual screens, now we will be using them in a `NavHost`.

We will add two more features: Onboarding and BottomNav. Onboarding will be the first screen when we open the app, BottomNav will be the screen holding the `NavHost`.







