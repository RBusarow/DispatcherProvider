# Module dispatch-android-lifecycle-extensions

## Contents
<!--- TOC -->

* [Api](#api)
  * [One-time suspend functions](#one-time-suspend-functions)
* [Difference from AndroidX](#difference-from-androidx)
* [Custom CoroutineScope factories](#custom-coroutinescope-factories)
* [Automatic lifecycle jobs](#automatic-lifecycle-jobs)
* [Minimum Gradle Config](#minimum-gradle-config)

<!--- END -->

## Api

### One-time suspend functions

Examples

``` kotlin
import dispatch.android.*

// This could be any LifecycleOwner -- Fragments, Activities, Services...
class SomeScreen : Fragment() {

  init {

    // auto-created MainImmediateCoroutineScope which is lifecycle-aware
    dispatchLifecycleScope //...

    // active only when "resumed".  starts a fresh coroutine each time
    // this is a rough proxy for LiveData behavior
    dispatchLifecycleScope.launchEveryResume {  }

    // active only when "started".  starts a fresh coroutine each time
    dispatchLifecycleScope.launchEveryStart {  }

    // launch when created, automatically stop on destroy
    dispatchLifecycleScope.launchEveryCreate {  }

    // it works as a normal CoroutineScope as well (because it is)
    dispatchLifecycleScope.launchMain {  }

  }
}
```

``` kotlin
class SomeApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // A custom factory can be set to add elements to the CoroutineContext
    LifecycleScopeFactory.set { MainImmediateContext() + SomeCustomElement() }
  }
}
```

``` kotlin
class SomeEspressoTest {
  @Before
  fun setUp() {
    // This custom factory can be used to use custom scopes for testing,
    // such as an idling dispatcher
    LifecycleScopeFactory.set { MainImmediateIdlingCoroutineScope().coroutineContext }
  }

  @After
  fun tearDown() {
    // The factory can also be reset to default
    LifecycleScopeFactory.reset()
  }
}
```

## Difference from AndroidX

This module is really just a slightly different version of [androidx-lifecycle-runtime-ktx][androidx-lifecycle-runtime-ktx] — the library which gives us the [lifecycleScope][androidx-lifecycleScope] property.

Why not just use AndroidX?  Because we need two things it doesn't offer.

## Custom CoroutineScope factories

The way `androidx-lifecycle-runtime` constructs its [CoroutineScope] is [hard-coded][androidx-lifecycleScope], which eliminates the possibility of using a custom [CoroutineContext] such as a `DispatcherProvider` or [IdlingDispatcher]. With `dispatch.android.lifecycle`, we can set a custom factory.

``` kotlin
class SomeFragmentEspressoTest {

  // Not part of this artifact.  see dispatch-android-espresso
  @JvmField @Rule val idlingRule = IdlingDispatcherProviderRule()

  @Before
  fun setUp() {
    // set a custom factory which is applied to all newly created lifecycleScopes
    LifecycleScopeFactory.set {
      MainImmediateContext() + idlingRule.dispatcherProvider
    }

    // now SomeFragment will use an IdlingDispatcher in its CoroutineScope
  }
}
```

## Automatic lifecycle jobs

Structured concurrency relies upon cancellation, but [androidx-lifecycle-runtime.ktx][androidx-lifecycle-runtime-ktx] doesn't cancel.  It uses a special [PausingDispatcher][androidx-pausingDispatcher]. This pausing behavior then **leaks** upstream, creating backpressure and potentially deadlocks.

There's a [bug filed in their issue tracker][b/146370660], but 2.2.0 got released anyway.

This library's API surface is the same as that within the AndroidX version, but has a different strategy for handling lifecycle events.  When a lifecycle state enters the desired range, such as at `ON_RESUME`, a new coroutine is created.  When the state exists the range, that coroutine is *cancelled*. If the lifecycle state enters the desired range again, a **new** coroutine is created.

``` kotlin
import dispatch.android.*

class SomeFragment : Fragment() {

  val viewModel: SomeViewModel by viewModels()

  init {
    // automatically created CoroutineScope using the factory described above
    dispatchLifecycleScope.launchWhenResumed {
      viewModel.someFlow.consume {  }
    }
  }

}
```

This has the desired effect of not leaking backpressure upstream (which in this example is the `viewModel`).

## Minimum Gradle Config

Add to your module's `build.gradle.kts`:

``` kotlin
repositories {
  mavenCentral()
}

dependencies {

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")
  implementation("com.rickbusarow.dispatch:dispatch-android-lifecycle-extensions:1.0.0-beta09")

  implementation("androidx.lifecycle:lifecycle-common:2.3.1")
}
```


<!--- MODULE dispatch-core-->
<!--- INDEX  -->
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

[androidx-lifecycle-runtime-ktx]: https://developer.android.com/jetpack/androidx/releases/lifecycle
[b/146370660]: https://issuetracker.google.com/issues/146370660
[CoroutineContext]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/
[CoroutineScope]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html

[androidx-lifecycleScope]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:lifecycle/lifecycle-runtime-ktx/src/main/java/androidx/lifecycle/Lifecycle.kt;l=44

[androidx-pausingDispatcher]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:lifecycle/lifecycle-runtime-ktx/src/main/java/androidx/lifecycle/PausingDispatcher.kt

