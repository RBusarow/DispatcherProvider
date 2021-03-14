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
 * Extracts the **default** [CoroutineDispatcher] out of the [CoroutineScope],
 * using [DefaultDispatcherProvider.get] to provide one if necessary.
 *
 * Note that `CoroutineContext` is immutable, so if a new `DefaultDispatcherProvider` is needed,
 * a new instance will be created each time.
 *
 * @see CoroutineScope.dispatcherProvider
 */
public val CoroutineScope.defaultDispatcher: CoroutineDispatcher
  get() = dispatcherProvider.default

/**
 * Extracts the **io** [CoroutineDispatcher] out of the [CoroutineScope],
 * using [DefaultDispatcherProvider.get] to provide one if necessary.
 *
 * Note that `CoroutineContext` is immutable, so if a new `DefaultDispatcherProvider` is needed,
 * a new instance will be created each time.
 *
 * @see CoroutineScope.dispatcherProvider
 */
public val CoroutineScope.ioDispatcher: CoroutineDispatcher
  get() = dispatcherProvider.io

/**
 * Extracts the **main** [CoroutineDispatcher] out of the [CoroutineScope],
 * using [DefaultDispatcherProvider.get] to provide one if necessary.
 *
 * Note that `CoroutineContext` is immutable, so if a new `DefaultDispatcherProvider` is needed,
 * a new instance will be created each time.
 *
 * @see CoroutineScope.dispatcherProvider
 */
public val CoroutineScope.mainDispatcher: CoroutineDispatcher
  get() = dispatcherProvider.main

/**
 * Extracts the **mainImmediate** [CoroutineDispatcher] out of the [CoroutineScope],
 * using [DefaultDispatcherProvider.get] to provide one if necessary.
 *
 * Note that `CoroutineContext` is immutable, so if a new `DefaultDispatcherProvider` is needed,
 * a new instance will be created each time.
 *
 * @see CoroutineScope.dispatcherProvider
 */
public val CoroutineScope.mainImmediateDispatcher: CoroutineDispatcher
  get() = dispatcherProvider.mainImmediate

/**
 * Extracts the **unconfined** [CoroutineDispatcher] out of the [CoroutineScope],
 * using [DefaultDispatcherProvider.get] to provide one if necessary.
 *
 * Note that `CoroutineContext` is immutable, so if a new `DefaultDispatcherProvider` is needed,
 * a new instance will be created each time.
 *
 * @see CoroutineScope.dispatcherProvider
 */
public val CoroutineScope.unconfinedDispatcher: CoroutineDispatcher
  get() = dispatcherProvider.unconfined

/**
 * Extracts the [DispatcherProvider] out of the [CoroutineScope],
 * or returns a new instance of [DefaultDispatcherProvider.get] if the `coroutineContext`
 * does not have one specified.
 *
 * Note that `CoroutineContext` is immutable, so if a new `DefaultDispatcherProvider` is needed,
 * a new instance will be created each time.
 *
 * @see CoroutineContext.dispatcherProvider
 */
public val CoroutineScope.dispatcherProvider: DispatcherProvider
  get() = coroutineContext.dispatcherProvider

/**
 * Extracts the [DispatcherProvider] out of the [CoroutineContext],
 * or returns a default from [DefaultDispatcherProvider.get] if the `CoroutineContext`
 * does not have one specified.
 *
 * Note that `CoroutineContext` is immutable, so if a new `DefaultDispatcherProvider` is needed,
 * a new instance will be created each time.
 */
public val CoroutineContext.dispatcherProvider: DispatcherProvider
  get() = get(DispatcherProvider) ?: DefaultDispatcherProvider.get()
