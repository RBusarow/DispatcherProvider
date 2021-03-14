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

object Plugins {

  const val androidApplication = "com.android.application"
  const val androidLibrary = "com.android.library"

  const val atomicFu = "kotlinx-atomicfu"
  const val binaryCompatilibity = "binary-compatibility-validator"

  const val dependencyAnalysis = "com.autonomousapps.dependency-analysis"
  const val detekt = "io.gitlab.arturbosch.detekt"
  const val gradleDoctor = "com.osacky.doctor"

  const val dokka = "org.jetbrains.dokka"
  const val knit = "kotlinx-knit"

  const val javaLibrary = "java-library"

  const val kotlin = "kotlin"
  const val kotlinAndroid = "kotlin-android"
  const val kotlinParcelize = "kotlin-parcelize"

  const val mavenPublish = "com.vanniktech.maven.publish"
  const val taskTree = "com.dorongold.task-tree"
  const val spotless = "com.diffplug.spotless"
  const val benManes = "com.github.ben-manes.versions"
}

object Versions {
  const val ktlint = "0.35.0"
  const val dokka = "1.4.20"
  const val dependencyAnalysis = "0.71.0"
  const val knit = "0.2.3"
  const val gradleDoctor = "0.7.0"

  const val compileSdk = 29
  const val minSdk = "21"
  const val targetSdk = 29

  const val binaryCompatibility = "0.4.0"
  const val benManes = "0.38.0"
  const val gradleWrapper = "4.1.2"
  const val dagger = "2.25.2"
  const val kotlin = "1.4.31"
  const val mavenPublish = "0.13.0"

  const val spotless = "5.11.0"
  const val taskTree = "1.5"
  const val versionName = "1.0.0-beta09"
}

object BuildPlugins {

  const val gradleMavenPublish =
    "com.vanniktech:gradle-maven-publish-plugin:${Versions.mavenPublish}"
  const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}"
  const val knit = "org.jetbrains.kotlinx:kotlinx-knit:${Versions.knit}"

  const val binaryCompatibility =
    "org.jetbrains.kotlinx:binary-compatibility-validator:${Versions.binaryCompatibility}"
  const val atomicFu = "org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.14.4"

  const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.gradleWrapper}"
  const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
  const val benManesVersions = "com.github.ben-manes:gradle-versions-plugin:${Versions.benManes}"

  const val androidApplication = "com.android.application"
  const val kotlinAndroid = "kotlin-android"
  const val kotlinAndroidExtensions = "kotlin-android-extensions"
}

object Libs {

  object AndroidX {
    const val activity = "androidx.activity:activity-ktx:1.2.0"
    const val appcompat = "androidx.appcompat:appcompat:1.2.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val coreKtx = "androidx.core:core-ktx:1.3.2"

    object Fragment {

      private const val version = "1.3.0"

      const val core = "androidx.fragment:fragment:$version"
      const val ktx = "androidx.fragment:fragment-ktx:$version"
      const val testing = "androidx.fragment:fragment-testing:$version"
    }

    object Lifecycle {

      private const val version = "2.3.0"

      const val common = "androidx.lifecycle:lifecycle-common:$version"
      const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
      const val liveData = "androidx.lifecycle:lifecycle-livedata-core:$version"
      const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
      const val runtime = "androidx.lifecycle:lifecycle-runtime:$version"
      const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
      const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:$version"
      const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
    }

    object Test {
      private const val version = "1.3.0"

      const val core = "androidx.test:core:$version"
      const val jUnit = "androidx.test.ext:junit:1.1.2"
      const val orchestrator = "androidx.test:orchestrator:$version"
      const val rules = "androidx.test:rules:$version"
      const val runner = "androidx.test:runner:$version"
      const val truth = "androidx.test.ext:truth:$version"

      object Arch {
        const val core = "androidx.arch.core:core-testing:2.1.0"
      }

      object Espresso {

        private const val version = "3.3.0"

        const val contrib = "androidx.test.espresso:espresso-contrib:$version"
        const val core = "androidx.test.espresso:espresso-core:$version"
        const val idlingResource = "androidx.test.espresso:espresso-idling-resource:$version"
        const val intents = "androidx.test.espresso:espresso-intents:$version"
        const val web = "androidx.test.espresso:espresso-web:$version"
      }
    }
  }

  object Dagger {
    private const val version = "2.25.2"
    const val android = "com.google.dagger:dagger-android:$version"
    const val androidKapt = "com.google.dagger:dagger-android-processor:$version"
    const val androidSupport = "com.google.dagger:dagger-android-support:$version"
    const val compiler = "com.google.dagger:dagger-compiler:$version"
    const val core = "com.google.dagger:dagger:$version"
  }

  object Detekt {
    const val version = "1.15.0"
    const val api = "io.gitlab.arturbosch.detekt:detekt-api:$version"
    const val cli = "io.gitlab.arturbosch.detekt:detekt-cli:$version"
    const val formatting = "io.gitlab.arturbosch.detekt:detekt-formatting:$version"
    const val test = "io.gitlab.arturbosch.detekt:detekt-test:$version"
  }

  object JakeWharton {
    const val timber = "com.jakewharton.timber:timber:4.7.1"
  }

  object JUnit {
    const val jUnit4 = "junit:junit:4.13.2"

    private const val version = "5.7.1"

    const val jUnit5 = "org.junit.jupiter:junit-jupiter:$version"
    const val jUnit5Api = "org.junit.jupiter:junit-jupiter-api:$version"
    const val jUnit5Params = "org.junit.jupiter:junit-jupiter-params:$version"
    const val jUnit5Runtime = "org.junit.jupiter:junit-jupiter-engine:$version"
    const val jUnit5Vintage = "org.junit.vintage:junit-vintage-engine:$version"
  }

  object Kotest {
    private const val version = "4.4.2"
    const val assertions = "io.kotest:kotest-assertions-core-jvm:$version"
    const val assertionsShared = "io.kotest:kotest-assertions-shared-jvm:$version"
    const val commonJvm = "io.kotest:kotest-common-jvm:$version"
    const val properties = "io.kotest:kotest-property-jvm:$version"
    const val runner = "io.kotest:kotest-runner-junit5-jvm:$version"
  }

  object Kotlin {
    const val compiler = "org.jetbrains.kotlin:kotlin-compiler-embeddable:${Versions.kotlin}"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}"
    const val test = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    const val testCommon = "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
  }

  object Kotlinx {

    object Coroutines {
      private const val version = "1.4.3"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
      const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
      const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Knit {
      const val test = "org.jetbrains.kotlinx:kotlinx-knit-test:${Versions.knit}"
    }
  }

  object MockK {
    const val core = "io.mockk:mockk:1.10.6"
  }

  object RickBusarow {

    object Dispatch {

      const val core = "com.rickbusarow.dispatch:dispatch-core:${Versions.versionName}"
      const val detekt = "com.rickbusarow.dispatch:dispatch-detekt:${Versions.versionName}"
      const val espresso =
        "com.rickbusarow.dispatch:dispatch-android-espresso:${Versions.versionName}"
      const val lifecycle =
        "com.rickbusarow.dispatch:dispatch-android-lifecycle:${Versions.versionName}"
      const val lifecycleExtensions =
        "com.rickbusarow.dispatch:dispatch-android-lifecycle-extensions:${Versions.versionName}"
      const val viewModel =
        "com.rickbusarow.dispatch:dispatch-android-viewmodel:${Versions.versionName}"

      object Test {
        const val core = "com.rickbusarow.dispatch:dispatch-test:${Versions.versionName}"
        const val jUnit4 = "com.rickbusarow.dispatch:dispatch-test-junit4:${Versions.versionName}"
        const val jUnit5 = "com.rickbusarow.dispatch:dispatch-test-junit5:${Versions.versionName}"
      }
    }

    object Hermit {
      private const val version = "0.9.2"
      const val core = "com.rickbusarow.hermit:hermit-core:$version"
      const val junit4 = "com.rickbusarow.hermit:hermit-junit4:$version"
      const val junit5 = "com.rickbusarow.hermit:hermit-junit5:$version"
      const val mockk = "com.rickbusarow.hermit:hermit-mockk:$version"
      const val coroutines = "com.rickbusarow.hermit:hermit-coroutines:$version"
    }
  }

  object Robolectric {
    const val core = "org.robolectric:robolectric:4.5.1"
  }
}
