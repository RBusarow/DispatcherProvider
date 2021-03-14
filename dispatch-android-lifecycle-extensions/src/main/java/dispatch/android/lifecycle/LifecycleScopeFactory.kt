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

package dispatch.android.lifecycle

import androidx.lifecycle.*
import dispatch.android.lifecycle.LifecycleScopeFactory.reset
import dispatch.core.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * Factory holder for [DispatchLifecycleScope]'s.
 *
 * By default, `create` returns a [MainImmediateContext].
 *
 * This factory can be overridden for testing or to include a custom [CoroutineContext][kotlin.coroutines.CoroutineContext]
 * in production code.  This may be done in [Application.onCreate][android.app.Application.onCreate],
 * or else as early as possible in the Application's existence.
 *
 * A custom factory will persist for the application's full lifecycle unless overwritten or [reset].
 *
 * [reset] may be used to reset the factory to default at any time.
 *
 * @see MainImmediateContext
 * @see DispatchLifecycleScope
 * @see DispatchLifecycleScopeFactory
 * @sample samples.LifecycleScopeFactorySample.setLifecycleScopeFactoryProductionSample
 * @sample samples.LifecycleScopeFactorySample.setLifecycleScopeFactoryEspressoSample
 */
object LifecycleScopeFactory {

  private val defaultFactory: DispatchLifecycleScopeFactory
    get() = DispatchLifecycleScopeFactory { MainImmediateContext() }

  private var factoryInstance = defaultFactory

  internal fun create(lifecycle: Lifecycle): DispatchLifecycleScope =
    factoryInstance.create(lifecycle)

  /**
   * Immediately resets the factory function to its default.
   *
   * @sample samples.LifecycleScopeFactorySample.LifecycleScopeFactoryResetSample
   */
  @Suppress("UNUSED")
  public fun reset() {
    factoryInstance = defaultFactory
  }

  /**
   * Override the default [MainImmediateCoroutineScope] factory, for testing or to include a custom [CoroutineContext][kotlin.coroutines.CoroutineContext]
   * in production code.  This may be done in [Application.onCreate][android.app.Application.onCreate]
   *
   * @param factory sets a custom [CoroutineContext] factory to be used for all new instance creations until reset.
   * Its [Elements][CoroutineContext.Element] will be re-used, except:
   * 1. If a [DispatcherProvider] element isn't present, [DefaultDispatcherProvider.get] will be added.
   * 2. If a [Job] element isn't present, a [SupervisorJob] will be added.
   * 3. If the [ContinuationInterceptor][kotlin.coroutines.ContinuationInterceptor] does not match
   * the one referenced by the [possibly new] [DispatcherProvider.mainImmediate] property, it will be updated to match.
   *
   * @sample samples.LifecycleScopeFactorySample.setLifecycleScopeFactoryProductionSample
   * @sample samples.LifecycleScopeFactorySample.setLifecycleScopeFactoryEspressoSample
   */
  @Suppress("UNUSED")
  public fun set(factory: DispatchLifecycleScopeFactory) {
    factoryInstance = factory
  }

  /**
   * Override the default [MainImmediateCoroutineScope] factory, for testing or to include a custom [CoroutineContext][kotlin.coroutines.CoroutineContext]
   * in production code.  This may be done in [Application.onCreate][android.app.Application.onCreate]
   *
   * @param factory sets a custom [CoroutineContext] factory to be used for all new instance creations until reset.
   * Its [Elements][CoroutineContext.Element] will be re-used, except:
   * 1. If a [DispatcherProvider] element isn't present, [DefaultDispatcherProvider.get] will be added.
   * 2. If a [Job] element isn't present, a [SupervisorJob] will be added.
   * 3. If the [ContinuationInterceptor][kotlin.coroutines.ContinuationInterceptor] does not match
   * the one referenced by the [possibly new] [DispatcherProvider.mainImmediate] property, it will be updated to match.
   *
   * @sample samples.LifecycleScopeFactorySample.setLifecycleScopeFactoryProductionSample
   * @sample samples.LifecycleScopeFactorySample.setLifecycleScopeFactoryEspressoSample
   */
  @Suppress("UNUSED")
  public fun set(factory: () -> CoroutineContext) {
    factoryInstance = DispatchLifecycleScopeFactory(factory)
  }
}
