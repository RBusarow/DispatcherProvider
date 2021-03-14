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

import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.*
import java.util.*
import java.util.concurrent.atomic.*
import kotlin.coroutines.*

@ExperimentalCoroutinesApi
internal class FlowTest {

  val dispatcherStack = LinkedList<RecordingDispatcher>()
  val dispatchCount = AtomicInteger(0)

  var originDispatcher: RecordingDispatcher? = null
  var delegatedDispatcher: RecordingDispatcher? = null

  lateinit var originalDispatcher: RecordingDispatcher
  lateinit var originalScope: CoroutineScope

  lateinit var testProvider: DispatcherProvider

  @BeforeEach
  fun beforeEach() {

    originalDispatcher = RecordingDispatcher("origin")

    testProvider = object : DispatcherProvider {

      override val default: CoroutineDispatcher = RecordingDispatcher("default")
      override val io: CoroutineDispatcher = RecordingDispatcher("io")
      override val main: CoroutineDispatcher = RecordingDispatcher("main")
      override val mainImmediate: CoroutineDispatcher = RecordingDispatcher("mainImmediate")
      override val unconfined: CoroutineDispatcher = RecordingDispatcher("unconfined")
    }

    originalScope = CoroutineScope(originalDispatcher + testProvider)
  }

  @AfterEach
  fun afterEach() {

    dispatcherStack.clear()
    dispatchCount.set(0)

    delegatedDispatcher = null
    originDispatcher = null

    originalScope.cancel()
  }

  @Test
  fun `flow on default should use provided default dispatcher`() = runTest {

    flowOf(1).onEach {
      delegatedDispatcher = dispatcherStack.pop()
    }
      .flowOnDefault()
      .onStart {
        originDispatcher = dispatcherStack.pop()
      }
      .launchIn(originalScope)

    delegatedDispatcher shouldBe originalScope.dispatcherProvider.default
    originDispatcher shouldBe originalDispatcher
  }

  @Test
  fun `flow on io should use provided io dispatcher`() = runTest {

    flowOf(1).onEach {
      delegatedDispatcher = dispatcherStack.pop()
    }
      .flowOnIO()
      .onStart {
        originDispatcher = dispatcherStack.pop()
      }
      .launchIn(originalScope)

    delegatedDispatcher shouldBe originalScope.dispatcherProvider.io
    originDispatcher shouldBe originalDispatcher
  }

  @Test
  fun `flow on main should use provided main dispatcher`() = runTest {

    flowOf(1).onEach {
      delegatedDispatcher = dispatcherStack.pop()
    }
      .flowOnMain()
      .onStart {
        originDispatcher = dispatcherStack.pop()
      }
      .launchIn(originalScope)

    delegatedDispatcher shouldBe originalScope.dispatcherProvider.main
    originDispatcher shouldBe originalDispatcher
  }

  @Test
  fun `flow on mainImmediate should use provided mainImmediate dispatcher`() = runTest {

    flowOf(1).onEach {
      delegatedDispatcher = dispatcherStack.pop()
    }
      .flowOnMainImmediate()
      .onStart {
        originDispatcher = dispatcherStack.pop()
      }
      .launchIn(originalScope)

    delegatedDispatcher shouldBe originalScope.dispatcherProvider.mainImmediate
    originDispatcher shouldBe originalDispatcher
  }

  @Test
  fun `flow on unconfined should use provided unconfined dispatcher`() = runTest {

    flowOf(1).onEach {
      delegatedDispatcher = dispatcherStack.pop()
    }
      .flowOnUnconfined()
      .onStart {
        originDispatcher = dispatcherStack.pop()
      }
      .launchIn(originalScope)

    delegatedDispatcher shouldBe originalScope.dispatcherProvider.unconfined
    originDispatcher shouldBe originalDispatcher
  }

  @Test
  fun `multi element flows should only switch dispatchers once`() = runTest {

    flowOf(1, 2, 3, 4, 5, 6)
      .flowOnUnconfined()
      .launchIn(originalScope)

    dispatchCount.get() shouldBe 2 // original dispatch + unconfined
  }

  @Test
  fun `flowOn should not dispatch if origin dispatcher and new are the same`() = runTest {

    flowOf(1)
      .flowOnDefault()
      .launchIn(originalScope + testProvider.default)

    dispatchCount.get() shouldBe 1
  }

  @Test
  fun `flowOn output should fuse context with ChannelFlow`() = runTest {

    flowOf(1)
      .flowOn(testProvider.default)
      .flowOnDefault()
      .flowOn(testProvider.default)
      .launchIn(originalScope + testProvider.default)

    dispatchCount.get() shouldBe 1 // ChannelFlow does not re-dispatch for the same dispatcher

    dispatchCount.set(0)

    flowOf(1)
      .flowOn(testProvider.io)
      .flowOnDefault()
      .flowOn(testProvider.main)
      .launchIn(originalScope + testProvider.default)

    dispatchCount.get() shouldBe 3 // sanity check -- switching should increment the count
  }

  @Test
  fun `provider flowOn should buffer like a normal flowOn would`() = runTest {

    val sourceProgress = mutableListOf<Int>()

    flowOf(1, 2, 3, 4, 5)
      .onEach { sourceProgress.add(it) }
      .flowOnDefault()
      .take(1)
      .collect()

    sourceProgress shouldBe listOf(1, 2, 3, 4, 5)
  }

  inner class RecordingDispatcher(private val name: String) : CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {

      dispatchCount.incrementAndGet()

      dispatcherStack.push(this)
      block.run()
    }

    override fun toString(): String = "RecordingDispatcher ($name)"
  }

  private fun runTest(block: suspend CoroutineScope.() -> Unit) = runBlocking { block() }
}
