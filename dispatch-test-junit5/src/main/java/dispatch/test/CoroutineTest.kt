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

import kotlinx.coroutines.*
import org.junit.jupiter.api.extension.*
import kotlin.reflect.*

/**
 * Annotation for specifying a custom [CoroutineTestExtension.ScopeFactory] while
 * extending a test class or function with [CoroutineTestExtension].
 *
 * @see CoroutineTestExtension
 * @param scopeFactory *optional* KClass which extends [CoroutineTestExtension.ScopeFactory].
 * **This class must have a default constructor**
 * An instance will be automatically initialized inside the [CoroutineTestExtension] and used to create custom [TestProvidedCoroutineScope] instances.
 * @sample samples.CoroutineTestDefaultFactorySample
 * @sample samples.CoroutineTestNamedFactorySample
 */
@ExperimentalCoroutinesApi
@ExtendWith(CoroutineTestExtension::class)
public annotation class CoroutineTest(val scopeFactory: KClass<*> = CoroutineTestExtension.ScopeFactory::class)
