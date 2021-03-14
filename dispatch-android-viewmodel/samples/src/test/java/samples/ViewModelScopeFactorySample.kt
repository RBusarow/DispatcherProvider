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

import dispatch.android.espresso.*
import dispatch.android.viewmodel.*
import dispatch.core.*
import dispatch.test.*
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class ViewModelScopeFactorySample {

  @Sample
  fun setViewModelScopeFactoryProductionSample() {

    class MyApplication : Application {

      override fun onCreate() {
        ViewModelScopeFactory.set { MainImmediateCoroutineScope() }
      }
    }
  }

  @Sample
  fun setViewModelScopeFactoryEspressoSample() {

    class MyEspressoTest {

      @Before
      fun setUp() {
        ViewModelScopeFactory.set { MainImmediateIdlingCoroutineScope() }
      }
    }
  }

  @Sample
  fun viewModelScopeFactoryResetSample() {

    class MyEspressoTest {

      @Before
      fun setUp() {
        ViewModelScopeFactory.set { MainImmediateIdlingCoroutineScope() }
      }

      @After
      fun tearDown() {
        ViewModelScopeFactory.reset()
      }
    }
  }

  @Sample
  fun setViewModelScopeFactoryJvmSample() {

    class MyJvmTest {

      @Before
      fun setUp() {
        ViewModelScopeFactory.set { TestProvidedCoroutineScope() }
      }
    }
  }

  private annotation class Before
  private annotation class After
}
