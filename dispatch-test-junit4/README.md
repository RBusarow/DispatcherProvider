# Module dispatch-test-junit4

## Contents
<!--- TOC -->

* [Features](#features)
* [Setting Dispatchers.Main](#setting-dispatchersmain)
* [This module replaces dispatch-test](#this-module-replaces-dispatch-test)
* [JUnit dependencies](#junit-dependencies)
  * [Minimum Gradle Config](#minimum-gradle-config)
  * [JUnit 5 interoperability](#junit-5-interoperability)

<!--- END -->

## Features

In addition to all the functionality in [dispatch-test], this module exposes a [TestCoroutineRule] to handle set-up and tear-down of a [TestProvidedCoroutineScope].

Since [TestProvidedCoroutineScope] is a [TestCoroutineScope], this Rule also invokes [cleanupTestCoroutines][cleanupTestCoroutines] after the test.

``` kotlin
class SomeClassTest {

  @JvmField
  @Rule
  val rule = TestCoroutineRule()

  @Test
  fun `some test`() = runBlocking {

    val subject = SomeClass(rule)

    val job = subject.fireAndForget()

    // TODO: assertions go here
  }

}

class SomeClass(val coroutineScope: CoroutineScope) {
  fun fireAndForget() = launch { }
}
```

## Setting Dispatchers.Main

Even though `dispatch-core` eliminates the need to use `Dispatchers.Main` in internal code, it’s still possible that code which has yet to be migrated, or a third-party library is making use of the hard-coded dispatcher.  Because of this, the rule still calls `Dispatchers.setMain(...)` in its setup and `Dispatchers.resetMain()` afterwards.

## This module replaces dispatch-test

If using this module, there is no need to include the `dispatch-test` artifact in your dependencies.

## JUnit dependencies

### Minimum Gradle Config

Because this is a JUnit 4 Rule, it requires a variant of that artifact.  No external libraries are bundled as part of Dispatch, so you’ll need to add it to your `dependencies` block yourself.  The two official options would be:

- classic JUnit 4
  - `org.junit.jupiter:junit-jupiter:4.13`
- JUnit 5 "vintage"
  - `org.junit.vintage:junit-vintage-engine:5.5.1`

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

  // the junit4 artifact also provides the dispatch-test artifact
  testImplementation("com.rickbusarow.dispatch:dispatch-test-junit4:1.0.0-beta09")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")
}
```

### JUnit 5 interoperability

Junit 5 provides a “vintage” (JUnit 4) artifact for legacy support (such as Robolectric or Android instrumented tests).  Dispatch also supports running both in the same project by just adding both artifacts.

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

  // the junit4 and junit5 artifacts also provides the dispatch-test artifact
  testImplementation("com.rickbusarow.dispatch:dispatch-test-junit4:1.0.0-beta09")
  testImplementation("com.rickbusarow.dispatch:dispatch-test-junit5:1.0.0-beta09")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")
}
```

<!--- MODULE dispatch-test-->
<!--- INDEX  -->
[TestProvidedCoroutineScope]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/-test-provided-coroutine-scope/index.html
[cleanupTestCoroutines]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/-test-provided-coroutine-scope/index.html#kotlinx.coroutines.test/TestCoroutineScope/cleanupTestCoroutines/#/PointingToDeclaration/
<!--- MODULE dispatch-test-junit4-->
<!--- INDEX  -->
[TestCoroutineRule]: https://rbusarow.github.io/Dispatch/api/dispatch-test-junit4/dispatch.test/-test-coroutine-rule/index.html
<!--- END -->

[dispatch-test]: https://rbusarow.github.io/Dispatch/api/dispatch-test/dispatch.test/index.html
[TestCoroutineScope]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-coroutine-scope/index.html

