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

import io.kotest.matchers.*
import kotlinx.atomicfu.*
import org.junit.jupiter.api.*

@Suppress("UnnecessaryAbstractClass")
public abstract class BaseTest {

  private val index = atomic(0)
  private val finished = atomic(false)

  @AfterEach
  internal fun _afterEach() {
    resetIndex()
  }

  public fun expect(expectedIndex: Int) {

    val actualIndex = index.incrementAndGet()

    actualIndex shouldBe expectedIndex
  }

  public fun finish(expectedIndex: Int) {
    expect(expectedIndex)
    require(!finished.getAndSet(true)) { "Should call 'finish(...)' at most once" }
  }

  public fun resetIndex() {
    try {
      require(index.value == 0 || finished.value) { "Expecting that 'finish(...)' was invoked, but it was not" }
    } finally {
      index.value = 0
      finished.value = false
    }
  }
}
