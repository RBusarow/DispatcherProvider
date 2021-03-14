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

import kotlinx.atomicfu.*
import kotlinx.coroutines.*

/**
 * Holder singleton for a [DispatcherProvider] instance.
 *
 * If [CoroutineScope.dispatcherProvider][dispatch.core.dispatcherProvider] or [CoroutineContext.dispatcherProvider][dispatch.core.dispatcherProvider] is referenced
 * in a [CoroutineContext][kotlin.coroutines.CoroutineContext] which does not have one,
 * it will use a default defined by this object.
 *
 * @sample samples.DefaultDispatcherProviderSample.defaultDispatcherProviderSetSample
 */
public object DefaultDispatcherProvider {

  private val holder = atomic(default())

  /**
   * Returns the current configured default [DispatcherProvider]
   *
   * @see set
   */
  public fun get(): DispatcherProvider = holder.value

  /**
   * Atomically sets a default [DispatcherProvider] instance.
   *
   * @see get
   * @sample samples.DefaultDispatcherProviderSample.defaultDispatcherProviderSetSample
   */
  public fun set(value: DispatcherProvider) {
    while (true) {
      val cur = holder.value
      if (holder.compareAndSet(cur, value)) return
    }
  }

  private fun default(): DispatcherProvider = object : DispatcherProvider {}

  /**
   * Default implementation of [DispatcherProvider] which simply delegates to the corresponding
   * properties in the [Dispatchers] singleton.
   *
   * This should be suitable for most production code.
   *
   * **Deprecated**
   * The DefaultDispatcherProvider class has been replaced with this singleton object.
   * To create a DispatcherProvider with the default implementation, use the DispatcherProvider companion object factory.
   * This function will be removed before the 1.0 release.
   *
   * @see DispatcherProvider
   */
  @Deprecated(
    message = "The DefaultDispatcherProvider class has been replaced with this singleton object.  " +
      "To create a DispatcherProvider with the default implementation," +
      " use the DispatcherProvider companion object factory.  " +
      "This function will be removed before the 1.0 release.",
    replaceWith = ReplaceWith("DispatcherProvider()"),
    level = DeprecationLevel.HIDDEN
  )
  public operator fun invoke(): DispatcherProvider = DispatcherProvider()
}
