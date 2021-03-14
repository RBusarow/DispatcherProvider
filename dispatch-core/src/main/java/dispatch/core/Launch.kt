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

package dispatch.core

import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a [Job].
 * The coroutine is cancelled when the resulting job is [cancelled][Job.cancel].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **default** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.default`) to call `launch(...)`.
 *
 * The `default` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.LaunchSample.launchDefaultSample
 * @see launch
 */
public fun CoroutineScope.launchDefault(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job = launch(context + coroutineContext.dispatcherProvider.default, start, block)

/**
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a [Job].
 * The coroutine is cancelled when the resulting job is [cancelled][Job.cancel].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **io** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.io`) to call `launch(...)`.
 *
 * The `io` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.LaunchSample.launchIOSample
 * @see launch
 */
public fun CoroutineScope.launchIO(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job = launch(context + coroutineContext.dispatcherProvider.io, start, block)

/**
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a [Job].
 * The coroutine is cancelled when the resulting job is [cancelled][Job.cancel].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **main** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.main`) to call `launch(...)`.
 *
 * The `main` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.LaunchSample.launchMainSample
 * @see launch
 */
public fun CoroutineScope.launchMain(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job = launch(context + coroutineContext.dispatcherProvider.main, start, block)

/**
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a [Job].
 * The coroutine is cancelled when the resulting job is [cancelled][Job.cancel].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **mainImmediate** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.mainImmediate`) to call `launch(...)`.
 *
 * The `mainImmediate` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.LaunchSample.launchMainImmediateSample
 * @see launch
 */
public fun CoroutineScope.launchMainImmediate(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job = launch(context + coroutineContext.dispatcherProvider.mainImmediate, start, block)

/**
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a [Job].
 * The coroutine is cancelled when the resulting job is [cancelled][Job.cancel].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **unconfined** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.unconfined`) to call `launch(...)`.
 *
 * The `unconfined` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.LaunchSample.launchUnconfinedSample
 * @see launch
 */
public fun CoroutineScope.launchUnconfined(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job = launch(context + coroutineContext.dispatcherProvider.unconfined, start, block)
