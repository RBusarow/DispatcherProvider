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
  id(Plugins.androidApplication)
  kotlin("android")
  id(Plugins.kotlinParcelize)
}

android {
  compileSdkVersion(Versions.compileSdk)

  defaultConfig {
    minSdkVersion(Versions.minSdk)
    targetSdkVersion(Versions.targetSdk)
    versionCode = 1
    versionName = Versions.versionName

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures.viewBinding = true

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {

  androidTestImplementation(Libs.AndroidX.Test.Espresso.core)
  androidTestImplementation(Libs.AndroidX.Test.runner)

  androidTestImplementation(project(":dispatch-android-espresso"))

  implementation(Libs.AndroidX.activity)
  implementation(Libs.AndroidX.appcompat)
  implementation(Libs.AndroidX.constraintLayout)
  implementation(Libs.AndroidX.coreKtx)
  implementation(Libs.AndroidX.Fragment.ktx)
  implementation(Libs.AndroidX.Lifecycle.common)
  implementation(Libs.JakeWharton.timber)
  implementation(Libs.Kotlinx.Coroutines.android)
  implementation(Libs.Kotlinx.Coroutines.core)

  implementation(project(":dispatch-android-lifecycle"))
  implementation(project(":dispatch-android-lifecycle-extensions"))
  implementation(project(":dispatch-android-viewmodel"))
  implementation(project(":dispatch-core"))

  testImplementation(Libs.JUnit.jUnit4)
  testImplementation(Libs.JUnit.jUnit5)
  testImplementation(Libs.Kotest.assertions)
  testImplementation(Libs.Kotest.properties)
  testImplementation(Libs.Kotest.runner)
  testImplementation(Libs.Kotlinx.Coroutines.test)

  testImplementation(project(":dispatch-test-junit4"))
  testImplementation(project(":dispatch-test-junit5"))
}
