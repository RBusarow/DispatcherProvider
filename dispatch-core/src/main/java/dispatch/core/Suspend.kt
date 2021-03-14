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
 * Calls the specified suspending block with a given coroutine context, suspends until it completes, and returns
 * the result.
 *
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the current coroutine,
 * then uses its **default** [CoroutineDispatcher] property to call `withContext(theDispatcher)`,
 * and returns the result.
 *
 * The *default* property always corresponds to the `DispatcherProvider` of the current coroutine.
 *
 * @sample samples.WithContextSample.withDefaultSample
 * @see withContext
 */
public suspend fun <T> withDefault(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T {
  val newContext = context + coroutineContext.dispatcherProvider.default
  return withContext(newContext, block)
}

/**
 * Calls the specified suspending block with a given coroutine context, suspends until it completes, and returns
 * the result.
 *
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the current coroutine,
 * then uses its **io** [CoroutineDispatcher] property to call `withContext(theDispatcher)`,
 * and returns the result.
 *
 * The `io` property always corresponds to the `DispatcherProvider` of the current coroutine.
 *
 * @sample samples.WithContextSample.withIOSample
 * @see withContext
 */
public suspend fun <T> withIO(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T {
  val newContext = context + coroutineContext.dispatcherProvider.io
  return withContext(newContext, block)
}

/**
 * Calls the specified suspending block with a given coroutine context, suspends until it completes, and returns
 * the result.
 *
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the current coroutine,
 * then uses its **main** [CoroutineDispatcher] property to call `withContext(theDispatcher)`,
 * and returns the result.
 *
 * The `main` property always corresponds to the `DispatcherProvider` of the current coroutine.
 *
 * @sample samples.WithContextSample.withMainSample
 * @see withContext
 */
public suspend fun <T> withMain(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T {
  val newContext = context + coroutineContext.dispatcherProvider.main
  return withContext(newContext, block)
}

/**
 * Calls the specified suspending block with a given coroutine context, suspends until it completes, and returns
 * the result.
 *
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the current coroutine,
 * then uses its **mainImmediate** [CoroutineDispatcher] property to call `withContext(theDispatcher)`,
 * and returns the result.
 *
 * The `mainImmediate` property always corresponds to the `DispatcherProvider` of the current coroutine.
 *
 * @sample samples.WithContextSample.withMainImmediateSample
 * @see withContext
 */
public suspend fun <T> withMainImmediate(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T {
  val newContext = context + coroutineContext.dispatcherProvider.mainImmediate
  return withContext(newContext, block)
}

/**
 * Calls the specified suspending block with a given coroutine context, suspends until it completes, and returns
 * the result.
 *
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the current coroutine,
 * then uses its **unconfined** [CoroutineDispatcher] property to call `withContext(theDispatcher)`,
 * and returns the result.
 *
 * The `unconfined` property always corresponds to the `DispatcherProvider` of the current coroutine.
 *
 * @sample samples.WithContextSample.withUnconfinedSample
 * @see withContext
 */
public suspend fun <T> withUnconfined(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T {
  val newContext = context + coroutineContext.dispatcherProvider.unconfined
  return withContext(newContext, block)
}
