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

  implementation(libs.androidx.fragment.core)
  implementation(libs.androidx.fragment.ktx)
  implementation(libs.androidx.lifecycle.common)
  implementation(libs.androidx.lifecycle.liveData)
  implementation(libs.androidx.lifecycle.viewModel)
  implementation(libs.junit.junit4)
  implementation(libs.junit.api)
  implementation(libs.kotlinx.coroutines.core)

  implementation(projects.dispatchAndroidLifecycle)
  implementation(projects.dispatchCore)

  testImplementation(libs.androidx.lifecycle.runtime)
  testImplementation(libs.androidx.arch.test.core)
  testImplementation(libs.androidx.test.espresso.core)
  testImplementation(libs.androidx.test.runner)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotest.assertions)
  testImplementation(libs.kotest.properties)
  testImplementation(libs.kotest.runner)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.kotlin.testCommon)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlinx.knit.test)
  testImplementation(libs.hermit.coroutines)
  testImplementation(libs.hermit.jUnit5)
  testImplementation(libs.robolectric)

  testImplementation(projects.dispatchAndroidLifecycleExtensions)
  testImplementation(projects.dispatchInternalTestAndroid)
  testImplementation(projects.dispatchTest)
  testImplementation(projects.dispatchTestJunit5)

}
