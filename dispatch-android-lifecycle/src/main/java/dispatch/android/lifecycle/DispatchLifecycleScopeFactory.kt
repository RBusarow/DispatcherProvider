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
import kotlin.coroutines.*

/**
 * Factory for [DispatchLifecycleScope]s.  This may be injected into a lifecycle-aware class
 * to provide custom [CoroutineContexts][CoroutineContext].
 *
 * @sample samples.DispatchLifecycleScopeFactorySample.factorySample
 * @param coroutineContextFactory the lambda defining the creating of a [CoroutineContext]
 */
public class DispatchLifecycleScopeFactory(
  private val coroutineContextFactory: () -> CoroutineContext
) {

  /**
   * Creates a new [DispatchLifecycleScope] using `coroutineContextFactory`
   *
   * @param lifecycle the lifecycle which will be bound to the [DispatchLifecycleScope]
   */
  public fun create(
    lifecycle: Lifecycle
  ): DispatchLifecycleScope = DispatchLifecycleScope(lifecycle, coroutineContextFactory.invoke())
}
