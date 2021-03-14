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

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import kotlin.test.*

internal class AssertionsTest {

  @Nested
  inner class `should be SupervisorJob` {

    @Test
    fun `normal Job should fail`() {

      assertFails { Job().shouldBeSupervisorJob() }
    }

    @Test
    fun `SupervisorJob should pass`() {

      SupervisorJob().shouldBeSupervisorJob()
    }
  }

  @Nested
  inner class `should be or child of` {

    @Test
    fun `referential equality should pass`() {

      val job = Job()

      job shouldBeOrChildOf job
    }

    @Test
    fun `receiver job as child of parameter should pass`() = runBlocking a@{

      launch b@{
        this@b.coroutineContext[Job]!! shouldBeOrChildOf this@a.coroutineContext[Job]!!
      }.join()
    }

    @Test
    fun `parameter job as child of receiver should fail`() = runBlocking<Unit> a@{

      launch b@{
        assertFails { this@a.coroutineContext[Job]!! shouldBeOrChildOf this@b.coroutineContext[Job]!! }
      }
    }

    @Test
    fun `unrelated jobs should fail`() {

      val a = Job()
      val b = Job()

      assertFails { a shouldBeOrChildOf b }
    }
  }
}
