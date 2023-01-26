# Results

Passing results between navigation keys is done in Guia using the `ResultManager` API, it's type safe and stateful.

```kotlin
interface ResultManager {
    fun result(key: String): Any?
    fun setResult(key: String, result: Any)
    fun clearResult(key: String)
}
```

A [Navigator](navigator/) itself is a `ResultManager` backed by a stateful key/value map.

```kotlin
data class Result(val item: String)

@Composable
fun HomeScreen() {
    val navigator = requireLocalNavigator()
    val result = navigator.result<Result>()
    
    Column {
        Text(text = "Result: $result")
        
        Button(onClick = { 
            navigator.setResult<Result>(UUID.randomUUID().toString) 
        }) {
            Text(text = "Refresh Result")
        }
        
        Button(onClick = { 
            navigator.clearResult<Result>() 
        }) {
            Text(text = "Clear Result")
        }
    }
}

@Composable
fun AnotherScreen() {
    val navigator = requireLocalNavigator()
    
    Button(onClick = { 
        navigator.setResult<Result>(UUID.randomUUID().toString()) 
        navigator.pop()
    }) {
        Text(text = "Return Result")
    }
}
```

### Survive state restoration

By simply marking our result as `Parcelable` our result can now be saved and restored, the `ResultManager`  will handle that internally.

```kotlin
@Parcelize
data class Result(val item: String): Parcelable
```

### Passing data between different navigators

The `ResultManager` API is public can be used to create our own:

```kotlin
val resultManager = rememberResultManager()
```

Now we can provide this result manager as `CompositionLocal` to children Composables, that can potentially be hosting different navigators.&#x20;

