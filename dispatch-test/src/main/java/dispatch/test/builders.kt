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
import kotlin.coroutines.*

/**
 * Delegates to [runBlocking], but injects a [DispatcherProvider] into the created [CoroutineScope].
 *
 * The resultant [CoroutineContext] will use a [BlockingEventLoop][kotlinx.coroutines.BlockingEventLoop]
 * as its default [ContinuationInterceptor].
 *
 * If the `context` does not contain a `DispatcherProvider`,
 * a [TestDispatcherProvider] will be created using the [BlockingEventLoop] interceptor.
 *
 * @param context The base `CoroutineContext` which will be modified
 * to use a [TestCoroutineDispatcher] and [TestDispatcherProvider].
 * [EmptyCoroutineContext] is used if one is not provided.
 *
 * @see runBlocking
 * @see testProvided
 * @sample samples.BuildersSample.runBlockingProvidedSample
 */
@ExperimentalCoroutinesApi
public fun runBlockingProvided(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> Unit
): Unit = runBlocking(context) {

  val existingDispatcherProvider = context[DispatcherProvider]

  val newContext = if (existingDispatcherProvider == null) {
    coroutineContext + TestBasicDispatcherProvider()
  } else coroutineContext

  CoroutineScope(newContext).block()
}

/**
 * Delegates to [runBlockingTest], but injects a [DispatcherProvider] into the created [TestCoroutineScope].
 *
 * If the `context`'s [ContinuationInterceptor] is not a [TestCoroutineDispatcher],
 * then a new [TestCoroutineDispatcher] will be created.
 *
 * If the `context` does not contain a `DispatcherProvider`,
 * a [TestDispatcherProvider] will be created using the `TestCoroutineDispatcher`.
 *
 * @param context The base `CoroutineContext` which will be modified
 * to use a `TestCoroutineDispatcher` and `TestDispatcherProvider`.
 * [EmptyCoroutineContext] is used if one is not provided.
 *
 * @see runBlockingTest
 * @see runBlockingProvided
 * @sample samples.BuildersSample.testProvidedSample
 */
@ExperimentalCoroutinesApi
public fun testProvided(
  context: CoroutineContext = EmptyCoroutineContext,
  testBody: suspend TestProvidedCoroutineScope.() -> Unit
) {

  val dispatcher = (context[ContinuationInterceptor] as? TestCoroutineDispatcher)
    ?: TestCoroutineDispatcher()

  val dispatcherProvider = context[DispatcherProvider]
    ?: TestDispatcherProvider(dispatcher)

  val combinedContext = context + dispatcher + dispatcherProvider

  return runBlockingTest(context = combinedContext) {

    val providedScope = TestProvidedCoroutineScopeImpl(
      dispatcherProvider = dispatcherProvider,
      context = combinedContext + coroutineContext
    )
    testBody.invoke(providedScope)
  }
}

/**
 * Delegates to [runBlockingTest], but injects a [DispatcherProvider] into the created [TestCoroutineScope].
 *
 * If the `context`'s [ContinuationInterceptor] is not a [TestCoroutineDispatcher],
 * then a new [TestCoroutineDispatcher] will be created.
 *
 * If the `context` does not contain a `DispatcherProvider`,
 * a [TestDispatcherProvider] will be created using the `TestCoroutineDispatcher`.
 *
 * @see runBlockingTest
 * @see runBlockingProvided
 * @sample samples.BuildersSample.testProvidedSample
 * @sample samples.BuildersSample.testProvidedExtensionSample
 */
@ExperimentalCoroutinesApi
public fun TestProvidedCoroutineScope.testProvided(
  testBody: suspend TestProvidedCoroutineScope.() -> Unit
): Unit = testProvided(coroutineContext, testBody)
