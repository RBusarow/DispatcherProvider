# Module dispatch-android-viewmodel

The artifact I hope you don't need, but if you're not doing dependency injection, you probably do.
## Contents
<!--- TOC -->

* [Examples](#examples)
* [Difference from AndroidX](#difference-from-androidx)
* [Custom CoroutineScope factories](#custom-coroutinescope-factories)
* [Automatic cancellation in onCleared()](#automatic-cancellation-in-oncleared)
* [viewModelScope is not lifecycleScope](#viewmodelscope-is-not-lifecyclescope)
* [Extending ViewModel](#extending-viewmodel)
* [Minimum Gradle Config](#minimum-gradle-config)

<!--- END -->
## Examples

``` kotlin
import dispatch.android.*

// DispatchViewModel is just a ViewModel with a lazy viewModelScope
class SomeViewModel : DispatchViewModel() {
  // ...

  init {

    // auto-creates a MainImmediateCoroutineScope which is closed in onCleared()
    viewModelScope. //...

    // multiple invocations use the same instance
    viewModelScope.launch {  }

    // it works as a normal CoroutineScope (because it is)
    viewModelScope.launchMain {  }

  }
}

class SomeApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // A custom factory can be set to add elements to the CoroutineContext
    ViewModelScopeFactory.set { MainImmediateCoroutineScope() + SomeCustomElement() }
  }
}

class SomeViewModelTest {

  val viewModel = SomeViewModel()

  @Before
  fun setUp() {
    // This custom factory can be used to use custom scopes for testing
    ViewModelScopeFactory.set { TestProvidedCoroutineScope() }
  }

  @After
  fun tearDown() {
    // The factory can also be reset to default
    ViewModelScopeFactory.reset()
  }

  @Test
  fun someTest() = runBlocking {
    // the AndroidX version is public, so it's public here as well.
    viewModel.viewModelScope.launch {  }
  }
}
```

## Difference from AndroidX

This module is essentially a fork of [androidx-lifecycle-viewmodel-ktx] — the library which gives us the [viewModelScope][androidx-viewModelScope] property.

It exists entirely so that we can have a settable factory.  This gives us a lot more options for JVM or instrumented tests, with custom dispatchers or other custom `CoroutineContext` elements.

## Custom CoroutineScope factories

The way `androidx-lifecycle-viewModel` constructs its [CoroutineScope] is [hard-coded][androidx-lifecycle-viewmodel-ktx],
which eliminates the possibility of using a custom [CoroutineContext]
such as a [DispatcherProvider] or [IdlingDispatcher]. With [dispatch-android-lifecycle], we can set a custom factory.

``` kotlin
class SomeViewModelTest {

  @Before
  fun setUp() {
    // This custom factory can be used to use custom scopes for testing
    ViewModelScopeFactory.set { TestProvidedCoroutineScope() }

    // it could also return a specific instance
    val someTestScope = TestProvidedCoroutineScope()
    ViewModelScopeFactory.set { someTestScope }
  }

  @After
  fun tearDown() {
    // The factory can also be reset to default
    ViewModelScopeFactory.reset()
  }
}
```

## Automatic cancellation in onCleared()

Just like AndroidX, this version of `viewModelScope` is automatically cancelled in `ViewModel.onCleared()`.

## viewModelScope is not lifecycleScope

It's important to remember that `onCleared()` is only called when a `ViewModel` is about to be destroyed -- when its associated `LifecycleOwner`(s) are all destroyed.  This means that a `viewModelScope` is active while the `LifecycleOwner` is in the backstack.

Consider this example:

``` kotlin
// Don't do this
class SomeViewModel : DispatchViewModel() {

  init {
    viewModelScope.launch {
      // this job will continue forever even if the ViewModel is on the backstack.
      someRepository.dataFlow.collect {
        parseData(it)
      }
    }
  }
}
```

A `CoroutineScope` in a `ViewModel` is better utilized for single-shot requests which shouldn't be restarted in the event of a configuration change.  "Observer" behavior should be scoped to the associated view.

``` kotlin
// Maybe do this
class SomeFragment : Fragment() {

  val viewModel: SomeViewModel by viewModels()

  init {
    lifecycleScope.launchWhenResumed {
      viewModel.dataFlow.collect {  }
    }
  }
}

class SomeViewModel : DispatchViewModel() {

  // a single shot request is made using the viewModelScope
  val lazyData by lazy {
    CompletableDeferred<Data>().apply {
      viewModelScope.launch {
        complete(someRepository.getData())
      }
    }
  }

  // collection of the Flow is done using the view's lifecycleScope,
  // meaning that it will stop as soon as the screen is in the backstack
  val dataFlow = someRepository.dataFlow.onEach {
    parseData(it)
  }
}

```

## Extending ViewModel

Since nothing about the `clear` event is actually exposed outside of `ViewModel`, it's necessary to extend `ViewModel` in order to consume it for cancelling the `viewModelScope`.  This is especially galling since `ViewModel` could absolutely have just been an interface to begin with.

## Minimum Gradle Config

Add to your module's `build.gradle.kts`:

``` kotlin
repositories {
  mavenCentral()
}

dependencies {

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")
  implementation("com.rickbusarow.dispatch:dispatch-android-viewmodel:1.0.0-beta09")
}
```

<!--- MODULE dispatch-core-->
<!--- INDEX  -->
[DispatcherProvider]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-dispatcher-provider/index.html
<!--- MODULE dispatch-test-->
<!--- INDEX  -->
<!--- MODULE dispatch-test-junit4-->
<!--- INDEX  -->
<!--- MODULE dispatch-test-junit5-->
<!--- INDEX  -->
<!--- MODULE dispatch-android-espresso-->
<!--- INDEX  -->
[IdlingDispatcher]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-idling-dispatcher/index.html
<!--- MODULE dispatch-android-lifecycle-->
<!--- INDEX  -->
<!--- MODULE dispatch-android-viewmodel-->
<!--- INDEX  -->
<!--- MODULE dispatch-android-viewmodel-->
<!--- INDEX  -->
<!--- END -->


[androidx-lifecycle-viewmodel-ktx]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:lifecycle/lifecycle-viewmodel-ktx/src/main/java/androidx/lifecycle/ViewModel.kt;l=42
[CoroutineContext]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/
[CoroutineScope]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html
[dispatch-android-lifecycle]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/index.html
[androidx-viewModelScope]: https://developer.android.com/topic/libraries/architecture/coroutines#viewmodelscope
