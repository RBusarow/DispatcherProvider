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

/**
 * [CoroutineScope] instance for the [LifecycleOwner].
 * By default, it uses the [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate] dispatcher.
 *
 * The `lifecycleScope` instance is created automatically upon first access,
 * from the factory set in [LifecycleScopeFactory].
 *
 * The type of [CoroutineScope] created is configurable via [LifecycleScopeFactory.set].
 *
 * The `viewModelScope` is automatically cancelled when the [LifecycleOwner]'s
 * [lifecycle][LifecycleOwner.getLifecycle]'s [Lifecycle.State] drops to [Lifecycle.State.DESTROYED].
 *
 * @sample samples.LifecycleScopeExtensionSample.lifecycleScopeExtensionSample
 */
@Deprecated(
  "Use dispatchLifecycleScope to avoid collisions with the Androidx library",
  ReplaceWith("dispatchLifecycleScope")
)
val LifecycleOwner.lifecycleScope: DispatchLifecycleScope
  get() = dispatchLifecycleScope

/**
 * [CoroutineScope] instance for the [LifecycleOwner].
 * By default, it uses the [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate] dispatcher.
 *
 * The `lifecycleScope` instance is created automatically upon first access,
 * from the factory set in [LifecycleScopeFactory].
 *
 * The type of [CoroutineScope] created is configurable via [LifecycleScopeFactory.set].
 *
 * The `viewModelScope` is automatically cancelled when the [LifecycleOwner]'s
 * [lifecycle][LifecycleOwner.getLifecycle]'s [Lifecycle.State] drops to [Lifecycle.State.DESTROYED].
 *
 * @sample samples.LifecycleScopeExtensionSample.lifecycleScopeExtensionSample
 */
val LifecycleOwner.dispatchLifecycleScope: DispatchLifecycleScope
  get() = DispatchLifecycleScopeStore.get(this.lifecycle)
