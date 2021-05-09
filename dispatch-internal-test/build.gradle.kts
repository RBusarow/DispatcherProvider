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
  id("kotlinx-atomicfu")
  javaLibrary
}

dependencies {

  api(libs.junit.junit4)
  api(libs.junit.api)
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.coroutines.jvm)

  implementation(libs.junit.jupiter)
  implementation(libs.junit.vintage)
  implementation(libs.kotest.assertions)
  implementation(libs.kotest.assertionsShared)
  implementation(libs.kotest.common.jvm)
  implementation(libs.kotest.runner)
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlin.test)
  implementation(libs.kotlin.testCommon)
  implementation(libs.kotlinx.coroutines.test)

  implementation(projects.dispatchCore)

}
