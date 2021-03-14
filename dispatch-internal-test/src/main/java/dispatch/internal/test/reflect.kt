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

package dispatch.internal.test

import kotlin.reflect.full.*
import kotlin.reflect.jvm.*

public inline fun <reified T : Any, reified R : Any> T.getPrivateObjectFieldByName(name: String): R {

  val kClass = T::class

  val property = kClass.members.find { it.name == name }

  require(property != null) { "Cannot find a property named `$name` in ${kClass::qualifiedName}." }

  property.isAccessible = true

  return property.call() as R
}

public inline fun <reified T : Any, reified R : Any> T.getPrivateFieldByName(name: String): R {

  val kClass = T::class

  val property = kClass.memberProperties.find { it.name == name }

  require(property != null) { "Cannot find a property named `$name` in ${kClass::qualifiedName}." }

  property.isAccessible = true

  return property.get(this) as R
}
