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
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
internal class DispatcherProviderTest {

  val main = newSingleThreadContext("main dispatcher")

  @BeforeEach
  fun beforeEach() {
    Dispatchers.setMain(main)
  }

  @AfterEach
  fun afterEach() {
    Dispatchers.resetMain()
  }

  @Nested
  inner class `DispatcherProvider factory` {

    @Test
    fun `created provider default dispatcher should use Dispatchers Default`() {

      DispatcherProvider().default shouldBe Dispatchers.Default
    }

    @Test
    fun `created provider io dispatcher should use Dispatchers IO`() {

      DispatcherProvider().io shouldBe Dispatchers.IO
    }

    @Test
    fun `created provider main dispatcher should use Dispatchers Main`() {

      DispatcherProvider().main shouldBe Dispatchers.Main
    }

    @Test
    fun `created provider mainImmediate dispatcher should use Dispatchers Main immediate`() {

      DispatcherProvider().mainImmediate shouldBe Dispatchers.Main.immediate
    }

    @Test
    fun `created provider unconfined dispatcher should use Dispatchers Unconfined`() {

      DispatcherProvider().unconfined shouldBe Dispatchers.Unconfined
    }

    @Test
    fun `DispatcherProvider factory should create DefaultDispatcherProvider`() {

      DispatcherProvider() shouldBe DefaultDispatcherProvider.get()
    }
  }
}
