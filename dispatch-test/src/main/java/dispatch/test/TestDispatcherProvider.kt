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

package dispatch.test

import dispatch.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*

/**
 * [DispatcherProvider] implementation for testing, where each property defaults to a [TestCoroutineDispatcher].
 *
 * A default version will create a different `TestCoroutineDispatcher` for each property.
 */
@ExperimentalCoroutinesApi
public class TestDispatcherProvider(
  /**
   * [CoroutineDispatcher] generally intended for cpu-bound tasks.
   *
   * Corresponds to the [Dispatchers.Default] property in a default implementation.
   *
   * @see Dispatchers.Default
   */
  override val default: CoroutineDispatcher = TestCoroutineDispatcher(),
  /**
   * [CoroutineDispatcher] generally intended for blocking I/O tasks.
   *
   * Corresponds to the [Dispatchers.IO] property in a default implementation.
   *
   * @see Dispatchers.IO
   */
  override val io: CoroutineDispatcher = TestCoroutineDispatcher(),
  /**
   * [CoroutineDispatcher] which is confined to the "main" thread.
   *
   * Corresponds to the [Dispatchers.Main] property in a default implementation.
   *
   * @see Dispatchers.Main
   */
  override val main: CoroutineDispatcher = TestCoroutineDispatcher(),
  /**
   * [CoroutineDispatcher] which is confined to the "main" thread with immediate dispatch.
   *
   * Corresponds to the [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate] property in a default implementation.
   *
   * @see [MainCoroutineDispatcher.immediate]
   */
  override val mainImmediate: CoroutineDispatcher = TestCoroutineDispatcher(),
  /**
   * Corresponds to the [Dispatchers.Unconfined] property in a default implementation.
   *
   * @see [Dispatchers.Unconfined]
   */
  override val unconfined: CoroutineDispatcher = TestCoroutineDispatcher()
) : DispatcherProvider {
  /**
   * @suppress
   */
  override fun toString(): String {
    return """${this::class.java.simpleName}: default       -> $default
      |io            -> $io
      |main          -> $main
      |mainImmediate -> $mainImmediate
      |unconfined    -> $unconfined
    """.replaceIndentByMargin(" ".repeat(this::class.java.simpleName.length + 2))
  }
}

/**
 * Convenience factory function for [TestDispatcherProvider], creating an implementation
 * where all properties point to the same underlying [TestCoroutineDispatcher].
 */
@ExperimentalCoroutinesApi
public fun TestDispatcherProvider(
  dispatcher: CoroutineDispatcher
): TestDispatcherProvider = TestDispatcherProvider(
  default = dispatcher,
  io = dispatcher,
  main = dispatcher,
  mainImmediate = dispatcher,
  unconfined = dispatcher
)

/**
 * "Basic" [TestDispatcherProvider] which mimics production behavior,
 * without the automatic time control of [TestCoroutineDispatcher]
 * and without the need for [Dispatchers.setMain][kotlinx.coroutines.test.setMain]
 *
 * The `default`, `io`, and `unconfined` properties just delegate to their counterparts in [Dispatchers].
 *
 * The `main` and `mainImmediate` properties share a single dispatcher and thread
 * as they do with the `Dispatchers.setMain(...)` implementation from `kotlinx-coroutines-test`.
 */
@ExperimentalCoroutinesApi
@Suppress("HardCodedDispatcher")
public fun TestBasicDispatcherProvider(): TestDispatcherProvider {

  @Suppress("EXPERIMENTAL_API_USAGE")
  val mainThread = newSingleThreadContext("main thread proxy")

  return TestDispatcherProvider(
    default = Dispatchers.Default,
    io = Dispatchers.IO,
    main = mainThread,
    mainImmediate = mainThread,
    unconfined = Dispatchers.Unconfined
  )
}
