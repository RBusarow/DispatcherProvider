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

package dispatch.sample

import androidx.lifecycle.*
import dispatch.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class MainViewModel(
  val coroutineScope: DefaultCoroutineScope,
  val repository: SomeRepository
) : ViewModel() {

  val expensiveDeferred: Deferred<String> = repository.getSomethingExpensiveUnstructured()

  val alsoExpensiveDeferred: Deferred<String> by lazy {
    CompletableDeferred<String>().also { completable ->

      // this no-arg launch will use the default CoroutineDispatcher in the CoroutineScope,
      // which in this case is dispatcherProvider.default
      coroutineScope.launch {
        val expensive = repository.getSomethingExpensive()
        completable.complete(expensive)
      }
    }
  }

  val message = flow {

    emit("Get ready for some coroutine stuff...")

    emit(expensiveDeferred.await())

    emit(alsoExpensiveDeferred.await())

    @Suppress("MagicNumber")
    delay(5000)

    // explicitly specify the default dispatcher (which is redundant here, but it's an example)
  }.flowOn(coroutineScope.defaultDispatcher)

  override fun onCleared() {
    super.onCleared()

    coroutineScope.cancel()
  }
}
