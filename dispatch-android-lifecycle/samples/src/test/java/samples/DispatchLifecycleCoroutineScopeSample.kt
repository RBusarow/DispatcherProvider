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
import dispatch.android.lifecycle.DispatchLifecycleScope.MinimumStatePolicy.*
import dispatch.core.*
import dispatch.internal.test.android.*
import dispatch.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@CoroutineTest
@ExperimentalCoroutinesApi
class DispatchLifecycleScopeSample : LiveDataTest {

  @Sample
  fun lifecycleCoroutineScopeFromScopeSample() = runBlocking {

    // This could be any LifecycleOwner -- Fragments, Activities, Services...
    class SomeFragment @Inject constructor(
      coroutineScope: CoroutineScope // could be any type of CoroutineScope
    ) : Fragment() {

      val lifecycleScope = DispatchLifecycleScope(lifecycle, coroutineScope)

      init {

        // active only when "resumed".  starts a fresh coroutine each time
        lifecycleScope.launchOnResume { }

        // active only when "started".  starts a fresh coroutine each time
        // this is a rough proxy for LiveData behavior
        lifecycleScope.launchOnStart { }

        // active after only the first "started" event, and never re-started
        lifecycleScope.launchOnStart(minimumStatePolicy = CANCEL) { }

        // launch when created, automatically stop on destroy
        lifecycleScope.launchOnCreate { }

        // it works as a normal CoroutineScope as well (because it is)
        lifecycleScope.launchMain { }
      }
    }
  }

  @Sample
  fun lifecycleCoroutineScopeFromContextSample() = runBlocking {

    // This could be any LifecycleOwner -- Fragments, Activities, Services...
    class SomeFragment : Fragment() {

      val context = Job() + DispatcherProvider()

      val lifecycleScope = DispatchLifecycleScope(lifecycle, context)

      init {

        // active only when "resumed".  starts a fresh coroutine each time
        lifecycleScope.launchOnResume { }

        // active only when "started".  starts a fresh coroutine each time
        // this is a rough proxy for LiveData behavior
        lifecycleScope.launchOnStart { }

        // active after only the first "started" event, and never re-started
        lifecycleScope.launchOnStart(minimumStatePolicy = CANCEL) { }

        // launch when created, automatically stop on destroy
        lifecycleScope.launchOnCreate { }

        // it works as a normal CoroutineScope as well (because it is)
        lifecycleScope.launchMain { }
      }
    }
  }

  @Sample
  fun lifecycleCoroutineScopeDefaultSample() = runBlocking {

    // This could be any LifecycleOwner -- Fragments, Activities, Services...
    class SomeFragment : Fragment() {

      val lifecycleScope = DispatchLifecycleScope(lifecycle)

      init {

        // active only when "resumed".  starts a fresh coroutine each time
        lifecycleScope.launchOnResume { }

        // active only when "started".  starts a fresh coroutine each time
        // this is a rough proxy for LiveData behavior
        lifecycleScope.launchOnStart { }

        // active after only the first "started" event, and never re-started
        lifecycleScope.launchOnStart(minimumStatePolicy = CANCEL) { }

        // launch when created, automatically stop on destroy
        lifecycleScope.launchOnCreate { }

        // it works as a normal CoroutineScope as well (because it is)
        lifecycleScope.launchMain { }
      }
    }
  }

  @Sample
  fun launchOnCreateOnceSample() = runBlocking {

    val channel = Channel<String>()
    val history = mutableListOf<String>()

    class SomeViewModel {
      val someFlow = flow {
        repeat(100) {
          emit(it)
        }
      }
    }

    class SomeFragment : Fragment() {

      val viewModel = SomeViewModel()

      init {

        dispatchLifecycleScope.launchOnCreate(minimumStatePolicy = CANCEL) {
          viewModel.someFlow.collect {
            channel.send("$it")
          }
        }
      }
    }

    val fragment = SomeFragment()

    history.add("creating")
    fragment.create()

    repeat(3) {
      history.add(channel.receive())
    }

    // destroying the lifecycle cancels the lifecycleScope
    history.add("destroying")
    fragment.destroy()

    history shouldBe listOf(
      "creating",
      "0",
      "1",
      "2",
      "destroying"
    )
  }

  @Sample
  fun launchOnCreateRestartingSample() = runBlocking {

    val channel = Channel<String>()
    val history = mutableListOf<String>()

    class SomeViewModel {
      val someFlow = flow {
        repeat(100) {
          emit(it)
        }
      }
    }

    class SomeFragment : Fragment() {

      val viewModel = SomeViewModel()

      init {

        dispatchLifecycleScope.launchOnCreate {
          viewModel.someFlow.collect {
            channel.send("$it")
          }
        }
      }
    }

    val fragment = SomeFragment()

    history.add("creating")
    fragment.create()

    repeat(3) {
      history.add(channel.receive())
    }

    // destroying the lifecycle cancels the lifecycleScope
    history.add("destroying")
    fragment.destroy()

    history shouldBe listOf(
      "creating",
      "0",
      "1",
      "2",
      "destroying"
    )
  }

  @Sample
  fun launchOnStartOnceSample() = runBlocking {

    val channel = Channel<String>()
    val history = mutableListOf<String>()

    class SomeViewModel {
      val someFlow = flow {
        repeat(100) {
          emit(it)
        }
      }
    }

    class SomeFragment : Fragment() {

      val viewModel = SomeViewModel()

      init {
        dispatchLifecycleScope.launchOnStart(minimumStatePolicy = CANCEL) {
          viewModel.someFlow.collect {
            channel.send("$it")
          }
        }
      }
    }

    val fragment = SomeFragment()

    history.add("starting")
    fragment.start()

    repeat(3) {
      history.add(channel.receive())
    }

    // stopping the lifecycle cancels the existing Job
    history.add("stopping")
    fragment.stop()

    // starting the lifecycle does not create a new Job

    history shouldBe listOf(
      "starting",
      "0",
      "1",
      "2",
      "stopping"
    )
  }

  @Sample
  fun launchOnStartRestartingSample() = runBlocking {

    val channel = Channel<String>()
    val history = mutableListOf<String>()

    class SomeViewModel {
      val someFlow = flow {
        repeat(100) {
          emit(it)
        }
      }
    }

    class SomeFragment : Fragment() {

      val viewModel = SomeViewModel()

      init {
        dispatchLifecycleScope.launchOnStart {
          viewModel.someFlow.collect {
            channel.send("$it")
          }
        }
      }
    }

    val fragment = SomeFragment()

    history.add("starting")
    fragment.start()

    repeat(3) {
      history.add(channel.receive())
    }

    // stopping the lifecycle cancels the existing Job
    history.add("stopping")
    fragment.stop()

    // starting the lifecycle creates a new Job
    history.add("starting")
    fragment.start()

    repeat(3) {
      history.add(channel.receive())
    }

    history.add("stopping")
    fragment.stop()

    history shouldBe listOf(
      "starting",
      "0",
      "1",
      "2",
      "stopping",
      "starting",
      "0",
      "1",
      "2",
      "stopping"
    )
  }

  @Sample
  fun launchOnResumeOnceSample() = runBlocking {

    val channel = Channel<String>()
    val history = mutableListOf<String>()

    class SomeViewModel {
      val someFlow = flow {
        repeat(100) {
          emit(it)
        }
      }
    }

    class SomeFragment : Fragment() {

      val viewModel = SomeViewModel()

      init {
        dispatchLifecycleScope.launchOnResume(minimumStatePolicy = CANCEL) {
          viewModel.someFlow.collect {
            channel.send("$it")
          }
        }
      }
    }

    val fragment = SomeFragment()

    history.add("resuming")
    fragment.resume()

    repeat(3) {
      history.add(channel.receive())
    }

    // pausing the lifecycle cancels the existing Job
    history.add("pausing")
    fragment.pause()

    // resuming the lifecycle does not create a new Job

    history shouldBe listOf(
      "resuming",
      "0",
      "1",
      "2",
      "pausing"
    )
  }

  @Sample
  fun launchOnResumeRestartingSample() = runBlocking {

    val channel = Channel<String>()
    val history = mutableListOf<String>()

    class SomeViewModel {
      val someFlow = flow {
        repeat(100) {
          emit(it)
        }
      }
    }

    class SomeFragment : Fragment() {

      val viewModel = SomeViewModel()

      init {
        dispatchLifecycleScope.launchOnResume {
          viewModel.someFlow.collect {
            channel.send("$it")
          }
        }
      }
    }

    val fragment = SomeFragment()

    history.add("resuming")
    fragment.resume()

    repeat(3) {
      history.add(channel.receive())
    }

    // pausing the lifecycle cancels the existing Job
    history.add("pausing")
    fragment.pause()

    // resuming the lifecycle creates a new Job
    history.add("resuming")
    fragment.resume()

    repeat(3) {
      history.add(channel.receive())
    }

    history.add("pausing")
    fragment.pause()

    history shouldBe listOf(
      "resuming",
      "0",
      "1",
      "2",
      "pausing",
      "resuming",
      "0",
      "1",
      "2",
      "pausing"
    )
  }
}
