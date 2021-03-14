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

package dispatch.internal.test

import kotlinx.atomicfu.*
import org.junit.jupiter.api.extension.*

public class AtomicCounterExtension : AfterEachCallback {

  private val index = atomic(0)
  private val finished = atomic(false)

  public fun expect(expectedIndex: Int) {

    val actualIndex = index.incrementAndGet()

    assert(expectedIndex == actualIndex) {
      "Expecting action index $expectedIndex but it is actually $actualIndex"
    }
  }

  public fun finish(expectedIndex: Int) {
    expect(expectedIndex)
    assert(!finished.getAndSet(true)) { "Should call 'finish(...)' at most once" }
  }

  override fun afterEach(context: ExtensionContext?) {
    resetIndex()
  }

  public fun resetIndex() {
    assert(index.value == 0) { "Expecting that 'finish(...)' was invoked, but it was not" }
    index.value = 0
    finished.value = false
  }
}
