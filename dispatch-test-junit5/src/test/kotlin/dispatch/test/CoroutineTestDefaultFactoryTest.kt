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

import dispatch.internal.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import kotlin.coroutines.*

@CoroutineTest
@ExperimentalCoroutinesApi
class CoroutineTestDefaultFactoryTest(
  val testScope: TestProvidedCoroutineScope
) {

  @Test
  fun `a no-arg extension should use a default TestProvidedCoroutineScope`() {
    val dispatcherProvider = testScope.dispatcherProvider

    val allDispatchers = setOf(
      dispatcherProvider.default,
      dispatcherProvider.io,
      dispatcherProvider.main,
      dispatcherProvider.mainImmediate,
      dispatcherProvider.unconfined,
      testScope.coroutineContext[ContinuationInterceptor]!!
    )

    allDispatchers.size shouldBe 1
  }

  @Test
  fun `testProvided with default context should use testScope`() = testScope.testProvided {

    // RBT adds a SupervisorJob when there is no Job, so we really only need to check the other properties
    coroutineContext shouldEqualFolded testScope.coroutineContext + coroutineContext[Job]!!
  }

  @Test
  fun `testProvided with context arg should use testScope + context arg`() {

    val job = Job()

    val dispatcher = testScope.coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher
    val dispatcherProvider = testScope.dispatcherProvider as TestDispatcherProvider

    TestProvidedCoroutineScope(
      dispatcher,
      dispatcherProvider,
      testScope.coroutineContext + job
    ).testProvided {

      // RBT adds a SupervisorJob when there is no Job, so we really only need to check the other properties
      coroutineContext shouldEqualFolded testScope.coroutineContext + job
    }
  }

  @Nested
  inner class `nested classes` {

    @Test
    fun `a no-arg extension should use a default TestProvidedCoroutineScope`() {
      val dispatcherProvider = testScope.dispatcherProvider

      val allDispatchers = setOf(
        dispatcherProvider.default,
        dispatcherProvider.io,
        dispatcherProvider.main,
        dispatcherProvider.mainImmediate,
        dispatcherProvider.unconfined,
        testScope.coroutineContext[ContinuationInterceptor]!!
      )

      allDispatchers.size shouldBe 1
    }

    @Test
    fun `testProvided with default context should use testScope`() = testScope.testProvided {

      // RBT adds a SupervisorJob when there is no Job, so we really only need to check the other properties
      coroutineContext shouldEqualFolded testScope.coroutineContext + coroutineContext[Job]!!
    }

    @Test
    fun `testProvided with context arg should use testScope + context arg`() {

      val job = Job()

      val dispatcher =
        testScope.coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher
      val dispatcherProvider = testScope.dispatcherProvider as TestDispatcherProvider

      TestProvidedCoroutineScope(
        dispatcher,
        dispatcherProvider,
        testScope.coroutineContext + job
      ).testProvided {

        // RBT adds a SupervisorJob when there is no Job, so we really only need to check the other properties
        coroutineContext shouldEqualFolded testScope.coroutineContext + job
      }
    }
  }
}
