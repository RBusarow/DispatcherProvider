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
import kotlinx.coroutines.*
import org.junit.jupiter.api.*

@ExperimentalCoroutinesApi
class BuildersSample {

  @Sample
  fun runBlockingProvidedSample() {

    @Test
    fun someTest() = runBlockingProvided {

      val subject = SomeClass(this)

      val myData = Data()

      subject.dataDeferred()
        .await() shouldBe myData

    }

  }

  @Sample
  fun testProvidedSample() {

    @Test
    fun someTest() = testProvided {

      val subject = SomeClass(this)

      val myData = Data()

      subject.dataDeferred()
        .await() shouldBe myData

    }

  }

  @Sample
  fun testProvidedExtensionSample() {

    val scope = TestProvidedCoroutineScope()

    @Test
    fun someTest() = scope.testProvided {

      val subject = SomeClass(this)

      val myData = Data()

      subject.dataDeferred()
        .await() shouldBe myData

    }

  }

}
