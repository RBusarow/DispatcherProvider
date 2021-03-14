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

@file:Suppress("EXPERIMENTAL_API_USAGE")

package samples

import dispatch.android.espresso.*
import dispatch.android.lifecycle.*
import dispatch.core.*
import kotlinx.coroutines.*

class LifecycleScopeFactorySample {

  @Sample
  fun setLifecycleScopeFactoryProductionSample() {

    class MyApplication : Application {

      override fun onCreate() {

        LifecycleScopeFactory.set { MyCustomElement() + MainImmediateContext() }
      }
    }
  }

  @Sample
  fun setLifecycleScopeFactoryEspressoSample() {

    class MyEspressoTest {

      @Before
      fun setUp() {

        val dispatcherProvider = IdlingDispatcherProvider()

        LifecycleScopeFactory.set { SupervisorJob() + dispatcherProvider + dispatcherProvider.mainImmediate }
      }
    }
  }

  @Sample
  fun LifecycleScopeFactoryResetSample() {

    class MyEspressoTest {

      @Before
      fun setUp() {

        val dispatcherProvider = DispatcherProvider()

        LifecycleScopeFactory.set { SupervisorJob() + dispatcherProvider + dispatcherProvider.mainImmediate }
      }

      @After
      fun tearDown() {
        LifecycleScopeFactory.reset()
      }
    }
  }

  private annotation class Before
  private annotation class After
}
