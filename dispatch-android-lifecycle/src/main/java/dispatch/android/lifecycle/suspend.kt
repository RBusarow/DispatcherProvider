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

package dispatch.android.lifecycle

import androidx.lifecycle.*
import dispatch.android.lifecycle.internal.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * Executes `block` one time, the next time the [Lifecycle]'s state is at least [Lifecycle.State.CREATED].
 *
 * If the lifecycle is already in this state, `block` will be executed immediately.
 *
 * @see [DispatchLifecycleScope.launchOnCreate] for repeating behavior.
 * @param context *optional* - additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @sample samples.LifecycleSuspendSample.lifecycleOwnerOnNextCreateSample
 */
suspend fun <T> LifecycleOwner.onNextCreate(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T? = lifecycle.onNext(context, Lifecycle.State.CREATED, block)

/**
 * Executes `block` one time, the next time the [Lifecycle]'s state is at least [Lifecycle.State.CREATED].
 *
 * If the lifecycle is already in this state, `block` will be executed immediately.
 *
 * @see [DispatchLifecycleScope.launchOnCreate] for repeating behavior.
 * @param context *optional* - additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @sample samples.LifecycleSuspendSample.lifecycleOnNextCreateSample
 */
suspend fun <T> Lifecycle.onNextCreate(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T? = onNext(context, Lifecycle.State.CREATED, block)

/**
 * Executes `block` one time, the next time the [Lifecycle]'s state is at least [Lifecycle.State.STARTED].
 *
 * If the lifecycle is already in this state, `block` will be executed immediately.
 *
 * @see [DispatchLifecycleScope.launchOnStart] for repeating behavior.
 * @param context *optional* - additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @sample samples.LifecycleSuspendSample.lifecycleOwnerOnNextStartSample
 */
suspend fun <T> LifecycleOwner.onNextStart(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T? = lifecycle.onNext(context, Lifecycle.State.STARTED, block)

/**
 * Executes `block` one time, the next time the [Lifecycle]'s state is at least [Lifecycle.State.STARTED].
 *
 * If the lifecycle is already in this state, `block` will be executed immediately.
 *
 * @see [DispatchLifecycleScope.launchOnStart] for repeating behavior.
 * @param context *optional* - additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @sample samples.LifecycleSuspendSample.lifecycleOnNextStartSample
 */
suspend fun <T> Lifecycle.onNextStart(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T? = onNext(context, Lifecycle.State.STARTED, block)

/**
 * Executes `block` one time, the next time the [Lifecycle]'s state is at least [Lifecycle.State.RESUMED].
 *
 * If the lifecycle is already in this state, `block` will be executed immediately.
 *
 * @see [DispatchLifecycleScope.launchOnResume] for repeating behavior.
 * @param context *optional* - additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @sample samples.LifecycleSuspendSample.lifecycleOwnerOnNextResumeSample
 */
suspend fun <T> LifecycleOwner.onNextResume(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T? = lifecycle.onNext(context, Lifecycle.State.RESUMED, block)

/**
 * Executes `block` one time, the next time the [Lifecycle]'s state is at least [Lifecycle.State.RESUMED].
 *
 * If the lifecycle is already in this state, `block` will be executed immediately.
 *
 * @see [DispatchLifecycleScope.launchOnResume] for repeating behavior.
 * @param context *optional* - additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @sample samples.LifecycleSuspendSample.lifecycleOnNextResumeSample
 */
suspend fun <T> Lifecycle.onNextResume(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> T
): T? = onNext(context, Lifecycle.State.RESUMED, block)
