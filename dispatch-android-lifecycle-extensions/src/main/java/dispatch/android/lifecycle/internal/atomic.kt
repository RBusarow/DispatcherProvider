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

package dispatch.android.lifecycle.internal

import androidx.lifecycle.*
import dispatch.android.lifecycle.*
import dispatch.core.*
import kotlinx.coroutines.*
import java.util.concurrent.*

/**
 * Normal [getOrPut] is guaranteed to only return a single instance for a given key,
 * but the [defaultValue] lambda may be invoked unnecessarily.  This would create a new instance
 * of [DispatchLifecycleScope] without actually returning it.
 *
 * This extra [DispatchLifecycleScope] would still be active and observing the [lifecycle][key].
 *
 * This function compares the result of the lambda to the result of [getOrPut],
 * and cancels the lambda's result if it's different.
 *
 * @see getOrPut
 */
internal inline fun ConcurrentMap<Lifecycle, DispatchLifecycleScope>.atomicGetOrPut(
  key: Lifecycle,
  defaultValue: () -> DispatchLifecycleScope
): DispatchLifecycleScope {

  var newInstance: DispatchLifecycleScope? = null

  val existingOrNew = getOrPut(key) {
    newInstance = defaultValue.invoke()
    newInstance!!
  }

  // If the second value is different, that means the first was never inserted into the map.
  // Cancel the observer for `newInstance` and cancel the coroutineContext.
  newInstance?.let { new ->
    if (new != existingOrNew) {
      existingOrNew.launchMainImmediate {

        new.coroutineContext[Job]?.cancel()
      }
    }
  }

  return existingOrNew
}
