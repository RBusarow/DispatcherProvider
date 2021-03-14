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

package dispatch.android.lifecycle

import androidx.lifecycle.*
import dispatch.android.lifecycle.internal.*
import dispatch.internal.test.*
import dispatch.internal.test.android.*
import dispatch.test.*
import hermit.test.*
import hermit.test.junit.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import java.util.concurrent.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
internal class LifecycleScopeExtensionTest :
  HermitJUnit5(),
  LiveDataTest {

  val storeMap: MutableMap<Lifecycle, DispatchLifecycleScope> =
    DispatchLifecycleScopeStore.getPrivateObjectFieldByName("map")

  @BeforeEach
  fun beforeEach() {

    val dispatcher = TestCoroutineDispatcher()

    LifecycleScopeFactory.set { dispatcher + TestDispatcherProvider(dispatcher) + Job() }
  }

  @AfterEach
  fun afterEach() {
    LifecycleScopeFactory.reset()
    storeMap.clear()
  }

  @Nested
  inner class `lifecycle is already destroyed` {

    val lifecycleOwner by resets { FakeLifecycleOwner(initialState = Lifecycle.State.DESTROYED) }

    @Nested
    inner class `scope is created` {

      val scope by resets { lifecycleOwner.dispatchLifecycleScope }

      @Test
      fun `scope and job should already be cancelled`() = runBlocking {

        scope.isActive shouldBe false
        scope.coroutineContext[Job]!!.isActive shouldBe false
      }

      @Test
      fun `scope should not be cached`() {

        storeMap[lifecycleOwner.lifecycle] shouldBe null
      }

      // This is a change detector.
      // This isn't actually desired, but the precondition should be impossible.
      @Test
      fun `additional scopes should be unique`() {

        lifecycleOwner.dispatchLifecycleScope shouldNotBe scope
      }
    }
  }

  @Nested
  inner class `initial lifecycle of Lifecycle State INITIALIZED` {

    val lifecycleOwner by resets { FakeLifecycleOwner(initialState = Lifecycle.State.INITIALIZED) }

    @Nested
    inner class `scope is created` {

      val scope by resets { lifecycleOwner.dispatchLifecycleScope }

      @Test
      fun `scope and job should be active`() {

        scope.isActive shouldBe true
      }

      @Test
      fun `scope should be cached`() {

        scope

        storeMap[lifecycleOwner.lifecycle] shouldBe scope
      }

      @Test
      fun `repeated access should return the same scope`() {

        lifecycleOwner.dispatchLifecycleScope shouldBe scope
      }

      @Nested
      inner class `lifecycle passes to destroyed` {

        @BeforeEach
        fun beforeEach() {
          // special case for Initialized state
          lifecycleOwner.create()

          lifecycleOwner.destroy()
        }

        @Test
        fun `scope should be cancelled`() {

          scope.isActive shouldBe false
        }

        @Test
        fun `scope should be removed from the cache`() {

          storeMap[lifecycleOwner.lifecycle] shouldBe null
        }
      }
    }

    @Nested
    inner class `multiple threads access lifecycleScope at once` {

      @Test
      fun `all threads should get the same instance`() = runBlocking {

        val hugeExecutor = ThreadPoolExecutor(
          200, 200, 5000, TimeUnit.MILLISECONDS, LinkedBlockingQueue()
        )

        val dispatcher = hugeExecutor.asCoroutineDispatcher()

        val lock = CompletableDeferred<Unit>()

        val all = List(200) {
          async(dispatcher) {
            lock.await()
            lifecycleOwner.dispatchLifecycleScope
          }
        }

        yield()
        lock.complete(Unit)

        all.awaitAll().distinct() shouldBe listOf(lifecycleOwner.dispatchLifecycleScope)
      }
    }
  }

  @Nested
  inner class `initial lifecycle of CREATED` {

    val lifecycleOwner by resets { FakeLifecycleOwner(initialState = Lifecycle.State.CREATED) }

    @Nested
    inner class `scope is created` {

      val scope by resets { lifecycleOwner.dispatchLifecycleScope }

      @Test
      fun `scope and job should be active`() {

        scope.isActive shouldBe true
      }

      @Test
      fun `scope should be cached`() {

        scope

        storeMap[lifecycleOwner.lifecycle] shouldBe scope
      }

      @Test
      fun `repeated access should return the same scope`() {

        lifecycleOwner.dispatchLifecycleScope shouldBe scope
      }

      @Nested
      inner class `lifecycle passes to destroyed` {

        @BeforeEach
        fun beforeEach() {
          lifecycleOwner.destroy()
        }

        @Test
        fun `scope should be cancelled`() {

          scope.isActive shouldBe false
        }

        @Test
        fun `scope should be removed from the cache`() {

          storeMap[lifecycleOwner.lifecycle] shouldBe null
        }
      }
    }

    @Nested
    inner class `multiple threads access lifecycleScope at once` {

      @Test
      fun `all threads should get the same instance`() = runBlocking {

        val hugeExecutor = ThreadPoolExecutor(
          200, 200, 5000, TimeUnit.MILLISECONDS, LinkedBlockingQueue()
        )

        val dispatcher = hugeExecutor.asCoroutineDispatcher()

        val lock = CompletableDeferred<Unit>()

        val all = List(200) {
          async(dispatcher) {
            lock.await()
            lifecycleOwner.dispatchLifecycleScope
          }
        }

        yield()
        lock.complete(Unit)

        all.awaitAll().distinct() shouldBe listOf(lifecycleOwner.dispatchLifecycleScope)
      }
    }
  }

  @Nested
  inner class `initial lifecycle of STARTED` {

    val lifecycleOwner by resets { FakeLifecycleOwner(initialState = Lifecycle.State.STARTED) }

    @Nested
    inner class `scope is created` {

      val scope by resets { lifecycleOwner.dispatchLifecycleScope }

      @Test
      fun `scope and job should be active`() {

        scope.isActive shouldBe true
      }

      @Test
      fun `scope should be cached`() {

        scope

        storeMap[lifecycleOwner.lifecycle] shouldBe scope
      }

      @Test
      fun `repeated access should return the same scope`() {

        lifecycleOwner.dispatchLifecycleScope shouldBe scope
      }

      @Nested
      inner class `lifecycle passes to destroyed` {

        @BeforeEach
        fun beforeEach() {
          lifecycleOwner.destroy()
        }

        @Test
        fun `scope should be cancelled`() {

          scope.isActive shouldBe false
        }

        @Test
        fun `scope should be removed from the cache`() {

          storeMap[lifecycleOwner.lifecycle] shouldBe null
        }
      }
    }

    @Nested
    inner class `multiple threads access lifecycleScope at once` {

      @Test
      fun `all threads should get the same instance`() = runBlocking {

        val hugeExecutor = ThreadPoolExecutor(
          200, 200, 5000, TimeUnit.MILLISECONDS, LinkedBlockingQueue()
        )

        val dispatcher = hugeExecutor.asCoroutineDispatcher()

        val lock = CompletableDeferred<Unit>()

        val all = List(200) {
          async(dispatcher) {
            lock.await()
            lifecycleOwner.dispatchLifecycleScope
          }
        }

        yield()
        lock.complete(Unit)

        all.awaitAll().distinct() shouldBe listOf(lifecycleOwner.dispatchLifecycleScope)
      }
    }
  }

  @Nested
  inner class `initial lifecycle of RESUMED` {

    val lifecycleOwner by resets { FakeLifecycleOwner(initialState = Lifecycle.State.RESUMED) }

    @Nested
    inner class `scope is created` {

      val scope by resets { lifecycleOwner.dispatchLifecycleScope }

      @Test
      fun `scope and job should be active`() {

        scope.isActive shouldBe true
      }

      @Test
      fun `scope should be cached`() {

        scope

        storeMap[lifecycleOwner.lifecycle] shouldBe scope
      }

      @Test
      fun `repeated access should return the same scope`() {

        lifecycleOwner.dispatchLifecycleScope shouldBe scope
      }

      @Nested
      inner class `lifecycle passes to destroyed` {

        @BeforeEach
        fun beforeEach() {

          lifecycleOwner.destroy()
        }

        @Test
        fun `scope should be cancelled`() {

          scope.isActive shouldBe false
        }

        @Test
        fun `scope should be removed from the cache`() {

          storeMap[lifecycleOwner.lifecycle] shouldBe null
        }
      }
    }

    @Nested
    inner class `multiple threads access lifecycleScope at once` {

      @Test
      fun `all threads should get the same instance`() = runBlocking {

        val hugeExecutor = ThreadPoolExecutor(
          200, 200, 5000, TimeUnit.MILLISECONDS, LinkedBlockingQueue()
        )

        val dispatcher = hugeExecutor.asCoroutineDispatcher()

        val lock = CompletableDeferred<Unit>()

        val all = List(200) {
          async(dispatcher) {
            lock.await()
            lifecycleOwner.dispatchLifecycleScope
          }
        }

        yield()
        lock.complete(Unit)

        all.awaitAll().distinct() shouldBe listOf(lifecycleOwner.dispatchLifecycleScope)
      }
    }
  }
}
