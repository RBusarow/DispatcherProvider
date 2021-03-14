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

class LaunchSample {

  @Sample
  fun launchDefaultSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    launchDefault {

      dispatcherName() shouldBe "default"

    }.join()

  }

  @Sample
  fun launchIOSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    launchIO {

      dispatcherName() shouldBe "io"

    }.join()

  }

  @Sample
  fun launchMainSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    launchMain {

      dispatcherName() shouldBe "main"

    }.join()

  }

  @Sample
  fun launchMainImmediateSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    launchMainImmediate {

      dispatcherName() shouldBe "main immediate"

    }.join()

  }

  @Sample
  fun launchUnconfinedSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    launchUnconfined {

      dispatcherName() shouldBe "unconfined"

    }.join()

  }
}

