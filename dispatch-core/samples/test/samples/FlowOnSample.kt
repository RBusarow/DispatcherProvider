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
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class FlowOnSample {

  @Sample
  fun flowOnDefaultSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    flow {

      dispatcherName() shouldBe "default"

      emit(Unit)
    }.flowOnDefault() // switch to the "default" dispatcher for everything upstream
      .collect()      // collect the flow from the "main" dispatcher

  }

  @Sample
  fun flowOnIOSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    flow {

      dispatcherName() shouldBe "io"

      emit(Unit)
    }.flowOnIO()      // switch to the "io" dispatcher for everything upstream
      .collect()      // collect the flow from the "main" dispatcher

  }

  @Sample
  fun flowOnMainSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    flow {

      dispatcherName() shouldBe "main"

      emit(Unit)
    }.flowOnMain()    // switch to the "main" dispatcher for everything upstream
      .collect()      // collect the flow from the "default" dispatcher

  }

  @Sample
  fun flowOnMainImmediateSample() =
    runBlocking(someDispatcherProvider) {

      dispatcherName() shouldBe "runBlocking thread"

      flow {

        dispatcherName() shouldBe "main immediate"

        emit(Unit)
      }.flowOnMainImmediate()  // switch to the "mainImmediate" dispatcher for everything upstream
        .collect()             // collect the flow from the "main" dispatcher

    }

  @Sample
  fun flowOnUnconfinedSample() = runBlocking(someDispatcherProvider) {

    dispatcherName() shouldBe "runBlocking thread"

    flow {

      dispatcherName() shouldBe "unconfined"

      emit(Unit)
    }.flowOnUnconfined()  // switch to the "unconfined" dispatcher for everything upstream
      .collect()          // collect the flow from the "main" dispatcher

  }

}
