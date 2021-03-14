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
import io.kotest.matchers.*
import kotlinx.coroutines.*

class AsyncSample {

  @Sample
  fun asyncDefaultSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    asyncDefault {

      dispatcherName() shouldBe "default"

    }.join()

  }

  @Sample
  fun asyncIOSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    asyncIO {

      dispatcherName() shouldBe "io"

    }.join()

  }

  @Sample
  fun asyncMainSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    asyncMain {

      dispatcherName() shouldBe "main"

    }.join()

  }

  @Sample
  fun asyncMainImmediateSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    asyncMainImmediate {

      dispatcherName() shouldBe "main immediate"

    }.join()

  }

  @Sample
  fun asyncUnconfinedSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    asyncUnconfined {

      dispatcherName() shouldBe "unconfined"

    }.join()

  }
}

