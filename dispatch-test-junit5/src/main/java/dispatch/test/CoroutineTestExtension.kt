/*
 * Copyright (C) 2021 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dispatch.test

import dispatch.test.CoroutineTestExtension.*
import dispatch.test.internal.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.api.extension.support.*
import kotlin.coroutines.*

/**
 * JUnit 5 [ParameterResolver] [extension][Extension] for injecting and managing a [TestProvidedCoroutineScope] in a test instance.
 * This creates a new instance of [TestProvidedCoroutineScope] each time the scope is injected, optionally using a custom [ScopeFactory].
 *
 * If this extension is initialized via [RegisterExtension], there is also a [scope] property which is automatically managed.
 *
 * ### Before Each:
 * [Dispatchers.Main] is set to the [TestCoroutineDispatcher] used by the [CoroutineContext].
 *
 * ### After Each:
 * * [cleanupTestCoroutines][TestCoroutineScope.cleanupTestCoroutines] is called to ensure there are no leaking coroutines.  Any unfinished coroutine
 * will throw an [UncompletedCoroutinesError].
 * * [Dispatchers.Main] is reset via [Dispatchers.resetMain].
 *
 * ### Requires JUnit 5.
 * ``` Kotlin
 * dependencies {
 *   testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
 * }
 * ```
 *
 * @see CoroutineTestExtension
 * @see CoroutineTest
 * @param scopeFactory *optional* factory for a custom [TestProvidedCoroutineScope].  If a factory is not provided,
 * the resultant scope uses the same [TestCoroutineDispatcher] for each property in its [TestDispatcherProvider]
 * @sample samples.RegisterSample
 * @sample samples.RegisterWithFactorySample
 * @sample samples.CoroutineTestExtensionExtendWithSample
 */
@ExperimentalCoroutinesApi
@Suppress("newApi") // this arbitrary build target is 21, but JUnit5 requires Java 8
public class CoroutineTestExtension(
  private val scopeFactory: ScopeFactory = ScopeFactory()
) : TypeBasedParameterResolver<TestProvidedCoroutineScope>(),
  BeforeEachCallback,
  AfterEachCallback {

  private val lazyScope = lazy { scopeFactory.create() }

  /**
   * A single `TestProvidedCoroutineScope` instance which is reset via [cleanUpTestCoroutines][TestCoroutineScope.cleanupTestCoroutines] after each test.
   */
  public val scope: TestProvidedCoroutineScope
    get() = lazyScope.value

  private val contextScopeMap = mutableMapOf<ExtensionContext, TestProvidedCoroutineScope>()

  /**
   * @suppress
   */
  override fun resolveParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): TestProvidedCoroutineScope {

    val scope = contextScopeMap[extensionContext]

    // if a scope was already created for this context (not sure how), return it
    if (scope != null) return scope

    val annotation = extensionContext.getAnnotationRecursive<CoroutineTest>()

    // if the extension is registered via @RegisterExtension,
    // then its factory would be passed at instance creation and is already set
    val newScope = if (annotation == null) {
      scopeFactory.create()
    } else {
      @Suppress("UNCHECKED_CAST")
      val factoryClass = annotation.scopeFactory

      val factory = factoryClass.java.getConstructor().newInstance() as? ScopeFactory
        ?: throw resolutionException(factoryClass)

      factory.create()
    }

    contextScopeMap[extensionContext] = newScope

    /*
    If injecting a function, then the scope isn't created until after beforeEach,
    so we have to setMain here
     */
    extensionContext.testMethod.ifPresent {
      Dispatchers.setMain(newScope.dispatcherProvider.main)
    }

    return newScope
  }

  /**
   * @suppress
   */
  override fun beforeEach(context: ExtensionContext) {

    /*
    In case of using RegisterExtension with the internal scope without resolving a parameter,
    make sure it's registered as Dispatchers.Main.
     */
    val scope = contextScopeMap[context] ?: this.scope

    Dispatchers.setMain(scope.dispatcherProvider.main)
  }

  /**
   * @suppress
   */
  override fun afterEach(context: ExtensionContext) {

    if (lazyScope.isInitialized()) {
      scope.cleanupTestCoroutines()
    }

    contextScopeMap[context]?.cleanupTestCoroutines()
    Dispatchers.resetMain()
  }

  /**
   * Class used to create the [TestProvidedCoroutineScope] used in [CoroutineTestExtension].
   *
   * In order to provide a custom implementation of [TestProvidedCoroutineScope]:
   * 1. Create a custom factory which has a default constructor and extends this `ScopeFactory`
   * 2. Annotate your test class with [CoroutineTest] and pass your custom factory's `KClass` in as its parameter.
   *
   * @sample samples.CoroutineTestNamedFactorySample
   */
  @ExperimentalCoroutinesApi
  public open class ScopeFactory {

    /**
     * Creates an instance of [TestProvidedCoroutineScope].  Uses the no-arg factory by default.
     */
    public open fun create(): TestProvidedCoroutineScope =
      TestProvidedCoroutineScope()
  }
}

/**
 * Factory function for creating a [CoroutineTestExtension].
 *
 * @see CoroutineTestExtension
 * @see CoroutineTest
 * @param scopeFactory *optional* factory for a custom [TestProvidedCoroutineScope].  If a factory is not provided,
 * the resultant scope uses the same [TestCoroutineDispatcher] for each property in its [TestDispatcherProvider]
 * @sample samples.RegisterSample
 * @sample samples.RegisterWithFactorySample
 */
@ExperimentalCoroutinesApi
public inline fun coroutineTestExtension(
  /**
   * This factory lambda creates the [TestProvidedCoroutineScope] which is managed by the [CoroutineTestExtension].
   *
   * By default, it creates a standard [TestProvidedCoroutineScope].
   */
  crossinline scopeFactory: () -> TestProvidedCoroutineScope = { TestProvidedCoroutineScope() }
): CoroutineTestExtension =
  CoroutineTestExtension(object : ScopeFactory() {
    override fun create(): TestProvidedCoroutineScope = scopeFactory.invoke()
  })
