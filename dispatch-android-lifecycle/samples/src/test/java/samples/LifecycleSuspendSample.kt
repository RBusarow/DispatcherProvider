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

package samples

import dispatch.android.lifecycle.*
import dispatch.core.*
import dispatch.internal.test.android.*
import dispatch.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*

@CoroutineTest
@ExperimentalCoroutinesApi
class LifecycleSuspendSample(
  val testScope: TestProvidedCoroutineScope
) : LiveDataTest {

  @BeforeEach
  fun beforeEach() {
    LifecycleScopeFactory.set { testScope.coroutineContext }
  }

  @Sample
  fun lifecycleOwnerOnNextCreateSample() = runBlocking {

    class SomeFragment : Fragment() {

      var invocations = 0

      init {
        dispatchLifecycleScope.launchMainImmediate {
          onNextCreate { invocations++ }
        }
      }
    }

    // current view lifecycle state is INITIALIZED
    val fragment = SomeFragment()

    // nothing is invoked yet
    fragment.invocations shouldBe 0

    fragment.create()
    fragment.invocations shouldBe 1
  }

  @Sample
  fun lifecycleOnNextCreateSample() = runBlocking {

    class SomeFragment : Fragment() {

      var invocations = 0

      init {
        dispatchLifecycleScope.launchMainImmediate {
          lifecycle.onNextCreate { invocations++ }
        }
      }
    }

    // current lifecycle state is INITIALIZED
    val fragment = SomeFragment()

    // nothing is invoked yet
    fragment.invocations shouldBe 0

    fragment.create()
    fragment.invocations shouldBe 1
  }

  @Sample
  fun lifecycleOwnerOnNextStartSample() = runBlocking {

    class SomeFragment : Fragment() {

      var invocations = 0

      init {
        dispatchLifecycleScope.launchMainImmediate {
          onNextStart { invocations++ }
        }
      }
    }

    // current view lifecycle state is INITIALIZED
    val fragment = SomeFragment()

    // nothing is invoked yet
    fragment.invocations shouldBe 0

    fragment.start()
    fragment.invocations shouldBe 1

    // crossing the threshold doesn't invoke the lambda again
    fragment.stop()
    fragment.start()
    fragment.invocations shouldBe 1
  }

  @Sample
  fun lifecycleOnNextStartSample() = runBlocking {

    class SomeFragment : Fragment() {

      var invocations = 0

      init {
        dispatchLifecycleScope.launchMainImmediate {
          lifecycle.onNextStart { invocations++ }
        }
      }
    }

    // current lifecycle state is INITIALIZED
    val fragment = SomeFragment()

    // nothing is invoked yet
    fragment.invocations shouldBe 0

    fragment.start()
    fragment.invocations shouldBe 1

    // crossing the threshold doesn't invoke the lambda again
    fragment.stop()
    fragment.start()
    fragment.invocations shouldBe 1
  }

  @Sample
  fun lifecycleOwnerOnNextResumeSample() = runBlocking {

    class SomeFragment : Fragment() {

      var invocations = 0

      init {
        dispatchLifecycleScope.launchMainImmediate {
          onNextResume { invocations++ }
        }
      }
    }

    // current view lifecycle state is INITIALIZED
    val fragment = SomeFragment()

    // nothing is invoked yet
    fragment.invocations shouldBe 0

    fragment.resume()
    fragment.invocations shouldBe 1

    // crossing the threshold doesn't invoke the lambda again
    fragment.pause()
    fragment.resume()
    fragment.invocations shouldBe 1
  }

  @Sample
  fun lifecycleOnNextResumeSample() = runBlocking {

    class SomeFragment : Fragment() {

      var invocations = 0

      init {
        dispatchLifecycleScope.launchMainImmediate {
          lifecycle.onNextResume { invocations++ }
        }
      }
    }

    // current lifecycle state is INITIALIZED
    val fragment = SomeFragment()

    // nothing is invoked yet
    fragment.invocations shouldBe 0

    fragment.resume()
    fragment.invocations shouldBe 1

    // crossing the threshold doesn't invoke the lambda again
    fragment.pause()
    fragment.resume()
    fragment.invocations shouldBe 1
  }
}
