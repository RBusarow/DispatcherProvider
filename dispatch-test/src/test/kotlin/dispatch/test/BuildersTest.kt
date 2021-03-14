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

package dispatch.test

import dispatch.core.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import kotlin.coroutines.*

@Suppress("HardCodedDispatcher")
@ExperimentalCoroutinesApi
internal class BuildersTest {

  val testProvider = object : DispatcherProvider {
    override val default: CoroutineDispatcher = TestCoroutineDispatcher()
    override val io: CoroutineDispatcher = TestCoroutineDispatcher()
    override val main: CoroutineDispatcher = TestCoroutineDispatcher()
    override val mainImmediate: CoroutineDispatcher = TestCoroutineDispatcher()
    override val unconfined: CoroutineDispatcher = TestCoroutineDispatcher()
  }

  @Nested
  inner class `run blocking provided` {

    @Test
    fun `default CoroutineContext param should create scope with TestDispatcherProvider`() =
      runBlockingProvided {

        val dispatcherProvider = coroutineContext.dispatcherProvider

        dispatcherProvider.shouldBeTypeOf<TestDispatcherProvider>()
      }

    @Test
    fun `existing DispatcherProvider in CoroutineContext param should be used in new CoroutineContext`() =
      runBlockingProvided(testProvider) {

        val dispatcherProvider = coroutineContext.dispatcherProvider

        dispatcherProvider shouldBe testProvider
      }

    @Test
    fun `existing unique CoroutineContext elements should be used in CoroutineContext`() {

      val ctx = TestCoroutineContext()

      runBlockingProvided(ctx) {

        coroutineContext[TestCoroutineContext] shouldBe ctx
      }
    }

    @Test
    fun `CoroutineContext param without DispatcherProvider should create provider which delegates to Dispatchers`() =

      runBlockingProvided {

        val provider = dispatcherProvider

        provider.default shouldBe Dispatchers.Default
        provider.io shouldBe Dispatchers.IO
        provider.unconfined shouldBe Dispatchers.Unconfined

        provider.main shouldBe provider.mainImmediate

        provider.main shouldNotBe Dispatchers.Main
      }
  }

  @Nested
  inner class `run blocking test provided` {

    @Test
    fun `default params should create scope with TestDispatcherProvider`() =
      testProvided {

        val dispatcherProvider = coroutineContext.dispatcherProvider

        dispatcherProvider.shouldBeTypeOf<TestDispatcherProvider>()
      }

    @Test
    fun `specified provider param should be used in CoroutineContext`() =
      testProvided(testProvider) {

        val dispatcherProvider = coroutineContext.dispatcherProvider

        dispatcherProvider shouldBe testProvider
      }

    @Test
    fun `specified context param should be used in CoroutineContext`() {

      val ctx = TestCoroutineContext()

      testProvided(ctx) {

        coroutineContext[TestCoroutineContext] shouldBe ctx
      }
    }

    @Test
    fun `CoroutineScope receiver should be TestProvidedCoroutineScope`() = testProvided {

      this.shouldBeInstanceOf<TestProvidedCoroutineScope>()
    }

    @Test
    fun `CoroutineScope receiver should be TestCoroutineScope`() = testProvided {

      this.shouldBeInstanceOf<TestCoroutineScope>()
    }

    @Test
    fun `new CoroutineContext should share delay control across all dispatchers`() =
      testProvided {
        launchMain {
          delay(1000)
        }
        advanceTimeBy(1000)
      }

    @Test
    fun `new CoroutineContext should have the same TestCoroutineDispatcher as the DispatcherProvider`() =
      testProvided {

        val dispatcher = coroutineContext[ContinuationInterceptor]

        dispatcher shouldBe dispatcherProvider.default
        dispatcher shouldBe dispatcherProvider.io
        dispatcher shouldBe dispatcherProvider.main
        dispatcher shouldBe dispatcherProvider.mainImmediate
        dispatcher shouldBe dispatcherProvider.unconfined
      }
  }

  class TestCoroutineContext : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = Key

    companion object Key : CoroutineContext.Key<TestCoroutineContext>
  }
}
