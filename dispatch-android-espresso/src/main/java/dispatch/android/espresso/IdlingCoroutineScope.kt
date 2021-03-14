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

package dispatch.android.espresso

import androidx.test.espresso.*
import dispatch.core.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * Special [CoroutineScope] with a [DispatcherProvider] which is an [IdlingDispatcherProvider].
 *
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 */
interface IdlingCoroutineScope : CoroutineScope {

  /**
   * Any [IdlingCoroutineScope] has an [idlingDispatcherProvider] property which can be registered in the [IdlingRegistry].
   */
  val idlingDispatcherProvider: IdlingDispatcherProvider
}

/**
 * Marker interface for an [IdlingCoroutineScope] which indicates that its [CoroutineDispatcher] is [DispatcherProvider.default]
 *
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 */
interface DefaultIdlingCoroutineScope :
  IdlingCoroutineScope,
  DefaultCoroutineScope

/**
 * Marker interface for an [IdlingCoroutineScope] which indicates that its [CoroutineDispatcher] is [DispatcherProvider.io]
 *
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 */
interface IOIdlingCoroutineScope :
  IdlingCoroutineScope,
  IOCoroutineScope

/**
 * Marker interface for an [IdlingCoroutineScope] which indicates that its [CoroutineDispatcher] is [DispatcherProvider.main]
 *
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 */
interface MainIdlingCoroutineScope :
  IdlingCoroutineScope,
  MainCoroutineScope

/**
 * Marker interface for an [IdlingCoroutineScope] which indicates that its [CoroutineDispatcher] is [DispatcherProvider.mainImmediate]
 *
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 */
interface MainImmediateIdlingCoroutineScope :
  IdlingCoroutineScope,
  MainImmediateCoroutineScope

/**
 * Marker interface for an [IdlingCoroutineScope] which indicates that its [CoroutineDispatcher] is [DispatcherProvider.unconfined]
 *
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 */
interface UnconfinedIdlingCoroutineScope :
  IdlingCoroutineScope,
  UnconfinedCoroutineScope

/**
 * Factory function for an [IdlingCoroutineScope].
 *
 * @param job *optional* The [Job] used in creation of the [CoroutineContext].  Uses [SupervisorJob] by default.
 * @param dispatcherProvider The [IdlingDispatcherProvider] used in creation of the [CoroutineContext].
 * Uses the default [IdlingDispatcherProvider] factory by default.
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 * @sample samples.IdlingCoroutineScopeSample.createNoArgIdlingCoroutineScope
 * @sample samples.IdlingCoroutineScopeSample.createCustomIdlingCoroutineScope
 */
fun IdlingCoroutineScope(
  job: Job = SupervisorJob(),
  dispatcherProvider: IdlingDispatcherProvider = IdlingDispatcherProvider()
): IdlingCoroutineScope = object : IdlingCoroutineScope {
  override val coroutineContext = job + dispatcherProvider.default + dispatcherProvider
  override val idlingDispatcherProvider: IdlingDispatcherProvider = dispatcherProvider
}

/**
 * Factory function for a [DefaultIdlingCoroutineScope].
 *
 * @param job *optional* The [Job] used in creation of the [CoroutineContext].  Uses [SupervisorJob] by default.
 * @param dispatcherProvider The [IdlingDispatcherProvider] used in creation of the [CoroutineContext].
 * Uses the default [IdlingDispatcherProvider] factory by default.
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 * @sample samples.DefaultIdlingCoroutineScopeSample.createNoArgDefaultIdlingCoroutineScope
 * @sample samples.DefaultIdlingCoroutineScopeSample.createCustomDefaultIdlingCoroutineScope
 */
fun DefaultIdlingCoroutineScope(
  job: Job = SupervisorJob(),
  dispatcherProvider: IdlingDispatcherProvider = IdlingDispatcherProvider()
): DefaultIdlingCoroutineScope = object : DefaultIdlingCoroutineScope {
  override val coroutineContext = job + dispatcherProvider.default + dispatcherProvider
  override val idlingDispatcherProvider: IdlingDispatcherProvider = dispatcherProvider
}

/**
 * Factory function for an [IOIdlingCoroutineScope].
 *
 * @param job *optional* The [Job] used in creation of the [CoroutineContext].  Uses [SupervisorJob] by default.
 * @param dispatcherProvider The [IdlingDispatcherProvider] used in creation of the [CoroutineContext].
 * Uses the default [IdlingDispatcherProvider] factory by default.
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 * @sample samples.IOIdlingCoroutineScopeSample.createNoArgIOIdlingCoroutineScope
 * @sample samples.IOIdlingCoroutineScopeSample.createCustomIOIdlingCoroutineScope
 */
fun IOIdlingCoroutineScope(
  job: Job = SupervisorJob(),
  dispatcherProvider: IdlingDispatcherProvider = IdlingDispatcherProvider()
): IOIdlingCoroutineScope = object : IOIdlingCoroutineScope {
  override val coroutineContext = job + dispatcherProvider.io + dispatcherProvider
  override val idlingDispatcherProvider: IdlingDispatcherProvider = dispatcherProvider
}

/**
 * Factory function for a [MainIdlingCoroutineScope].
 *
 * @param job *optional* The [Job] used in creation of the [CoroutineContext].  Uses [SupervisorJob] by default.
 * @param dispatcherProvider The [IdlingDispatcherProvider] used in creation of the [CoroutineContext].
 * Uses the default [IdlingDispatcherProvider] factory by default.
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 * @sample samples.MainIdlingCoroutineScopeSample.createNoArgMainIdlingCoroutineScope
 * @sample samples.MainIdlingCoroutineScopeSample.createCustomMainIdlingCoroutineScope
 */
fun MainIdlingCoroutineScope(
  job: Job = SupervisorJob(),
  dispatcherProvider: IdlingDispatcherProvider = IdlingDispatcherProvider()
): MainIdlingCoroutineScope = object : MainIdlingCoroutineScope {
  override val coroutineContext = job + dispatcherProvider.main + dispatcherProvider
  override val idlingDispatcherProvider: IdlingDispatcherProvider = dispatcherProvider
}

/**
 * Factory function for a [MainImmediateIdlingCoroutineScope].
 *
 * @param job *optional* The [Job] used in creation of the [CoroutineContext].  Uses [SupervisorJob] by default.
 * @param dispatcherProvider The [IdlingDispatcherProvider] used in creation of the [CoroutineContext].
 * Uses the default [IdlingDispatcherProvider] factory by default.
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 * @sample samples.MainImmediateIdlingCoroutineScopeSample.createNoArgMainImmediateIdlingCoroutineScope
 * @sample samples.MainImmediateIdlingCoroutineScopeSample.createCustomMainImmediateIdlingCoroutineScope
 */
fun MainImmediateIdlingCoroutineScope(
  job: Job = SupervisorJob(),
  dispatcherProvider: IdlingDispatcherProvider = IdlingDispatcherProvider()
): MainImmediateIdlingCoroutineScope = object : MainImmediateIdlingCoroutineScope {
  override val coroutineContext = job + dispatcherProvider.mainImmediate +
    dispatcherProvider
  override val idlingDispatcherProvider: IdlingDispatcherProvider = dispatcherProvider
}

/**
 * Factory function for an [UnconfinedIdlingCoroutineScope].
 *
 * @param job *optional* The [Job] used in creation of the [CoroutineContext].  Uses [SupervisorJob] by default.
 * @param dispatcherProvider The [IdlingDispatcherProvider] used in creation of the [CoroutineContext].
 * Uses the default [IdlingDispatcherProvider] factory by default.
 * @see IdlingDispatcherProvider
 * @see IdlingResource
 * @see IdlingCoroutineScope
 * @sample samples.UnconfinedIdlingCoroutineScopeSample.createNoArgUnconfinedIdlingCoroutineScope
 * @sample samples.UnconfinedIdlingCoroutineScopeSample.createCustomUnconfinedIdlingCoroutineScope
 */
fun UnconfinedIdlingCoroutineScope(
  job: Job = SupervisorJob(),
  dispatcherProvider: IdlingDispatcherProvider = IdlingDispatcherProvider()
): UnconfinedIdlingCoroutineScope = object : UnconfinedIdlingCoroutineScope {
  override val coroutineContext = job + dispatcherProvider.unconfined + dispatcherProvider
  override val idlingDispatcherProvider: IdlingDispatcherProvider = dispatcherProvider
}
