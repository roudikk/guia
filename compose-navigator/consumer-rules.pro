
# Keep class names for navigation nodes and navigation key to prevent issues with
# using class names as keys in navigatio nodes

-keepnames class * extends com.roudikk.navigator.navhost.StackKey
-keepnames class * extends com.roudikk.navigator.core.NavigationNode
