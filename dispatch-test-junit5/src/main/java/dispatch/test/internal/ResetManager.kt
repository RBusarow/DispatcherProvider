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

package dispatch.test.internal

internal class LazyResets<out T : Any>(
  private val resetManager: ResetManager,
  private val valueFactory: () -> T
) : Lazy<T>,
  Resets {

  private var lazyHolder: Lazy<T> = createLazy()

  override val value: T
    get() = lazyHolder.value

  override fun isInitialized(): Boolean = lazyHolder.isInitialized()

  private fun createLazy() = lazy {
    resetManager.register(this)
    valueFactory()
  }

  override fun reset() {
    lazyHolder = createLazy()
  }
}

internal interface Resets {
  fun reset()
}

internal class ResetManager(
  private val delegates: MutableCollection<Resets> = mutableListOf()
) {

  fun register(delegate: Resets) {
    synchronized(delegates) {
      delegates.add(delegate)
    }
  }

  fun resetAll() {
    synchronized(delegates) {
      delegates.forEach { it.reset() }
      delegates.clear()
    }
  }
}

internal inline fun <reified T : Any> ResetManager.resets(
  noinline valueFactory: () -> T
): LazyResets<T> = LazyResets(this, valueFactory)
