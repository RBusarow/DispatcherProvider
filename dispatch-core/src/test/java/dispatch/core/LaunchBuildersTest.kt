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
internal class LaunchBuildersTest {

  val testProvider = object : DispatcherProvider {
    override val default: CoroutineDispatcher = TestCoroutineDispatcher()
    override val io: CoroutineDispatcher = TestCoroutineDispatcher()
    override val main: CoroutineDispatcher = TestCoroutineDispatcher()
    override val mainImmediate: CoroutineDispatcher = TestCoroutineDispatcher()
    override val unconfined: CoroutineDispatcher = TestCoroutineDispatcher()
  }

  @Nested
  inner class `launch default` {

    @Test
    fun `empty context arg should use receiver context with provided default dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val ctx: CoroutineContext = coroutineContext + testProvider.default

        launchDefault { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should override specified provider dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val job = Job()
        val ctx: CoroutineContext = coroutineContext + testProvider.default

        launchDefault(testProvider.io + job) {
          coroutineContext shouldEqualFolded ctx + testProvider.io + job
        }
      }
  }

  @Nested
  inner class `launch io` {

    @Test
    fun `empty context arg should use receiver context with provided io dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val ctx: CoroutineContext = coroutineContext + testProvider.io

        launchIO { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should override specified provider dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val job = Job()
        val ctx: CoroutineContext = coroutineContext + testProvider.io

        launchIO(testProvider.default + job) {
          coroutineContext shouldEqualFolded ctx + testProvider.default + job
        }
      }
  }

  @Nested
  inner class `launch main` {

    @Test
    fun `empty context arg should use receiver context with provided main dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val ctx: CoroutineContext = coroutineContext + testProvider.main

        launchMain { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should override specified provider dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val job = Job()
        val ctx: CoroutineContext = coroutineContext + testProvider.main

        launchMain(testProvider.io + job) {
          coroutineContext shouldEqualFolded ctx + testProvider.io + job
        }
      }
  }

  @Nested
  inner class `launch main immediate` {

    @Test
    fun `empty context arg should use receiver context with provided mainImmediate dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val ctx: CoroutineContext = coroutineContext + testProvider.mainImmediate

        launchMainImmediate { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should override specified provider dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val job = Job()
        val ctx: CoroutineContext = coroutineContext + testProvider.mainImmediate

        launchMainImmediate(testProvider.io + job) {
          coroutineContext shouldEqualFolded ctx + testProvider.io + job
        }
      }
  }

  @Nested
  inner class `launch unconfined` {

    @Test
    fun `empty context arg should use receiver context with provided mainImmediate dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val ctx: CoroutineContext = coroutineContext + testProvider.unconfined

        launchUnconfined { coroutineContext shouldEqualFolded ctx }
      }

    @Test
    fun `context should override specified provider dispatcher`() =
      runBlocking<Unit>(testProvider) {

        val job = Job()
        val ctx: CoroutineContext = coroutineContext + testProvider.unconfined

        launchUnconfined(testProvider.io + job) {
          coroutineContext shouldEqualFolded ctx + testProvider.io + job
        }
      }
  }
}
