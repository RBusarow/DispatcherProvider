# Module dispatch-android-espresso

Tools to provide [Espresso] [IdlingResource] functionality for coroutines.

If an [IdlingDispatcherProvider] is registered with the [IdlingRegistry], [Espresso] will wait
for all associated coroutines to leave the *active* state before performing any assertions.

Coroutines which are in a *suspended* state (such as a [Job] "observing" a [Flow] for updates)
do not prevent Espresso from performing assertions.

``` kotlin
class IdlingCoroutineScopeRuleWithLifecycleSample {

  // Retrieve the DispatcherProvider from a dependency graph,
  // so that the same one is used throughout the codebase.
  val customDispatcherProvider = testAppComponent.customDispatcherProvider

  @JvmField
  @Rule
  val idlingRule = IdlingDispatcherProviderRule {
    IdlingDispatcherProvider(customDispatcherProvider)
  }

  /**
  * If you don't provide CoroutineScopes to your lifecycle components via a dependency injection framework,
  * you need to use the `dispatch-android-lifecycle-extensions` and `dispatch-android-viewmodel` artifacts
  * to ensure that the same `IdlingDispatcherProvider` is used.
  */
  @Before
  fun setUp() {
    LifecycleScopeFactory.set {
      MainImmediateCoroutineScope(customDispatcherProvider)
    }
    ViewModelScopeFactory.set {
      MainImmediateCoroutineScope(customDispatcherProvider)
    }
  }

  @Test
  fun testThings() = runBlocking {

    // Now any CoroutineScope which uses the DispatcherProvider
    // in TestAppComponent will sync its "idle" state with Espresso

  }

}
```

## Contents
<!--- TOC -->

* [Types](#types)
* [IdlingCoroutineScopes](#idlingcoroutinescopes)
* [Minimum Gradle Config](#minimum-gradle-config)

<!--- END -->

## Types

| **Name**       | **Description**
| -------------  | --------------- |
| [IdlingDispatcherProviderRule] | JUnit 4 [Rule] which automatically registers an [IdlingDispatcherProvider] with the [IdlingRegistry]
| [IdlingDispatcher] | A [CoroutineDispatcher] which tracks each dispatched coroutine using a [CountingIdlingResource].  All actual dispatches are delegated to a provided [CoroutineDispatcher].
| [IdlingDispatcherProvider] | A special [DispatcherProvider] which guarantees that each of its properties is an [IdlingDispatcher]
| [IdlingCoroutineScope] | A special [CoroutineScope] which guarantees a property of an [IdlingDispatcherProvider]

## IdlingCoroutineScopes

| **Marker Interface**                | **Factory Function**                | **Description**
| -------------------                 | -------------------                 | ---------------
| [DefaultIdlingCoroutineScope]       | [DefaultIdlingCoroutineScope]       | A [IdlingCoroutineScope] with a [CoroutineDispatcher] of `default`.
| [IOIdlingCoroutineScope]            | [IOIdlingCoroutineScope]            | A [IdlingCoroutineScope] with a [CoroutineDispatcher] of `io`.
| [MainIdlingCoroutineScope]          | [MainIdlingCoroutineScope]          | A [IdlingCoroutineScope] with a [CoroutineDispatcher] of `main`.
| [MainImmediateIdlingCoroutineScope] | [MainImmediateIdlingCoroutineScope] | A [IdlingCoroutineScope] with a [CoroutineDispatcher] of `mainImmediate`.
| [UnconfinedIdlingCoroutineScope]    | [UnconfinedIdlingCoroutineScope]    | A [IdlingCoroutineScope] with a [CoroutineDispatcher] of `unconfined`.


## Minimum Gradle Config

Add to your module's `build.gradle.kts`:

``` kotlin
repositories {
  mavenCentral()
}

dependencies {

  // core
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")
  implementation("com.rickbusarow.dispatch:dispatch-core:1.0.0-beta09")

  androidTestImplementation("com.rickbusarow.dispatch:dispatch-android-espresso:1.0.0-beta09")

  // android
  androidTestImplementation("androidx.test:runner:1.3.0")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
```

<!--- MODULE dispatch-core-->
<!--- INDEX  -->
[DispatcherProvider]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-dispatcher-provider/index.html
<!--- MODULE dispatch-android-espresso-->
<!--- INDEX  -->
[IdlingDispatcherProvider]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-idling-dispatcher-provider/index.html
[IdlingDispatcherProviderRule]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-idling-dispatcher-provider-rule/index.html
[IdlingDispatcher]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-idling-dispatcher/index.html
[IdlingCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-idling-coroutine-scope/index.html
[DefaultIdlingCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-default-idling-coroutine-scope/index.html
[IOIdlingCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-i-o-idling-coroutine-scope/index.html
[MainIdlingCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-main-idling-coroutine-scope/index.html
[MainImmediateIdlingCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-main-immediate-idling-coroutine-scope/index.html
[UnconfinedIdlingCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-unconfined-idling-coroutine-scope/index.html
<!--- END -->

[CoroutineDispatcher]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/index.html
[CoroutineScope]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html
[CountingIdlingResource]: https://developer.android.com/reference/androidx/test/espresso/idling/CountingIdlingResource
[Espresso]: https://developer.android.com/training/testing/espresso
[Flow]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/index.html
[IdlingRegistry]: https://developer.android.com/reference/androidx/test/espresso/IdlingRegistry
[IdlingResource]: https://developer.android.com/training/testing/espresso/idling-resource
[Job]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html
[Rule]: https://junit.org/junit4/javadoc/4.12/org/junit/Rule.html
