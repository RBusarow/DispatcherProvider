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

plugins {
  androidLibrary
}

dependencies {
  implementation(Libs.Kotlinx.Coroutines.coreJvm)

  implementation(project(":dispatch-core"))

  testImplementation(Libs.AndroidX.Test.Espresso.core)
  testImplementation(Libs.AndroidX.Test.runner)
  testImplementation(Libs.JUnit.jUnit4)
  testImplementation(Libs.Kotest.assertions)
  testImplementation(Libs.Kotest.properties)
  testImplementation(Libs.Kotest.runner)
  testImplementation(Libs.Kotlin.test)
  testImplementation(Libs.Kotlin.testCommon)
  testImplementation(Libs.Kotlinx.Coroutines.core)
  testImplementation(Libs.Kotlinx.Coroutines.test)
  testImplementation(Libs.Robolectric.core)

  testImplementation(project(":dispatch-android-espresso"))
  testImplementation(project(":dispatch-android-lifecycle-extensions"))
  testImplementation(project(":dispatch-android-viewmodel"))
  testImplementation(project(":dispatch-internal-test"))

}
