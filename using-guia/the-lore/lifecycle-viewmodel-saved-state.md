# Lifecycle Management

Lifecycle in Guia is managed by the `BackstackManager` API.

It creates a `LifecycleEntry` for each `BackstackEntry` in our navigator. Each lifecycle entry has its own `Lifecycle`, `ViewModel` scope, `SavedStateRegistry`

The `BackstackManager` will react to changes in the backstack and update the lifecycles of all entries accordingly. It also decides which entries are currently visible on the screen.

Nesting navigators is fine since the backstack manager will also propagate lifecycle changes between hosts/children.

The `BackstackManager` API is public and you can use it to create your own UI Logic. Check ViewPager and Card Stack examples.

