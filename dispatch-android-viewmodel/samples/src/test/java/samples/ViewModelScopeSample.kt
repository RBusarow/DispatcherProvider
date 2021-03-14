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

import dispatch.android.viewmodel.*
import dispatch.core.*
import io.kotest.matchers.*
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class ViewModelScopeSample {

  @Sample
  fun viewModelScopeSample() {

    class SomeViewModel : DispatchViewModel() {

      init {

        // auto-created MainImmediateCoroutineScope which is auto-cancelled in onClear()
        viewModelScope // ...

        // it works as a normal CoroutineScope (because it is)
        viewModelScope.launchMain { }

        // this is the same CoroutineScope instance each time
        viewModelScope.launchMain { }
      }

      override fun onClear() {
        viewModelScope.isActive shouldBe false
      }
    }
  }
}
