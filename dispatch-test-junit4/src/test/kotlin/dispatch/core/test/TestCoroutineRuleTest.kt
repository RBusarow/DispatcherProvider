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
import dispatch.internal.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import kotlin.coroutines.*

@ExperimentalCoroutinesApi
class TestCoroutineRuleTest {

  val customScope = TestProvidedCoroutineScope()
  @JvmField @Rule val customFactoryRule = TestCoroutineRule { customScope }

  @JvmField @Rule val defaultRule = TestCoroutineRule()

  @JvmField @Rule val failureRule = ExpectedFailureRule()

  @Test
  fun `a no-arg rule should use a default TestProvidedCoroutineScope`() {
    val dispatcherProvider = defaultRule.coroutineContext[DispatcherProvider]!!

    val allDispatchers = setOf(
      dispatcherProvider.default,
      dispatcherProvider.io,
      dispatcherProvider.main,
      dispatcherProvider.mainImmediate,
      dispatcherProvider.unconfined,
      defaultRule.dispatcher,
      defaultRule.coroutineContext[ContinuationInterceptor]!!
    )

    allDispatchers.size shouldBe 1
  }

  @Test
  fun `a custom factory rule should use use the custom factory`() {

    val context = customFactoryRule.coroutineContext

    context shouldBe customScope.coroutineContext
  }

  /**
   * We can't just use a normal `@Test(expected = UncompletedCoroutinesError::class)` because
   * that expects the `Throwable` to be thrown during the test itself.
   *
   * In this case, it's thrown during tear-down (after the function),
   * so we need to wrap the process with a larger try/catch.
   */
  @Test
  @Fails(expected = UncompletedCoroutinesError::class)
  fun `leaking coroutine should fail with UncompletedCoroutineError`() {

    // Job should run well past completion -- making the coroutine leak
    defaultRule.launch { delay(100000) }
  }
}
