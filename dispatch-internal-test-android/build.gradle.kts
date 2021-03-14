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

  api(Libs.AndroidX.Fragment.core)
  api(Libs.AndroidX.Lifecycle.common)
  api(Libs.AndroidX.Lifecycle.liveData)
  api(Libs.AndroidX.Lifecycle.runtime)
  api(Libs.Kotlinx.Coroutines.core)
  api(Libs.Kotlinx.Coroutines.coreJvm)
  api(Libs.JUnit.jUnit5Api)

  implementation(Libs.AndroidX.Lifecycle.runtimeKtx)
  implementation(Libs.Kotlin.reflect)
  implementation(Libs.Kotlinx.Coroutines.android)
}
