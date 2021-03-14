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

package dispatch.android.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.*

/**
 * **Deprecated** in favor of [DispatchViewModel].
 *
 * @see DispatchViewModel
 */
@Deprecated(
  "Use DispatchViewModel",
  replaceWith = ReplaceWith("DispatchViewModel")
)
typealias CoroutineViewModel = DispatchViewModel

/**
 * Base class for [ViewModel]s which will be using a [viewModelScope].
 *
 * The `viewModelScope` instance is created automatically upon first access
 * from the factory set in [ViewModelScopeFactory].
 *
 * The type of [CoroutineScope] created is configurable via [ViewModelScopeFactory.set].
 *
 * This must be an abstract class since nothing about the [ViewModel.onCleared] event is exposed.
 *
 * `viewModelScope` is automatically cancelled when `onCleared()` is invoked.
 *
 * @sample samples.ViewModelScopeSample.viewModelScopeSample
 */
abstract class DispatchViewModel : ViewModel() {

  private val _coroutineScope = AtomicReference<CoroutineScope?>(null)

  /**
   * [CoroutineScope] instance for the [DispatchViewModel].
   * By default, it uses the [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate] dispatcher
   *
   * The `viewModelScope` instance is created automatically upon first access
   * from the factory set in [ViewModelScopeFactory].
   *
   * The type of [CoroutineScope] created is configurable via [ViewModelScopeFactory.set].
   *
   * `viewModelScope` is automatically cancelled when `onCleared()` is invoked.
   *
   * @sample samples.ViewModelScopeSample.viewModelScopeSample
   */
  val viewModelScope: CoroutineScope
    get() {
      val scope = _coroutineScope.get()
      if (scope != null) {
        return scope
      }
      val new = ViewModelScopeFactory.create()

      return if (_coroutineScope.compareAndSet(null, new)) {
        new
      } else {
        _coroutineScope.get()!!
      }
    }

  /**
   * It is necessary to do a final override of [ViewModel.onCleared] to ensure that [viewModelScope] is cancelled.
   *
   * Use [onClear] to perform logic after this event.
   */
  final override fun onCleared() {
    _coroutineScope.get()
      ?.coroutineContext?.cancel()
    onClear()
  }

  /**
   * This method will be called when this ViewModel is no longer used and will be destroyed.
   * <p>
   * It is useful when ViewModel observes some data and you need to clear this subscription to
   * prevent a leak of this ViewModel.
   */
  protected open fun onClear() {}
}
