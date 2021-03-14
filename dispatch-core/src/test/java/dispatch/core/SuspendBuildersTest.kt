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

import dispatch.internal.test.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import kotlin.coroutines.*

@ExperimentalCoroutinesApi
internal class SuspendBuildersTest {

  val testProvider = object : DispatcherProvider {
    override val default: CoroutineDispatcher = TestCoroutineDispatcher()
    override val io: CoroutineDispatcher = TestCoroutineDispatcher()
    override val main: CoroutineDispatcher = TestCoroutineDispatcher()
    override val mainImmediate: CoroutineDispatcher = TestCoroutineDispatcher()
    override val unconfined: CoroutineDispatcher = TestCoroutineDispatcher()
  }

  @Nested
  inner class `with default` {

    @Test
    fun `empty context arg should use receiver context with provided default dispatcher`() =
      runBlockingProvided {

        val ctx: CoroutineContext = coroutineContext + testProvider.default

        withDefault { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should not override specified provider dispatcher`() = runBlockingProvided {

      val job = Job()
      val ctx: CoroutineContext = coroutineContext + testProvider.default

      withDefault(testProvider.io + job) {
        coroutineContext shouldEqualFolded ctx + job
      }
    }
  }

  @Nested
  inner class `with io` {

    @Test
    fun `empty context arg should use receiver context with provided io dispatcher`() =
      runBlockingProvided {

        val ctx: CoroutineContext = coroutineContext + testProvider.io

        withIO { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should not override specified provider dispatcher`() = runBlockingProvided {

      val job = Job()
      val ctx: CoroutineContext = coroutineContext + testProvider.io

      withIO(testProvider.default + job) {
        coroutineContext shouldEqualFolded ctx + job
      }
    }
  }

  @Nested
  inner class `with main` {

    @Test
    fun `empty context arg should use receiver context with provided main dispatcher`() =
      runBlockingProvided {

        val ctx: CoroutineContext = coroutineContext + testProvider.main

        withMain { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should not override specified provider dispatcher`() = runBlockingProvided {

      val job = Job()
      val ctx: CoroutineContext = coroutineContext + testProvider.main

      withMain(testProvider.default + job) {
        coroutineContext shouldEqualFolded ctx + job
      }
    }
  }

  @Nested
  inner class `with main immediate` {

    @Test
    fun `empty context arg should use receiver context with provided mainImmediate dispatcher`() =
      runBlockingProvided {

        val ctx: CoroutineContext = coroutineContext + testProvider.mainImmediate

        withMainImmediate { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should not override specified provider dispatcher`() = runBlockingProvided {

      val job = Job()
      val ctx: CoroutineContext = coroutineContext + testProvider.mainImmediate

      withMainImmediate(testProvider.default + job) {
        coroutineContext shouldEqualFolded ctx + job
      }
    }
  }

  @Nested
  inner class `with unconfined` {

    @Test
    fun `empty context arg should use receiver context with provided unconfined dispatcher`() =
      runBlockingProvided {

        val ctx: CoroutineContext = coroutineContext + testProvider.unconfined

        withUnconfined { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should not override specified provider dispatcher`() = runBlockingProvided {

      val job = Job()
      val ctx: CoroutineContext = coroutineContext + testProvider.unconfined

      withUnconfined(testProvider.default + job) {
        coroutineContext shouldEqualFolded ctx + job
      }
    }
  }

  fun runBlockingProvided(
    block: suspend CoroutineScope.() -> Unit
  ) = runBlocking(testProvider, block)
}
