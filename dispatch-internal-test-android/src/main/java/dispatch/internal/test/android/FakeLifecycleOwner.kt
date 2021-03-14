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
import kotlinx.coroutines.*

@Suppress("EXPERIMENTAL_API_USAGE")
open class FakeLifecycleOwner(
  initialState: Lifecycle.State = Lifecycle.State.INITIALIZED,
  private val mainDispatcher: CoroutineDispatcher = fakeMainDispatcher()
) : LifecycleOwner {

  val fakeLifecycle: FakeLifecycle by lazy { FakeLifecycle(this) }

  init {
    when (initialState) {
      Lifecycle.State.DESTROYED -> destroy()
      Lifecycle.State.CREATED -> create()
      Lifecycle.State.STARTED -> start()
      Lifecycle.State.RESUMED -> resume()
      else -> Unit
    }
  }

  override fun getLifecycle(): FakeLifecycle = fakeLifecycle

  fun stepDown() = when (lifecycle.currentState) {
    Lifecycle.State.DESTROYED -> throw IllegalArgumentException("already destroyed")
    Lifecycle.State.INITIALIZED -> throw IllegalArgumentException(
      "cannot transition straight from initialized to destroyed"
    )
    Lifecycle.State.CREATED -> destroy()
    Lifecycle.State.STARTED -> stop()
    Lifecycle.State.RESUMED -> pause()
  }

  fun stepUp() = when (lifecycle.currentState) {
    Lifecycle.State.DESTROYED -> throw IllegalArgumentException("already destroyed")
    Lifecycle.State.INITIALIZED -> create()
    Lifecycle.State.CREATED -> start()
    Lifecycle.State.STARTED -> resume()
    Lifecycle.State.RESUMED -> throw IllegalArgumentException("already resumed")
  }

  fun create() = runBlocking(mainDispatcher) {
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
  }

  fun start() = runBlocking(mainDispatcher) {
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
  }

  fun resume() = runBlocking(mainDispatcher) {
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  fun pause() = runBlocking(mainDispatcher) {
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  }

  fun stop() = runBlocking(mainDispatcher) {
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
  }

  fun initialize() = runBlocking(mainDispatcher) {
    lifecycle.currentState = Lifecycle.State.INITIALIZED
  }

  fun destroy() = runBlocking(mainDispatcher) {
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  }

  fun getObserverCount(): Int = runBlocking(mainDispatcher) { fakeLifecycle.observerCount }
}

@Suppress("EXPERIMENTAL_API_USAGE")
private fun fakeMainDispatcher() = newSingleThreadContext("FakeLifecycleOwner main")
