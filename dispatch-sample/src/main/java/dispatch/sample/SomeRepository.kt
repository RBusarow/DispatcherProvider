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

package dispatch.sample

import dispatch.core.*
import kotlinx.coroutines.*

/**
 * This would normally be a singleton,
 * but we don't have a DI framework here, so we'll just _suspend_ disbelief.
 */
class SomeRepository(private val coroutineScope: IOCoroutineScope) {

  @Suppress("MagicNumber")
  suspend fun getSomethingExpensive() = withIO {
    delay(5000)
    "suspend function is complete!"
  }

  @Suppress("MagicNumber")
  fun getSomethingExpensiveUnstructured() = coroutineScope.asyncIO {
    delay(5000)
    "deferred function is complete!"
  }
}
