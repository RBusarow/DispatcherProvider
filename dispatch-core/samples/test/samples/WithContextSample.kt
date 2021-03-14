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

class WithContextSample {

  @Sample
  fun withDefaultSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    withDefault {

      dispatcherName() shouldBe "default"

    }
  }

  @Sample
  fun withIOSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    withIO {

      dispatcherName() shouldBe "io"

    }
  }

  @Sample
  fun withMainSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    withMain {

      dispatcherName() shouldBe "main"

    }
  }

  @Sample
  fun withMainImmediateSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    withMainImmediate {

      dispatcherName() shouldBe "main immediate"

    }
  }

  @Sample
  fun withUnconfinedSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    withUnconfined {

      dispatcherName() shouldBe "unconfined"

    }
  }
}
