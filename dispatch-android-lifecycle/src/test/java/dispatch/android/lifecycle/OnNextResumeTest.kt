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
import dispatch.core.*
import dispatch.internal.test.*
import dispatch.internal.test.android.*
import dispatch.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import org.junit.jupiter.api.*
import kotlin.coroutines.*

@FlowPreview
@ExperimentalCoroutinesApi
class OnNextResumeTest :
  BaseTest(),
  LiveDataTest {

  lateinit var lifecycleOwner: LifecycleOwner
  lateinit var lifecycle: LifecycleRegistry

  @BeforeEach
  fun beforeEach() {

    lifecycleOwner = LifecycleOwner { lifecycle }
    lifecycle = LifecycleRegistry(lifecycleOwner)
  }

  @Nested
  inner class `Lifecycle version` {

    @Test
    fun `block should immediately execute if already resumed`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      var executed = false

      launch { lifecycle.onNextResume { executed = true } }

      executed shouldBe true
    }

    @Test
    fun `block should not immediately execute if lifecycle is not resumed`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

      var executed = false

      val job = launch { lifecycle.onNextResume { executed = true } }

      executed shouldBe false

      job.cancelAndJoin()
    }

    @Test
    fun `block should pause when lifecycle is destroyed`() = testProvided {

      val input = Channel<Int>()
      val output = mutableListOf<Int>()
      var completed = false

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      launch {
        lifecycle.onNextResume {
          input.consumeAsFlow()
            .onCompletion { completed = true }
            .collect { output.add(it) }
        }
      }

      input.send(1)
      input.send(2)
      input.send(3)

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

      output shouldBe listOf(1, 2, 3)
      completed shouldBe true
    }

    @Test
    fun `block should not execute twice when lifecycle is resumed twice`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      lifecycle.onNextResume { expect(1) }

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

      expect(2)

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      finish(3)
    }

    @Test
    fun `block should return value if allowed to complete`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      val result = lifecycle.onNextResume { true }

      result shouldBe true
    }

    @Test
    fun `block should return null if not allowed to complete`() = testProvided {

      val lock = Mutex(locked = true)

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      val resultDeferred = async {
        lifecycle.onNextResume {
          lock.withLock {
            // unreachable
            true
          }
        }
      }

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

      resultDeferred.await() shouldBe null
    }

    @Test
    fun `block context should respect context parameter`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      var dispatcher: ContinuationInterceptor? = null

      lifecycle.onNextResume(ioDispatcher) {
        dispatcher = coroutineContext[ContinuationInterceptor]
      }

      dispatcher shouldBe ioDispatcher
    }
  }

  @Nested
  inner class `LifecycleOwner version` {

    @Test
    fun `block should immediately execute if already resumed`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      var executed = false

      launch { lifecycleOwner.onNextResume { executed = true } }

      executed shouldBe true
    }

    @Test
    fun `block should not immediately execute if lifecycle is not resumed`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

      var executed = false

      val job = launch { lifecycleOwner.onNextResume { executed = true } }

      executed shouldBe false

      job.cancelAndJoin()
    }

    @Test
    fun `block should pause when lifecycle is destroyed`() = testProvided {

      val input = Channel<Int>()
      val output = mutableListOf<Int>()
      var completed = false

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      launch {
        lifecycleOwner.onNextResume {
          input.consumeAsFlow()
            .onCompletion { completed = true }
            .collect { output.add(it) }
        }
      }

      input.send(1)
      input.send(2)
      input.send(3)

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

      output shouldBe listOf(1, 2, 3)
      completed shouldBe true
    }

    @Test
    fun `block should not execute twice when lifecycle is resumed twice`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      lifecycleOwner.onNextResume { expect(1) }

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

      expect(2)

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      finish(3)
    }

    @Test
    fun `block should return value if allowed to complete`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      val result = lifecycleOwner.onNextResume { true }

      result shouldBe true
    }

    @Test
    fun `block should return null if not allowed to complete`() = testProvided {

      val lock = Mutex(locked = true)

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      val resultDeferred = async {
        lifecycleOwner.onNextResume {
          lock.withLock {
            // unreachable
            true
          }
        }
      }

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

      resultDeferred.await() shouldBe null
    }

    @Test
    fun `block context should respect context parameter`() = testProvided {

      lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

      var dispatcher: ContinuationInterceptor? = null

      lifecycleOwner.onNextResume(ioDispatcher) {
        dispatcher = coroutineContext[ContinuationInterceptor]
      }

      dispatcher shouldBe ioDispatcher
    }
  }
}
