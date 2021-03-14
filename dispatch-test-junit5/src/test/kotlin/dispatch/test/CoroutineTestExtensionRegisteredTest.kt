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
import io.kotest.matchers.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*
import kotlin.coroutines.*

@ExperimentalCoroutinesApi
class CoroutineTestExtensionRegisteredTest {

  val customScope = TestProvidedCoroutineScope()
  @JvmField @RegisterExtension val customFactoryExtension = coroutineTestExtension { customScope }
  @JvmField @RegisterExtension val defaultExtension = CoroutineTestExtension()

  @Test
  fun `a no-arg extension should use a default TestProvidedCoroutineScope`() {
    val dispatcherProvider = defaultExtension.scope.coroutineContext[DispatcherProvider]!!

    val allDispatchers = setOf(
      dispatcherProvider.default,
      dispatcherProvider.io,
      dispatcherProvider.main,
      dispatcherProvider.mainImmediate,
      dispatcherProvider.unconfined,
      defaultExtension.scope.coroutineContext[ContinuationInterceptor]!!
    )

    allDispatchers.size shouldBe 1
  }

  @Test
  fun `a custom factory extension should use use the custom factory`() {

    val context = customFactoryExtension.scope.coroutineContext

    context shouldBe customScope.coroutineContext
  }

  @Nested
  inner class `nested classes` {
    @Test
    fun `a no-arg extension should use a default TestProvidedCoroutineScope`() {
      val dispatcherProvider = defaultExtension.scope.coroutineContext[DispatcherProvider]!!

      val allDispatchers = setOf(
        dispatcherProvider.default,
        dispatcherProvider.io,
        dispatcherProvider.main,
        dispatcherProvider.mainImmediate,
        dispatcherProvider.unconfined,
        defaultExtension.scope.coroutineContext[ContinuationInterceptor]!!
      )

      allDispatchers.size shouldBe 1
    }

    @Test
    fun `a custom factory extension should use use the custom factory`() {

      val context = customFactoryExtension.scope.coroutineContext

      context shouldBe customScope.coroutineContext
    }
  }
}
