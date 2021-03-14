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

@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_OVERRIDE")

package samples

import dispatch.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*

@CoroutineTest
class CoroutineTestDefaultFactorySample(
  val testScope: TestProvidedCoroutineScope
) {

  @Test
  fun `extension should automatically inject into test class`() = runBlocking {

    val subject = SomeClass(testScope)

    val resultDeferred = subject.someFunction()

    testScope.advanceUntilIdle()

    resultDeferred.await() shouldBe someValue
  }

}
