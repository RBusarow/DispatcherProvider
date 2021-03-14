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

import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import java.util.concurrent.atomic.*

@Suppress("HardCodedDispatcher")
@ExperimentalCoroutinesApi
internal class TestDispatcherProviderTest {

  @Nested
  inner class `TestCoroutineDispatcher factory` {

    val dispatcher = TestCoroutineDispatcher()

    @Test
    fun `provider arg should be assigned to all properties`() {

      val provider = TestDispatcherProvider(dispatcher)

      provider.default shouldBe dispatcher
      provider.io shouldBe dispatcher
      provider.main shouldBe dispatcher
      provider.mainImmediate shouldBe dispatcher
      provider.unconfined shouldBe dispatcher
    }

    @Test
    fun `factory should create TestDispatcherProvider`() {

      val provider = TestDispatcherProvider(dispatcher)

      provider.shouldBeTypeOf<TestDispatcherProvider>()
    }
  }

  @Nested
  inner class `Basic CoroutineDispatcher factory` {

    @Test
    fun `default property should delegate to Dispatchers_Default`() {

      val provider = TestBasicDispatcherProvider()

      provider.default shouldBe Dispatchers.Default
    }

    @Test
    fun `io property should delegate to Dispatchers_IO`() {

      val provider = TestBasicDispatcherProvider()

      provider.io shouldBe Dispatchers.IO
    }

    @Test
    fun `main and mainImmediate properties should be a single dispatcher`() {

      val provider = TestBasicDispatcherProvider()

      provider.main shouldBe provider.mainImmediate

      val count = AtomicInteger(1)

      runBlocking(provider.main) {

        provider.main.asExecutor()

        count.getAndIncrement() shouldBe 1

        launch(provider.main) {
          count.getAndIncrement() shouldBe 2
        }
        launch(provider.mainImmediate) {
          count.getAndIncrement() shouldBe 3
        }

        // yielding only works because the above launches are already queued for dispatch on the same dispatcher
        yield()
        yield()
        count.getAndIncrement() shouldBe 4
      }
    }

    @Test
    fun `unconfined property should delegate to Dispatchers_Unconfined`() {

      val provider = TestBasicDispatcherProvider()

      provider.unconfined shouldBe Dispatchers.Unconfined
    }

    @Test
    fun `factory should create TestDispatcherProvider`() {

      val provider = TestDispatcherProvider()

      provider.shouldBeTypeOf<TestDispatcherProvider>()
    }
  }
}
