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

import dispatch.core.*
import dispatch.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*

@Suppress("HardCodedDispatcher")
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DefaultDispatcherProviderExtensionSample {

  val main = newSingleThreadContext("main")

  @BeforeEach
  fun beforeEach() {
    Dispatchers.setMain(main)
  }

  @AfterEach
  fun afterEach() {
    Dispatchers.resetMain()
  }

  @Sample
  fun defaultDispatcherProviderResetSample() {

    val custom = CustomDispatcherProvider()
    DefaultDispatcherProvider.set(custom)

    DefaultDispatcherProvider.get() shouldBe custom

    DefaultDispatcherProvider.reset()

    val default = DefaultDispatcherProvider.get()

    default shouldNotBe custom

    default.default shouldBe Dispatchers.Default
    default.io shouldBe Dispatchers.IO
    default.main shouldBe Dispatchers.Main
    default.mainImmediate shouldBe Dispatchers.Main.immediate
    default.unconfined shouldBe Dispatchers.Unconfined

  }

  @Suppress("TestFunctionName")
  private fun CustomDispatcherProvider(): DispatcherProvider = object : DispatcherProvider {

    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val main: CoroutineDispatcher get() = Dispatchers.Main
    override val mainImmediate: CoroutineDispatcher get() = Dispatchers.Main.immediate
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
  }
}
