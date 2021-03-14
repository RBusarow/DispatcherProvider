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

import androidx.arch.core.executor.testing.*
import dispatch.core.*
import dispatch.internal.test.*
import dispatch.internal.test.android.*
import dispatch.test.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.annotation.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@ExperimentalCoroutinesApi
internal class WithViewLifecycleTest {

  @JvmField
  @Rule
  val rule = TestCoroutineRule()

  @JvmField
  @Rule
  val instantTaskRule = InstantTaskExecutorRule()

  val fragmentLifecycleOwner = FakeLifecycleOwner(mainDispatcher = rule.dispatcherProvider.main)
  val viewLifecycleOwner = FakeLifecycleOwner(mainDispatcher = rule.dispatcherProvider.main)

  @Before
  fun setUp() {
    fragmentLifecycleOwner.start()
    viewLifecycleOwner.create()
  }

  @Test
  fun `when the livedata goes from null to non-null, lambda should be invoked`() {

    var invocations = 0

    val fragment = FakeFragment(fragmentLifecycleOwner)

    rule.withViewLifecycle(fragment) { invocations++ }

    invocations shouldBe 0

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    invocations shouldBe 1
  }

  @Test
  fun `when the livedata goes from null to non-null, lambda should be invoked every time`() {

    var invocations = 0

    val fragment = FakeFragment(fragmentLifecycleOwner)

    rule.withViewLifecycle(fragment) { invocations++ }

    invocations shouldBe 0

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    invocations shouldBe 1

    fragment.setFakeViewLifecycleOwner(null)
    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    invocations shouldBe 2

    fragment.setFakeViewLifecycleOwner(null)
    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    invocations shouldBe 3
  }

  @Test
  fun `when the livedata gets set to non-null, lambda should be invoked every time`() {

    var invocations = 0

    val fragment = FakeFragment(fragmentLifecycleOwner)

    rule.withViewLifecycle(fragment) { invocations++ }

    invocations shouldBe 0

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    invocations shouldBe 1

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    invocations shouldBe 2

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    invocations shouldBe 3
  }

  @Test
  fun `when the view lifecycle is destroyed, lambda should be cancelled`() {

    lateinit var job: Job

    val fragment = FakeFragment(fragmentLifecycleOwner)

    rule.withViewLifecycle(fragment) {
      job = launch {
        // never completes, so the Job should be active until the scope is destroyed
        CompletableDeferred<Int>().await()
      }
    }

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    viewLifecycleOwner.create()
    viewLifecycleOwner.destroy()

    job.isCancelled shouldBe true
  }

  @Test
  fun `when the fragment lifecycle is destroyed, lambda be cancelled`() {

    lateinit var job: Job

    val fragment = FakeFragment(fragmentLifecycleOwner)

    rule.withViewLifecycle(fragment) {
      job = launch {
        // never completes, so the Job should be active until the scope is destroyed
        CompletableDeferred<Int>().await()
      }
    }

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    fragmentLifecycleOwner.create()
    fragmentLifecycleOwner.destroy()

    job.isActive shouldBe false
  }

  @Test
  fun `the lambda's LifecycleScope should correspond to the view lifecycle`() = runBlocking {

    lateinit var job: Job

    val fragment = FakeFragment(fragmentLifecycleOwner)

    rule.withViewLifecycle(fragment) {
      job = launch {

        lifecycle shouldBeSameInstanceAs viewLifecycleOwner.lifecycle
      }
    }

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    job.join()
  }

  @Test
  fun `receiver CoroutineScope should not be cancelled when Fragment's lifecycle is destroyed`() =
    runBlocking {

      /*
      This extension creates a ViewLifecycleScope without knowing where the receiver CoroutineScope came from.

      It creates a child scope from the receiver, and that scope is automatically cancelled when the Fragment is destroyed,
      but the source scope should be unchanged.
       */

      val fragment = FakeFragment(fragmentLifecycleOwner)

      val receiverScope =
        MainImmediateCoroutineScope(Job() + rule.coroutineContext)

      var internalScope: CoroutineScope? = null

      receiverScope.withViewLifecycle(fragment) {
        launch {

          internalScope = this

          lifecycle shouldBeSameInstanceAs viewLifecycleOwner.lifecycle
        }
      }

      fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

      viewLifecycleOwner.destroy()
      fragmentLifecycleOwner.destroy()
      internalScope!!.coroutineContext[Job]!!.isActive shouldBe false

      receiverScope.coroutineContext[Job]!!.isActive shouldBe true
    }

  @Test
  fun `receiver CoroutineScope and View scope contexts should be identical except for the Job`() =
    runBlocking {

      val fragment = FakeFragment(fragmentLifecycleOwner)

      val receiverScope =
        MainImmediateCoroutineScope(Job() + rule.coroutineContext)

      var internalScope: CoroutineScope? = null

      receiverScope.withViewLifecycle(fragment) {
        launch {

          internalScope = this

          lifecycle shouldBeSameInstanceAs viewLifecycleOwner.lifecycle
        }
      }

      fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

      viewLifecycleOwner.destroy()
      fragmentLifecycleOwner.destroy()

      val receiverContext = receiverScope.coroutineContext

      val internalContext = internalScope!!.coroutineContext

      val internalJob = internalContext[Job]!!

      receiverContext shouldNotEqualFolded internalContext

      receiverContext + internalJob shouldEqualFolded internalContext
    }
}
