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
import io.kotest.matchers.types.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import kotlin.coroutines.*

@ExperimentalCoroutinesApi
internal class CoroutineScopeExtKtTest {

  val provider = object : DispatcherProvider {
    override val default: CoroutineDispatcher = TestCoroutineDispatcher()
    override val io: CoroutineDispatcher = TestCoroutineDispatcher()
    override val main: CoroutineDispatcher = TestCoroutineDispatcher()
    override val mainImmediate: CoroutineDispatcher = TestCoroutineDispatcher()
    override val unconfined: CoroutineDispatcher = TestCoroutineDispatcher()
  }

  @Test
  fun `CoroutineScope default dispatcher`() {

    CoroutineScope(provider).defaultDispatcher shouldBe provider.default
  }

  @Test
  fun `CoroutineScope io dispatcher`() {

    CoroutineScope(provider).ioDispatcher shouldBe provider.io
  }

  @Test
  fun `CoroutineScope main dispatcher`() {

    CoroutineScope(provider).mainDispatcher shouldBe provider.main
  }

  @Test
  fun `CoroutineScope mainImmediate dispatcher`() {

    CoroutineScope(provider).mainImmediateDispatcher shouldBe provider.mainImmediate
  }

  @Test
  fun `CoroutineScope unconfined dispatcher`() {

    CoroutineScope(provider).unconfinedDispatcher shouldBe provider.unconfined
  }

  @Nested
  inner class `CoroutineScope dispatcherProvider property` {

    @Test
    fun `call from scope with existing provider should return that provider`() {

      val scope = CoroutineScope(provider)

      scope.dispatcherProvider shouldBe provider
    }

    @Test
    fun `call from scope without provider should return new one`() {

      val scope = MainScope()

      scope.dispatcherProvider.shouldBeInstanceOf<DispatcherProvider>()
    }
  }

  @Nested
  inner class `CoroutineContext dispatcherProvider property` {

    @Test
    fun `call from context with existing provider should return that provider`() {

      val context = Job() + provider

      context.dispatcherProvider shouldBe provider
    }

    @Test
    fun `call from context without provider should return new one`() {

      val context: CoroutineContext = Job()

      context.dispatcherProvider.shouldBeInstanceOf<DispatcherProvider>()
    }
  }
}
