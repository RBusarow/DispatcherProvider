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

@file:Suppress("DeferredIsResult")

package dispatch.core

import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * Creates a coroutine and returns its future result as an implementation of [Deferred].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **default** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.default`) to call `async(...)`.
 *
 * The `default` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.AsyncSample.asyncDefaultSample
 * @see async
 */
public fun <T> CoroutineScope.asyncDefault(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> T
): Deferred<T> = async(context + coroutineContext.dispatcherProvider.default, start, block)

/**
 * Creates a coroutine and returns its future result as an implementation of [Deferred].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **io** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.io`) to call `async(...)`.
 *
 * The `io` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.AsyncSample.asyncIOSample
 * @see async
 */
public fun <T> CoroutineScope.asyncIO(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> T
): Deferred<T> = async(context + coroutineContext.dispatcherProvider.io, start, block)

/**
 * Creates a coroutine and returns its future result as an implementation of [Deferred].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **main** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.main`) to call `async(...)`.
 *
 * The `main` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.AsyncSample.asyncMainSample
 * @see async
 */
public fun <T> CoroutineScope.asyncMain(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> T
): Deferred<T> = async(context + coroutineContext.dispatcherProvider.main, start, block)

/**
 * Creates a coroutine and returns its future result as an implementation of [Deferred].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **mainImmediate** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.mainImmediate`) to call `async(...)`.
 *
 * The `mainImmediate` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.AsyncSample.asyncMainImmediateSample
 * @see async
 */
public fun <T> CoroutineScope.asyncMainImmediate(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> T
): Deferred<T> = async(context + coroutineContext.dispatcherProvider.mainImmediate, start, block)

/**
 * Creates a coroutine and returns its future result as an implementation of [Deferred].
 *
 * Extracts the [DispatcherProvider] from the [CoroutineScope] receiver, then uses its **unconfined** [CoroutineDispatcher]
 * property (`coroutineContext.dispatcherProvider.unconfined`) to call `async(...)`.
 *
 * The `unconfined` property always corresponds to the `DispatcherProvider` of the current `CoroutineScope`.
 *
 * @sample samples.AsyncSample.asyncUnconfinedSample
 * @see async
 */
public fun <T> CoroutineScope.asyncUnconfined(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> T
): Deferred<T> = async(context + coroutineContext.dispatcherProvider.unconfined, start, block)
