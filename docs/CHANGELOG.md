# Change log for Dispatch

## Version 1.0.0-beta09

* [CoroutineTestExtension] now creates a new [scope][CoroutineTestExtension.scope] instance for each test.
* Update Kotlin to [1.5.0](https://github.com/JetBrains/kotlin/releases/tag/v1.5.0)
* Update coroutines to [1.4.3](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.4.3)
* Update Android Gradle Plugin to [4.2.0](https://developer.android.com/studio/releases/gradle-plugin#4-2-0)
* Update Androidx Lifecycle to [2.3.1](https://developer.android.com/jetpack/androidx/releases/lifecycle#version_231_2)

## Version 1.0.0-beta08

* Remove FlowCancellationException ([#196](https://github.com/RBusarow/Dispatch/issues/196))
* Update Kotlin to [1.4.21](https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md#1421)

## Version 1.0.0-beta07

* Update to coroutines to [1.4.2](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.4.2)
* Update to Kotlin to [1.4.20](https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md#1420)

## Version 1.0.0-beta06

* Update to coroutines to [1.4.1](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.4.1) in order to fix [an issue with SharedFlow](https://github.com/Kotlin/kotlinx.coroutines/pull/2359)([#189](https://github.com/RBusarow/Dispatch/issues/189))

## Version 1.0.0-beta05

### Api changes
* [ViewLifecycleCoroutineScope] is a special [DispatchLifecycleScope] meant for binding a scope to a [Fragment]'s **view** lifecycle. ([#179](https://github.com/RBusarow/Dispatch/issues/179))
* Make [testProvided] receive [TestProvidedCoroutineScope] ([#157](https://github.com/RBusarow/Dispatch/issues/157))
* [LifecycleCoroutineScope] has been renamed to [DispatchLifecycleScope], along with its extension function. The old names are still functional with a deprecated typealias.  They will be removed in the future. ([#186](https://github.com/RBusarow/Dispatch/pull/186))
* [CoroutineViewModel] has been renamed to [DispatchViewModel]. The old names are still functional with a deprecated typealias.  They will be removed in the future. ([#186](https://github.com/RBusarow/Dispatch/pull/186))

### Bug fixes
* Use [currentCoroutineContext] to resolve the inner [CoroutineContext] in `flowOn___` functions. ([#181](https://github.com/RBusarow/Dispatch/issues/181))

### Housekeeping
* Set the project JDK target to 8. This only affects the build environment since all previous builds were done on a JDK 8 machine. ([#187](https://github.com/RBusarow/Dispatch/pull/187))
* Coroutines has been updated to 1.4.0 ([#183](https://github.com/RBusarow/Dispatch/pull/183))
* Detekt has been updated to 1.4.2 ([#184](https://github.com/RBusarow/Dispatch/pull/184))
* JUnit5 has been updated to 5.7.0 ([#178](https://github.com/RBusarow/Dispatch/pull/178))

## Version 1.0.0-beta04

### Features
* [DefaultDispatcherProvider] is now a mutable singleton which allows for a custom global default.

### Bug fixes

#### dispatch-test-junit5
* [CoroutineTestExtension] will now properly call `Dispatchers.setMain(...)` when injecting a
  CoroutineScope into a function or when not injecting at all.
  ([#130](https://github.com/RBusarow/Dispatch/issues/130))

#### dispatch-android-lifecycle
* [LifecycleCoroutineScope] will now be automatically cancelled when the associated [Lifecycle][Android Lifecycle] drops to the [Destroyed][Android Lifecycle] state.
  ([#135](https://github.com/RBusarow/Dispatch/issues/135))

#### dispatch-android-lifecycle-extensions
* Cached [LifecycleCoroutineScopes][LifecycleCoroutineScope] will now be removed from the cache when
  they are destroyed. ([#136](https://github.com/RBusarow/Dispatch/issues/136))
* Fixed a race condition where multiple [LifecycleCoroutineScopes][LifecycleCoroutineScope] may be
  created for concurrent cache misses. ([#136](https://github.com/RBusarow/Dispatch/issues/136))

### Deprecations
* The [DefaultDispatcherProvider] class constructor has been changed to an object factory function
  (`operator fun invoke(): DispatcherProvider`) and deprecated. This function will be removed prior
  to the 1.0 release.

### Breaking changes
* [DefaultDispatcherProvider] has been changed from a `class` to an `object`, and its functionality
  changed. It is now a singleton holder for a default `DispatcherProvider` instance. To create a
  default `DispatcherProvider`, use the interface's companion object factory function
  (`DispatcherProvider()`).

## Version 1.0.0-beta03

### Renames

* The project has been renamed from DispatcherProvider to Dispatch.
* Maven coordinates for existing modules have changed.
* The base Maven coordinate has changed from `com.rickbusarow.DispatcherProvider` to `com.rickbusarow.dispatch`.
* Base package names have been updated.

### New artifacts

* [dispatch-android-espresso] adds support for [IdlingResource] dispatchers in Espresso testing.
* [dispatch-android-lifecycle] adds an alternative to the Androidx [LifecycleScope][androidx-lifecycleScope] functionality, with lots of configuration options.
  * [dispatch-android-lifecycle-extensions] adds the [lifecycleScope] extension property, but with a configurable factory (useful for Espresso testing).
* [dispatch-android-viewmodel] adds an alternative to Androidx [viewModelScope] with configuration options.
* [dispatch-detekt] adds [Detekt] rules to warn against accidentally using a hard-coded CoroutineDispatcher.
* [dispatch-test-junit4] adds a JUnit4 Rule for automatically providing and cleaning up a [TestProvidedCoroutineScope].
* [dispatch-test-junit5] adds JUnit5 Extension support for automatically providing and cleaning up a [TestProvidedCoroutineScope].

## Version 1.0.0-beta02

### Test features

* Added `TestBasicDispatcherProvider` factory which uses `CommonPool` for `default` and `io`, but a shared single-threaded `ExecutorCoroutineDispatcher` for `main` and `mainImmediate` to provide "natural" dispatch behavior in tests without `Dispatchers.setMain(...)`.

### Bug fixes and improvements

* `runBlockingTestProvided` now uses the same `TestCoroutineDispatcher` as its `ContinuationInterceptor` and in its `TestDispatcherProvider` (#15).
* `runBlockingProvided` now uses `TestBasicDispatcherProvider` as its `DispatcherProvider`.

## Version 1.0.0-beta01

### Flow

* Add non-suspending `flowOn___()` operators for the `Flow` api.

### Misc

* Lots of Kdocs.
* Maven artifacts.
* Lower JDK version to 1.6

<!--- MODULE dispatch-core-->
<!--- INDEX -->

[DefaultDispatcherProvider]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-default-dispatcher-provider/index.html

<!--- MODULE dispatch-test-->
<!--- INDEX  -->

[testProvided]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/test-provided.html
[TestProvidedCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/-test-provided-coroutine-scope/index.html

<!--- MODULE dispatch-test-junit4-->
<!--- INDEX  -->
<!--- MODULE dispatch-test-junit5-->
<!--- INDEX  -->

[CoroutineTestExtension]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit5/dispatch.test/-coroutine-test-extension/index.html
[CoroutineTestExtension.scope]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit5/dispatch.test/-coroutine-test-extension/index.html#dispatch.test/CoroutineTestExtension/scope/#/PointingToDeclaration/

<!--- MODULE dispatch-android-espresso-->
<!--- INDEX  -->
<!--- MODULE dispatch-android-lifecycle-->
<!--- INDEX  -->

[ViewLifecycleCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/-view-lifecycle-coroutine-scope/index.html
[DispatchLifecycleScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/-dispatch-lifecycle-scope/index.html
[LifecycleCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/index.html#dispatch.android.lifecycle/LifecycleCoroutineScope//PointingToDeclaration/

<!--- MODULE dispatch-android-lifecycle-extensions-->
<!--- INDEX  -->

[lifecycleScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle-extensions/dispatch.android.lifecycle/index.html#dispatch.android.lifecycle/lifecycleScope/androidx.lifecycle.LifecycleOwner#/PointingToDeclaration/

<!--- MODULE dispatch-android-viewmodel-->
<!--- INDEX  -->

[CoroutineViewModel]: https://rbusarow.github.io/Dispatch/api/dispatch-android-viewmodel/dispatch.android.viewmodel/index.html#dispatch.android.viewmodel/CoroutineViewModel//PointingToDeclaration/
[DispatchViewModel]: https://rbusarow.github.io/Dispatch/api/dispatch-android-viewmodel/dispatch.android.viewmodel/-dispatch-view-model/index.html
[viewModelScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-viewmodel/dispatch.android.viewmodel/-dispatch-view-model/index.html#dispatch.android.viewmodel/DispatchViewModel/viewModelScope/#/PointingToDeclaration/

<!--- END -->

[Fragment]: https://developer.android.com/reference/androidx/fragment/app/Fragment
[Android Lifecycle]: https://developer.android.com/reference/androidx/lifecycle/Lifecycle.html
[androidx-lifecycleScope]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:lifecycle/lifecycle-runtime-ktx/src/main/java/androidx/lifecycle/Lifecycle.kt;l=44
[Detekt]: https://github.com/detekt/detekt
[dispatch-android-espresso]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/index.html
[dispatch-android-lifecycle-extensions]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle-extensions/dispatch.android.lifecycle/index.html
[dispatch-android-lifecycle]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/index.html
[dispatch-android-viewmodel]: https://rbusarow.github.io/Dispatch/api/dispatch-android-viewmodel/dispatch.android.viewmodel/index.html
[dispatch-detekt]: https://rbusarow.github.io/Dispatch/api/dispatch-detekt/dispatch.detekt/index.html
[dispatch-test]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/index.html
[dispatch-test-junit4]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit4/dispatch.test/index.html
[dispatch-test-junit5]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit4/dispatch.test/index.html
[IdlingResource]: https://developer.android.com/training/testing/espresso/idling-resource

[CoroutineContext]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/
[currentCoroutineContext]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/current-coroutine-context
