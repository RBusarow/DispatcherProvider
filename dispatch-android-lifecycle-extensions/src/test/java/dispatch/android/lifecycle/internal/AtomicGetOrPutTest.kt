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

package dispatch.android.lifecycle.internal

import androidx.lifecycle.*
import dispatch.android.lifecycle.*
import dispatch.core.*
import dispatch.internal.test.android.*
import dispatch.test.*
import hermit.test.junit.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import java.util.concurrent.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
internal class AtomicGetOrPutTest : HermitJUnit5() {

  @Nested
  inner class `API level 23` {

    @Nested
    inner class `multiple threads access lifecycleScope at once` {
      @Test
      fun `all threads should get the same instance`() = runBlocking {

        val main = newSingleThreadContext("main")

        val storeMap = ConcurrentHashMap<Lifecycle, DispatchLifecycleScope>()

        val lifecycleOwner = FakeLifecycleOwner(Lifecycle.State.INITIALIZED)

        val androidLifecycle = lifecycleOwner.lifecycle

        val hugeExecutor = ThreadPoolExecutor(
          200, 200, 5000, TimeUnit.MILLISECONDS, LinkedBlockingQueue()
        ).asCoroutineDispatcher()

        val lock = CompletableDeferred<Unit>()

        val all = List(200) {
          async(hugeExecutor) {

            lock.await()

            storeMap.atomicGetOrPut(androidLifecycle) {
              val scope = DispatchLifecycleScope(
                lifecycle = androidLifecycle,
                coroutineScope = MainImmediateCoroutineScope(Job(), TestDispatcherProvider(main))
              )
              withContext(main) {

                androidLifecycle.addObserver(DispatchLifecycleScopeStore)
              }
              scope
            }
          }
        }

        yield()
        lock.complete(Unit)

        all.awaitAll().distinct().size shouldBe 1

        delay(100)

        androidLifecycle.observerCount shouldBe 2
      }
    }
  }
}
