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

package dispatch.android.viewmodel

import dispatch.android.viewmodel.ViewModelScopeFactory.reset
import dispatch.core.*
import kotlinx.coroutines.*

/**
 * Factory holder for [viewModelScope][dispatch.android.viewmodel.DispatchViewModel.viewModelScope]'s.
 *
 * By default, `create` returns a [MainImmediateCoroutineScope], but may return any [CoroutineScope].
 *
 * This factory can be overridden for testing or to include a custom [CoroutineContext][kotlin.coroutines.CoroutineContext]
 * in production code.  This may be done in [Application.onCreate][android.app.Application.onCreate].
 *
 * [reset] may be used to reset the factory to default at any time.
 * @sample samples.ViewModelScopeFactorySample.setViewModelScopeFactoryProductionSample
 * @sample samples.ViewModelScopeFactorySample.setViewModelScopeFactoryEspressoSample
 * @sample samples.ViewModelScopeFactorySample.setViewModelScopeFactoryJvmSample
 */
public object ViewModelScopeFactory {

  private var _factory: () -> CoroutineScope = { MainImmediateCoroutineScope() }

  /**
   * Override the default [MainImmediateCoroutineScope] factory, for testing or to include a custom [CoroutineContext][kotlin.coroutines.CoroutineContext]
   * in production code.  This may be done in [Application.onCreate][android.app.Application.onCreate]
   *
   * @param factory sets a custom [CoroutineScope] factory to be used for all new instance creations until reset.
   * @sample samples.ViewModelScopeFactorySample.setViewModelScopeFactoryProductionSample
   * @sample samples.ViewModelScopeFactorySample.setViewModelScopeFactoryEspressoSample
   * @sample samples.ViewModelScopeFactorySample.setViewModelScopeFactoryJvmSample
   */
  public fun set(factory: () -> CoroutineScope) {
    _factory = factory
  }

  internal fun create(): CoroutineScope = _factory.invoke()

  /**
   * Immediately resets the factory function to its default.
   *
   * @sample samples.ViewModelScopeFactorySample.viewModelScopeFactoryResetSample
   */
  public fun reset() {
    _factory = { MainCoroutineScope() }
  }
}
