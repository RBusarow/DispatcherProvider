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
@file:Suppress("HardCodedDispatcher")

package dispatch.core

import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * Interface corresponding to the different [CoroutineDispatcher]'s offered by [Dispatchers].
 *
 * Implements the [CoroutineContext.Element] interface
 * so that it can be embedded into the [CoroutineContext] map,
 * meaning that a `CoroutineContext` can be composed with a set of pre-set dispatchers,
 * thereby eliminating the need for singleton references or dependency injecting this interface.
 */
public interface DispatcherProvider : CoroutineContext.Element {

  /**
   * This unique [Key] property is what allows the `DispatcherProvider` to be stored in the [CoroutineContext].
   */
  override val key: CoroutineContext.Key<*> get() = Key

  /**
   * [CoroutineDispatcher] generally intended for cpu-bound tasks.
   *
   * Corresponds to the [Dispatchers.Default] property in a default implementation.
   *
   * @see Dispatchers.Default
   */
  public val default: CoroutineDispatcher get() = Dispatchers.Default

  /**
   * [CoroutineDispatcher] generally intended for blocking I/O tasks.
   *
   * Corresponds to the [Dispatchers.IO] property in a default implementation.
   *
   * @see Dispatchers.IO
   */
  public val io: CoroutineDispatcher get() = Dispatchers.IO

  /**
   * [CoroutineDispatcher] which is confined to the "main" thread.
   *
   * Corresponds to the [Dispatchers.Main] property in a default implementation.
   *
   * @see Dispatchers.Main
   */
  public val main: CoroutineDispatcher get() = Dispatchers.Main

  /**
   * [CoroutineDispatcher] which is confined to the "main" thread with immediate dispatch.
   *
   * Corresponds to the [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate] property in a default implementation.
   *
   * @see MainCoroutineDispatcher.immediate
   */
  public val mainImmediate: CoroutineDispatcher get() = Dispatchers.Main.immediate

  /**
   * [CoroutineDispatcher] which is unconfined.
   *
   * Corresponds to the [Dispatchers.Unconfined] property in a default implementation.
   *
   * @see Dispatchers.Unconfined
   */
  public val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined

  /**
   * Unique [Key] definition which allows the `DispatcherProvider` to be stored in the [CoroutineContext].
   */
  public companion object Key : CoroutineContext.Key<DispatcherProvider> {

    /**
     * Default implementation of [DispatcherProvider] which simply delegates to the corresponding
     * properties in the [Dispatchers] singleton.
     *
     * This should be suitable for most production code.
     *
     * @see DefaultDispatcherProvider
     */
    public operator fun invoke(): DispatcherProvider = DefaultDispatcherProvider.get()
  }
}
