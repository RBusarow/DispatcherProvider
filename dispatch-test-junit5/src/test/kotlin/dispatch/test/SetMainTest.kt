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

import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*

@Suppress("HardCodedDispatcher")
@ExperimentalCoroutinesApi
class SetMainTest {

  @RegisterExtension @JvmField val ext = CoroutineTestExtension()

  @Test
  fun `extension's internal scope's dispatcher should be set as Main by default`() {

    val beforeSet = Dispatchers.Main

    Dispatchers.setMain(ext.scope.dispatcherProvider.main)

    beforeSet shouldBe Dispatchers.Main
  }

  @Test
  fun `function injected scope's dispatcher should be set as Main by default`(
    scope: TestProvidedCoroutineScope
  ) {

    val beforeSet = Dispatchers.Main

    Dispatchers.setMain(scope.dispatcherProvider.main)

    beforeSet shouldBe Dispatchers.Main
  }

  @Nested
  inner class `constructor injection`(
    val scope: TestProvidedCoroutineScope
  ) {

    @Test
    fun `constructor injected scope's dispatcher should be set as Main by default`() {

      val beforeSet = Dispatchers.Main

      Dispatchers.setMain(scope.dispatcherProvider.main)

      beforeSet shouldBe Dispatchers.Main
    }
  }
}
