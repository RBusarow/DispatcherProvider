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

import dispatch.test.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class TestCoroutineRuleWithFactorySample {

  val customScope = TestProvidedCoroutineScope(
    context = CoroutineName("custom name")
  )

  @JvmField
  @Rule
  val rule = TestCoroutineRule { customScope }

  @Test
  fun `rule should be a TestProvidedCoroutineScope`() = runBlocking {

    rule.shouldBeInstanceOf<TestProvidedCoroutineScope>()

    rule.launch {
      // use the rule like any other CoroutineScope
    }
      .join()
  }

  @Test
  fun `rule should be the provided custom scope`() = runBlocking {

    val context = rule.coroutineContext

    context shouldBe customScope.coroutineContext
  }
}
