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
import kotlinx.coroutines.flow.*

/**
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the *collector* coroutine,
 * then uses its [DispatcherProvider.default] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @sample samples.FlowOnSample.flowOnDefaultSample
 * @see flowOn
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<T>.flowOnDefault(): Flow<T> = flow {
  flowOn(currentCoroutineContext().dispatcherProvider.default)
    .collect { emit(it) }
}

/**
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the *collector* coroutine,
 * then uses its [DispatcherProvider.io] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @sample samples.FlowOnSample.flowOnIOSample
 * @see flowOn
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<T>.flowOnIO(): Flow<T> = flow {
  flowOn(currentCoroutineContext().dispatcherProvider.io)
    .collect { emit(it) }
}

/**
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the *collector* coroutine,
 * then uses its [DispatcherProvider.main] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @sample samples.FlowOnSample.flowOnMainSample
 * @see flowOn
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<T>.flowOnMain(): Flow<T> = flow {
  flowOn(currentCoroutineContext().dispatcherProvider.main)
    .collect { emit(it) }
}

/**
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the *collector* coroutine,
 * then uses its [DispatcherProvider.mainImmediate] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @sample samples.FlowOnSample.flowOnMainImmediateSample
 * @see flowOn
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<T>.flowOnMainImmediate(): Flow<T> = flow {
  flowOn(currentCoroutineContext().dispatcherProvider.mainImmediate)
    .collect { emit(it) }
}

/**
 * Extracts the [DispatcherProvider] from the `coroutineContext` of the *collector* coroutine,
 * then uses its [DispatcherProvider.unconfined] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @sample samples.FlowOnSample.flowOnUnconfinedSample
 * @see flowOn
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<T>.flowOnUnconfined(): Flow<T> = flow {
  flowOn(currentCoroutineContext().dispatcherProvider.unconfined)
    .collect { emit(it) }
}
