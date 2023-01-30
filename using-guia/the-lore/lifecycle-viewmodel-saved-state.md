# Lifecycle, ViewModel, Saved State

Lifecycle in Guia is managed by the `LifecycleManager`API.

It creates a `LifecycleEntry` for each `BackstackEntry` in our navigator. Each lifecycle entry has its own `Lifecycle`, `ViewModel` scope, `SavedStateRegistry`

The `LifecycleManager`will react to changes in the backstack and update the lifecycles of all entries accordingly. It also decides which entries are currently visible on the screen.

Nesting navigators is fine since the manager will also propagate lifecycle changes between hosts/children.

The `LifecycleManager` API is public[ and](#user-content-fn-1)[^1] you can use it to create your own UI Logic. Check ViewPager and Card Stack examples.



[^1]: 
