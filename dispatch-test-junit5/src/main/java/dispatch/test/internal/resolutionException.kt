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

import dispatch.test.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.extension.*
import kotlin.reflect.*

@ExperimentalCoroutinesApi
internal fun resolutionException(factoryClass: KClass<*>): ParameterResolutionException {
  return ParameterResolutionException(
    """A ${CoroutineTest::class.simpleName} annotation was found with an incompatible factory type.
              |
              |The specified factory must be <${CoroutineTestExtension.ScopeFactory::class.qualifiedName}> or a subtype.
              |
              |The provided factory type was:  <${factoryClass.qualifiedName}>
              |""".trimMargin()
  )
}
