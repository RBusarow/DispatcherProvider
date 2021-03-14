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

import org.junit.jupiter.api.extension.*
import java.lang.reflect.*

internal inline fun <reified T : Annotation> ExtensionContext.getAnnotationRecursive(): T? {

  return this.elementOrNull()
    ?.getAnnotation(T::class.java) ?: parentOrNull()?.getAnnotationRecursive(T::class.java)
}

internal fun <T : Annotation> ExtensionContext.getAnnotationRecursive(aClass: Class<T>): T? {

  return this.elementOrNull()
    ?.getAnnotation(aClass) ?: parentOrNull()?.getAnnotationRecursive(aClass)
}

@Suppress("NewApi")
internal fun ExtensionContext.elementOrNull(): AnnotatedElement? = if (element.isPresent) {
  element.get()
} else {
  null
}

@Suppress("NewApi")
internal inline fun <reified T : Annotation> Parameter.annotationOrNull(): T? =
  getAnnotation(T::class.java) ?: null

@Suppress("NewApi")
internal fun ExtensionContext.parentOrNull(): ExtensionContext? = if (parent.isPresent) {
  parent.get()
} else {
  null
}
