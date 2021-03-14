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

import dispatch.android.lifecycle.*
import dispatch.core.*

class LifecycleScopeExtensionSample {

  @Sample
  fun lifecycleScopeExtensionSample() {

    // This could be any LifecycleOwner -- Fragments, Activities, Services...
    class SomeFragment : Fragment() {

      init {

        // auto-created MainImmediateCoroutineScope which is lifecycle-aware
        dispatchLifecycleScope // ...

        // active only when "resumed".  starts a fresh coroutine each time
        // this is a rough proxy for LiveData behavior
        dispatchLifecycleScope.launchOnResume { }

        // active only when "started".  starts a fresh coroutine each time
        dispatchLifecycleScope.launchOnStart { }

        // launch when created, automatically stop on destroy
        dispatchLifecycleScope.launchOnCreate { }

        // it works as a normal CoroutineScope as well (because it is)
        dispatchLifecycleScope.launchMain { }
      }
    }
  }
}
