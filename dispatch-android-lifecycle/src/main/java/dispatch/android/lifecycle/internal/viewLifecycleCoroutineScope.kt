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

package dispatch.android.lifecycle.internal

import androidx.fragment.app.*
import androidx.lifecycle.*
import dispatch.android.lifecycle.*
import dispatch.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

@ExperimentalCoroutinesApi
internal fun bindViewLifecycleCoroutineScope(
  receiverScope: CoroutineScope,
  fragment: Fragment,
  block: ViewLifecycleCoroutineScope.() -> Unit
): Job {

  val observerJob = fragment.viewLifecycleOwnerLiveData.asFlow()
    .onEachLatest { owner: LifecycleOwner? ->

      if (owner != null) {

        /*
        Create a new ViewLifecycleCoroutineScope for each update to the LiveData.

        This new scope has the same CoroutineContext as the "receiverScope" parent,
        except that its Job is automatically cancelled for each new LiveData event
        without affecting the Job contained in the receiver scope.
         */
        val viewScope = ViewLifecycleCoroutineScope(
          lifecycle = owner.lifecycle,
          coroutineContext = coroutineContext
        )

        viewScope.block()
      }
    }.flowOnMainImmediate()
    .launchIn(receiverScope)

  fragment.lifecycle.addObserver(
    LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_DESTROY) {
        observerJob.cancel()
      }
    }
  )

  return observerJob
}
