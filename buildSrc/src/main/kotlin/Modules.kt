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

object Modules {

  private val internalRegex = ".*internal.*".toRegex()
  private val sampleRegex = ".*sample.*".toRegex()

  val allPaths = listOf(
    ":dispatch-android-espresso",
    ":dispatch-android-espresso:samples",
    ":dispatch-android-lifecycle",
    ":dispatch-android-lifecycle-extensions",
    ":dispatch-android-lifecycle-extensions:samples",
    ":dispatch-android-lifecycle:samples",
    ":dispatch-android-viewmodel",
    ":dispatch-android-viewmodel:samples",
    ":dispatch-core",
    ":dispatch-core:samples",
    ":dispatch-detekt",
    ":dispatch-internal-test",
    ":dispatch-internal-test-android",
    ":dispatch-sample",
    ":dispatch-test",
    ":dispatch-test-junit4",
    ":dispatch-test-junit4:samples",
    ":dispatch-test-junit5",
    ":dispatch-test-junit5:samples",
    ":dispatch-test:samples",
    ":dispatch-test:samples"
  )
  val allInternalPaths = allPaths.filter { it.matches(internalRegex) }
  val allProductionPaths = allPaths.filter { !it.matches(internalRegex, sampleRegex) }
  val allSamplesPaths = allPaths.filter { it.matches(sampleRegex) }

  val all = allPaths.map { it.removePrefix(":") }
  val allInternal = all.filter { it.matches(internalRegex) }
  val allProduction = all.filter { !it.matches(internalRegex, sampleRegex) }
  val allSamples = all.filter { it.matches(sampleRegex) }
}
