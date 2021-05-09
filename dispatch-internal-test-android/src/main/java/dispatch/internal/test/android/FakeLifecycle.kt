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

package dispatch.internal.test.android

import androidx.lifecycle.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

class FakeLifecycle(lifecycleOwner: FakeLifecycleOwner) : Lifecycle() {

  val delegate = LifecycleRegistry(lifecycleOwner)

  val observerEvents = MutableSharedFlow<ObserverEvent>(
    replay = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )

  val observerCount: Int get() = delegate.observerCount

  sealed class ObserverEvent {
    abstract val observer: LifecycleObserver

    data class Add(override val observer: LifecycleObserver) : ObserverEvent()
    data class Remove(override val observer: LifecycleObserver) : ObserverEvent()
  }

  override fun addObserver(observer: LifecycleObserver) {
    delegate.addObserver(observer)
    observerEvents.tryEmit(ObserverEvent.Add(observer))
  }

  override fun removeObserver(observer: LifecycleObserver) {
    delegate.removeObserver(observer)
    observerEvents.tryEmit(ObserverEvent.Remove(observer))
  }

  override fun getCurrentState(): State = delegate.currentState
  fun setCurrentState(state: State) {
    delegate.currentState = state
  }

  fun handleLifecycleEvent(event: Event) {
    delegate.handleLifecycleEvent(event)
  }
}
