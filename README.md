[<img src="https://img.shields.io/maven-central/v/com.rickbusarow.dispatch/dispatch-core.svg?label=Maven%20Central"/>](https://search.maven.org/search?q=com.rickbusarow.dispatch)
![CI](https://github.com/RBusarow/Dispatch/workflows/CI/badge.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Dispatch

Utilities for [kotlinx.coroutines] which make them type-safe, easier to test, and more expressive.
Use the predefined [types and factories](#types-and-factories) or define your own, and never inject
a `Dispatchers` object again.

``` kotlin
val presenter = MyPresenter(MainCoroutineScope())

class MyPresenter @Inject constructor(
  /**
  * Defaults to the Main dispatcher
  */
  val coroutineScope: MainCoroutineScope
) {

  fun loopSomething() = coroutineScope.launchDefault {  }

  suspend fun updateSomething() = withMainImmediate {  }
}
```

``` kotlin
class MyTest {

  @Test
  fun `no setting the main dispatcher`() = runBlockingProvidedTest {

    // automatically use TestCoroutineDispatcher for every dispatcher type
    val presenter = MyPresenter(coroutineScope = this)

    // this call would normally crash due to the main looper
    presenter.updateSomething()
  }

}
```

## Contents
<!--- TOC -->

* [Injecting dispatchers](#injecting-dispatchers)
* [Types and Factories](#types-and-factories)
* [Referencing dispatchers](#referencing-dispatchers)
  * [Builder Extensions](#builder-extensions)
* [Android Lifecycle](#android-lifecycle)
* [Android Espresso](#android-espresso)
* [Android ViewModel](#android-viewmodel)
* [Testing](#testing)
* [Modules](#modules)
* [Full Gradle Config](#full-gradle-config)
* [License](#license)

<!--- END -->


## Injecting dispatchers

Everywhere you use coroutines, you use a [CoroutineContext]. If we embed the
[CoroutineDispatchers][CoroutineDispatcher] settings we want into the context, then we don't need to
pass them around manually.

The core of this library is [DispatcherProvider] - an interface with properties corresponding to the
5 different [CoroutineDispatchers][CoroutineDispatcher] we can get from the [Dispatchers] singleton.
It lives inside the [CoroutineContext], and gets passed from parent to child coroutines
transparently without any additional code.

``` kotlin
interface DispatcherProvider : CoroutineContext.Element {

  override val key: CoroutineContext.Key<*> get() = Key

  val default: CoroutineDispatcher
  val io: CoroutineDispatcher
  val main: CoroutineDispatcher
  val mainImmediate: CoroutineDispatcher
  val unconfined: CoroutineDispatcher

  companion object Key : CoroutineContext.Key<DispatcherProvider>
}

val someCoroutineScope = CoroutineScope(
  Job() + Dispatchers.Main + DispatcherProvider()
)
```

The default implementation of this interface simply delegates to that [Dispatchers] singleton,
as that is what we typically want for production usage.

## Types and Factories

A [CoroutineScope] may have any type of [CoroutineDispatcher].  What if we have a View class which
will always use the [Main][Dispatchers.Main] thread, or one which will always do I/O?

There are marker interfaces and factories to ensure that the correct type of [CoroutineScope] is always used.

| **Type**                         | **Dispatcher**       |
| -------------------------------- | -------------------- |
| [DefaultCoroutineScope]          | [Dispatchers.Default]
| [IOCoroutineScope]               | [Dispatchers.IO]
| [MainCoroutineScope]             | [Dispatchers.Main]
| [MainImmediateCoroutineScope]    | [Dispatchers.Main.immediate]
| [UnconfinedCoroutineScope]       | [Dispatchers.Unconfined]

``` kotlin
val mainScope = MainCoroutineScope()

val someUIClass = SomeUIClass(mainScope)

class SomeUIClass(val coroutineScope: MainCoroutineScope) {

  fun foo() = coroutineScope.launch {
    // because of the dependency type,
    // we're guaranteed to be on the main dispatcher even though we didn't specify it
  }

}
```

## Referencing dispatchers

These [dispatcher][CoroutineDispatcher] settings can then be accessed via extension functions upon
[CoroutineScope], or the [coroutineContext][kotlin.coroutineContext], or directly from extension
functions:

### Builder Extensions

|              | **Default**     | **IO**     | **Main**     | **Main.immediate**    | **Unconfined**     |
| ------------ | --------------- | ---------- | ------------ | --------------------- | ------------------ |
| [Job]        | [launchDefault] | [launchIO] | [launchMain] | [launchMainImmediate] | [launchUnconfined]
| [Deferred]   | [asyncDefault]  | [asyncIO]  | [asyncMain]  | [asyncMainImmediate]  | [asyncUnconfined]
| `suspend T`  | [withDefault]   | [withIO]   | [withMain]   | [withMainImmediate]   | [withUnconfined]
| `Flow<T>`    | [flowOnDefault] | [flowOnIO] | [flowOnMain] | [flowOnMainImmediate] | [flowOnUnconfined]

``` kotlin
class MyClass(val coroutineScope: IOCoroutineScope) {

  fun accessMainThread() = coroutineScope.launchMain {
    // we're now on the "main" thread as defined by the interface
  }

}
```

## Android Lifecycle

The [AndroidX.lifecycle][androidx-lifecycle-runtime-ktx] library offers a
[lifecycleScope][androidx-lifecycleScope] extension function to provide a lifecycle-aware
[CoroutineScope], but there are two shortcomings:
1. It delegates to a hard-coded `Dispatchers.Main` [CoroutineDispatcher], which complicates unit and
   [Espresso] testing by requiring the use of [Dispatchers.setMain].
2. It *pauses* the dispatcher when the lifecycle state passes below its threshold, which [leaks
   backpressure to the producing coroutine and can create deadlocks][b/146370660].

[Dispatch-android-lifecycle] and [dispatch-android-lifecycle-extensions] completely replace the AndroidX version.

``` kotlin
import dispatch.android.lifecycle.*
import dispatch.core.*
import kotlinx.coroutines.flow.*

class MyActivity : Activity() {

  init {
    dispatchLifecycleScope.launchOnCreate {
          viewModel.someFlow.collect {
            channel.send("$it")
          }
        }
  }
}
```

The [DispatchLifecycleScope][DispatchLifecycleScope-class] may be configured with any dispatcher, since
[MainImmediateCoroutineScope] is just a marker interface. Its lifecycle-aware functions *cancel*
when dropping below a threshold, then automatically restart when entering into the desired lifecycle
state again. This is key to preventing the backpressure leak of the AndroidX version, and it's also
more analogous to the behavior of [LiveData] to which many developers are accustomed.

There are two built-in ways to define a custom LifecycleCoroutineScope - by simply constructing one
directly inside a Lifecycle class, or by statically setting a custom [LifecycleScopeFactory]. This
second option can be very useful when utilizing an [IdlingCoroutineScope].

## Android Espresso

[Espresso] is able to use [IdlingResource] to infer when it should perform its actions, which helps
to reduce the flakiness of tests. Conventional thread-based `IdlingResource` implementations don't
work with coroutines, however.

[IdlingCoroutineScope] utilizes [IdlingDispatchers][IdlingDispatcher], which count a coroutine as
being "idle" when it is suspended. Using statically defined factories, service locators, or
dependency injection, it is possible to utilize idling-aware dispatchers throughout a codebase
during Espresso testing.

``` kotlin
class IdlingCoroutineScopeRuleWithLifecycleSample {


  val customDispatcherProvider = IdlingDispatcherProvider()

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

## Android ViewModel

The [AndroidX ViewModel][androidx-lifecycle-viewmodel-ktx] library offers a
[viewModelScope][androidx-viewModelScope] extension function to provide an auto-cancelled
[CoroutineScope], but again, this `CoroutineScope` is hard-coded and uses `Dispatchers.Main`. This
limitation needn't exist.

[Dispatch-android-viewmodel] doesn't have as many options as its lifecycle counterpart, because the
[ViewModel.onCleared] function is `protected` and [ViewModel] does not expose anything about its
lifecycle. The only way for a third party library to achieve a lifecycle-aware `CoroutineScope` is
through inheritance.

[CoroutineViewModel] is a simple abstract class which exposes a lazy [viewModelScope] property which
is automatically cancelled when the `ViewModel` is destroyed. The exact type of the `viewModelScope`
can be configured statically via [ViewModelScopeFactory]. In this way, you can use
[IdlingCoroutineScopes][IdlingCoroutineScope] for Espresso testing,
[TestProvidedCoroutineScopes][TestProvidedCoroutineScope] for unit testing, or any other custom
scope you'd like.

If you're using the AAC `ViewModel` but not dependency injection, this artifact should be very
helpful with testing.

``` kotlin
import dispatch.android.viewmodel.*
import kotlinx.coroutines.flow.*
import timber.log.*

class MyViewModel : CoroutineViewModel() {

  init {
    MyRepository.someFlow.onEach {
      Timber.d("$it")
    }.launchIn(viewModelScope)
  }
}
```

The [DispatchLifecycleScope][DispatchLifecycleScope-class] may be configured with any dispatcher, since
[MainImmediateCoroutineScope] is just a marker interface. Its lifecycle-aware functions *cancel*
when dropping below a threshold, then automatically restart when entering into the desired lifecycle
state again. This is key to preventing the backpressure leak of the AndroidX version, and it's also
more analogous to the behavior of [LiveData] to which many developers are accustomed.

There are two built-in ways to define a custom LifecycleCoroutineScope - by simply constructing one
directly inside a Lifecycle class, or by statically setting a custom [LifecycleScopeFactory]. This
second option can be very useful when utilizing an [IdlingCoroutineScope].

## Testing

Testing is why this library exists. [TestCoroutineScope] and [TestCoroutineDispatcher] are very
powerful when they can be used, but any reference to a statically defined dispatcher (like a
[Dispatchers] property) removes that control.

To that end, there's a configurable [TestDispatcherProvider]:

``` kotlin
class TestDispatcherProvider(
  override val default: CoroutineDispatcher = TestCoroutineDispatcher(),
  override val io: CoroutineDispatcher = TestCoroutineDispatcher(),
  override val main: CoroutineDispatcher = TestCoroutineDispatcher(),
  override val mainImmediate: CoroutineDispatcher = TestCoroutineDispatcher(),
  override val unconfined: CoroutineDispatcher = TestCoroutineDispatcher()
) : DispatcherProvider
```

As well as a polymorphic [TestProvidedCoroutineScope] which may be used in place of any type-specific [CoroutineScope]:

``` kotlin
val testScope = TestProvidedCoroutineScope()

val someUIClass = SomeUIClass(testScope)

class SomeUIClass(val coroutineScope: MainCoroutineScope) {

  fun foo() = coroutineScope.launch {
    // ...
  }

}
```

There's also [testProvided], which delegates to [runBlockingTest][kotlinx.runBlockingTest] but which
includes a [TestDispatcherProvider] inside the [TestCoroutineScope].

``` kotlin
class Subject {
  // this would normally be a hard-coded reference to Dispatchers.Main
  suspend fun sayHello() = withMain {  }
}

@Test
fun `sayHello should say hello`() = runBlockingProvided {

  val subject = SomeClass(this)
  // uses "main" TestCoroutineDispatcher safely with no additional setup
  subject.getSomeData() shouldPrint "hello"
}
```

## Modules

| **artifact**                            | **features**                                   |
| --------------------------------------  | ---------------------------------------------- |
| [dispatch-android-espresso]             | [IdlingDispatcher] <p> [IdlingDispatcherProvider]
| [dispatch-android-lifecycle-extensions] | [dispatchLifecycleScope][dispatchLifecycleScope-extension]
| [dispatch-android-lifecycle]            | [DispatchLifecycleScope][DispatchLifecycleScope-class] <p> [launchOnCreate] <p> [launchOnStart] <p> [launchOnResume] <p> [onNextCreate] <p> [onNextStart] <p> [onNextResume]
| [dispatch-android-viewmodel]            | [CoroutineViewModel] <p> [viewModelScope]
| [dispatch-core]                         | Dispatcher-specific types and factories <p> Dispatcher-specific coroutine builders
| [dispatch-detekt]                       | [Detekt] rules for common auto-imported-the-wrong-thing problems
| [dispatch-test-junit4]                  | [TestCoroutineRule]
| [dispatch-test-junit5]                  | [CoroutineTest] <p> [CoroutineTestExtension]
| [dispatch-test]                         | [TestProvidedCoroutineScope] <p> [TestDispatcherProvider] <p> [runBlockingProvided] and [testProvided]

## Full Gradle Config

``` kotlin
repositories {
  mavenCentral()
}

dependencies {

  /*
  production code
  */

  // core coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")

  // everything provides :core via "api", so you only need this if you have no other "implementation" dispatch artifacts
  implementation("com.rickbusarow.dispatch:dispatch-core:1.0.0-beta09")
  // LifecycleCoroutineScope for Android Fragments, Activities, etc.
  implementation("com.rickbusarow.dispatch:dispatch-android-lifecycle:1.0.0-beta09")
  // lifecycleScope extension function with a settable factory.  Use this if you don't DI your CoroutineScopes
  // This provides :dispatch-android-lifecycle via "api", so you don't need to declare both
  implementation("com.rickbusarow.dispatch:dispatch-android-lifecycle-extensions:1.0.0-beta09")
  // ViewModelScope for Android ViewModels
  implementation("com.rickbusarow.dispatch:dispatch-android-viewmodel:1.0.0-beta09")

  /*
  jvm testing
  */

  // core coroutines-test
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")
  // you only need this if you don't have the -junit4 or -junit5 artifacts
  testImplementation("com.rickbusarow.dispatch:dispatch-test:1.0.0-beta09")
  // CoroutineTestRule and :dispatch-test
  // This provides :dispatch-test via "api", so you don't need to declare both
  // This can be used at the same time as :dispatch-test-junit5
  testImplementation("com.rickbusarow.dispatch:dispatch-test-junit4:1.0.0-beta09")
  // CoroutineTest, CoroutineTestExtension, and :dispatch-test
  // This provides :dispatch-test via "api", so you don't need to declare both
  // This can be used at the same time as :dispatch-test-junit4
  testImplementation("com.rickbusarow.dispatch:dispatch-test-junit5:1.0.0-beta09")
  /*
  Android testing
  */

  // core android
  androidTestImplementation("androidx.test:runner:1.3.0")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
  // IdlingDispatcher, IdlingDispatcherProvider, and IdlingCoroutineScope
  androidTestImplementation("com.rickbusarow.dispatch:dispatch-android-espresso:1.0.0-beta09")
}
```

## License

``` text
Copyright (C) 2021 Rick Busarow
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

<!--- MODULE dispatch-core-->
<!--- INDEX  -->

[DispatcherProvider]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-dispatcher-provider/index.html
[DefaultCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-default-coroutine-scope/index.html
[IOCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-i-o-coroutine-scope/index.html
[MainCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-main-coroutine-scope/index.html
[MainImmediateCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-main-immediate-coroutine-scope/index.html
[UnconfinedCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/-unconfined-coroutine-scope/index.html
[launchDefault]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/launch-default.html
[launchIO]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/launch-i-o.html
[launchMain]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/launch-main.html
[launchMainImmediate]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/launch-main-immediate.html
[launchUnconfined]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/launch-unconfined.html
[asyncDefault]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/async-default.html
[asyncIO]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/async-i-o.html
[asyncMain]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/async-main.html
[asyncMainImmediate]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/async-main-immediate.html
[asyncUnconfined]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/async-unconfined.html
[withDefault]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/with-default.html
[withIO]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/with-i-o.html
[withMain]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/with-main.html
[withMainImmediate]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/with-main-immediate.html
[withUnconfined]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/with-unconfined.html
[flowOnDefault]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/flow-on-default.html
[flowOnIO]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/flow-on-i-o.html
[flowOnMain]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/flow-on-main.html
[flowOnMainImmediate]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/flow-on-main-immediate.html
[flowOnUnconfined]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/flow-on-unconfined.html

<!--- MODULE dispatch-test-->
<!--- INDEX  -->

[TestProvidedCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/-test-provided-coroutine-scope/index.html
[TestDispatcherProvider]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/-test-dispatcher-provider/index.html
[testProvided]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/test-provided.html
[runBlockingProvided]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/run-blocking-provided.html

<!--- MODULE dispatch-test-junit4-->
<!--- INDEX  -->

[TestCoroutineRule]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit4/dispatch.test/-test-coroutine-rule/index.html

<!--- MODULE dispatch-test-junit5-->
<!--- INDEX  -->

[CoroutineTest]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit5/dispatch.test/-coroutine-test/index.html
[CoroutineTestExtension]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit5/dispatch.test/-coroutine-test-extension/index.html

<!--- MODULE dispatch-android-espresso-->
<!--- INDEX  -->

[IdlingCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-idling-coroutine-scope/index.html
[IdlingDispatcher]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-idling-dispatcher/index.html
[IdlingDispatcherProvider]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/-idling-dispatcher-provider/index.html

<!--- MODULE dispatch-android-lifecycle-->
<!--- INDEX  -->

[launchOnCreate]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/-dispatch-lifecycle-scope/launch-on-create.html
[launchOnStart]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/-dispatch-lifecycle-scope/launch-on-start.html
[launchOnResume]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/-dispatch-lifecycle-scope/launch-on-resume.html
[onNextCreate]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/on-next-create.html
[onNextStart]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/on-next-start.html
[onNextResume]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/on-next-resume.html

<!--- MODULE dispatch-android-lifecycle-extensions-->
<!--- INDEX  -->

[LifecycleScopeFactory]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle-extensions/dispatch.android.lifecycle/-lifecycle-scope-factory/index.html

<!--- MODULE dispatch-android-viewmodel-->
<!--- INDEX  -->

[CoroutineViewModel]: https://rbusarow.github.io/Dispatch/api/dispatch-android-viewmodel/dispatch.android.viewmodel/index.html#dispatch.android.viewmodel/CoroutineViewModel//PointingToDeclaration/
[viewModelScope]: https://rbusarow.github.io/Dispatch/api/dispatch-android-viewmodel/dispatch.android.viewmodel/-dispatch-view-model/index.html#dispatch.android.viewmodel/DispatchViewModel/viewModelScope/#/PointingToDeclaration/
[ViewModelScopeFactory]: https://rbusarow.github.io/Dispatch/api/dispatch-android-viewmodel/dispatch.android.viewmodel/-view-model-scope-factory/index.html

<!--- END -->

[androidx-lifecycle-runtime-ktx]: https://developer.android.com/jetpack/androidx/releases/lifecycle
[androidx-lifecycle-viewmodel-ktx]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:lifecycle/lifecycle-viewmodel-ktx/src/main/java/androidx/lifecycle/ViewModel.kt;l=42
[androidx-lifecycleScope]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:lifecycle/lifecycle-runtime-ktx/src/main/java/androidx/lifecycle/Lifecycle.kt;l=44
[androidx-viewModelScope]: https://developer.android.com/topic/libraries/architecture/coroutines#viewmodelscope
[b/146370660]: https://issuetracker.google.com/issues/146370660
[CoroutineContext]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/
[CoroutineDispatcher]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/index.html
[CoroutineScope]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html
[Deferred]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/index.html
[Detekt]: https://github.com/detekt/detekt
[dispatch-android-espresso]: https://rbusarow.github.io/Dispatch/api/dispatch-android-espresso/dispatch.android.espresso/index.html
[dispatch-android-lifecycle-extensions]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle-extensions/dispatch.android.lifecycle/index.html
[dispatch-android-lifecycle]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/index.html
[dispatch-android-viewmodel]: https://rbusarow.github.io/Dispatch/api/dispatch-android-viewmodel/dispatch.android.viewmodel/index.html
[dispatch-core]: https://rbusarow.github.io/Dispatch/api/dispatch-core/dispatch.core/index.html
[dispatch-detekt]: https://rbusarow.github.io/Dispatch/api/dispatch-detekt/dispatch.detekt/index.html
[dispatch-test-junit4]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit4/dispatch.test/index.html
[dispatch-test-junit5]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit5/dispatch.test/index.html
[dispatch-test]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/index.html
[Dispatchers.Default]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-default.html
[Dispatchers.IO]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-io.html
[Dispatchers.Main.immediate]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-main-coroutine-dispatcher/immediate.html
[Dispatchers.Main]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-main.html
[Dispatchers.setMain]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/kotlinx.coroutines.-dispatchers/set-main.html
[Dispatchers.Unconfined]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-unconfined.html
[Dispatchers]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/index.html
[Espresso]: https://developer.android.com/training/testing/espresso
[IdlingResource]: https://developer.android.com/training/testing/espresso/idling-resource
[Job]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html
[kotlin.coroutineContext]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/coroutine-context.html
[kotlinx.coroutines]: https://kotlin.github.io/kotlinx.coroutines/
[kotlinx.runBlockingTest]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/run-blocking-test.html
[LiveData]: https://developer.android.com/reference/androidx/lifecycle/LiveData
[TestCoroutineDispatcher]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-coroutine-dispatcher/index.html
[TestCoroutineScope]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-coroutine-scope/index.html
[ViewModel.onCleared]: https://developer.android.com/reference/androidx/lifecycle/ViewModel#onCleared()
[ViewModel]: https://developer.android.com/reference/androidx/lifecycle/ViewModel


[DispatchLifecycleScope-class]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle/dispatch.android.lifecycle/-dispatch-lifecycle-scope/index.html
[dispatchLifecycleScope-extension]: https://rbusarow.github.io/Dispatch/api/dispatch-android-lifecycle-extensions/dispatch.android.lifecycle/index.html#dispatch.android.lifecycle/dispatchLifecycleScope/androidx.lifecycle.LifecycleOwner#/PointingToDeclaration/
