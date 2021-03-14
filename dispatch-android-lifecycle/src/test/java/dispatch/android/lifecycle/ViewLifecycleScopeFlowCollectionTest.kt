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
import dispatch.internal.test.android.*
import dispatch.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.annotation.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@ExperimentalCoroutinesApi
internal class ViewLifecycleScopeFlowCollectionTest {

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
  fun `launchOnCreate collection should only happen while at least CREATED`() = runBlocking {

    val fragment = FakeFragment(fragmentLifecycleOwner)

    val flow = MutableStateFlow(0)
    val collected = mutableListOf<Int>()

    rule.withViewLifecycle(fragment) {
      flow.onEach { collected.add(it) }.launchOnCreate()
    }

    collected shouldBe listOf<Int>()

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    viewLifecycleOwner.create()

    collected shouldBe listOf(0)

    flow.value = 1

    collected shouldBe listOf(0, 1)

    viewLifecycleOwner.destroy()

    flow.value = 2

    collected shouldBe listOf(0, 1)
  }

  @Test
  fun `launchOnStart collection should only happen while at least STARTED`() = runBlocking {

    val fragment = FakeFragment(fragmentLifecycleOwner)

    val flow = MutableStateFlow(0)
    val collected = mutableListOf<Int>()

    rule.withViewLifecycle(fragment) {
      flow.onEach { collected.add(it) }
        .launchOnStart()
    }

    collected shouldBe listOf<Int>()

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    viewLifecycleOwner.start()

    collected shouldBe listOf(0)

    flow.value = 1

    collected shouldBe listOf(0, 1)

    viewLifecycleOwner.stop()

    flow.value = 2

    collected shouldBe listOf(0, 1)
  }

  @Test
  fun `launchOnResume collection should only happen while RESUMED`() = runBlocking {

    val fragment = FakeFragment(fragmentLifecycleOwner)

    val flow = MutableStateFlow(0)
    val collected = mutableListOf<Int>()

    rule.withViewLifecycle(fragment) {
      flow.onEach { collected.add(it) }.launchOnResume()
    }

    collected shouldBe listOf<Int>()

    fragment.setFakeViewLifecycleOwner(viewLifecycleOwner)

    viewLifecycleOwner.resume()

    collected shouldBe listOf(0)

    flow.value = 1

    collected shouldBe listOf(0, 1)

    viewLifecycleOwner.pause()

    flow.value = 2

    collected shouldBe listOf(0, 1)
  }
}
