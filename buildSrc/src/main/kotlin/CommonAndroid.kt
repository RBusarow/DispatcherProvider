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

import com.android.build.gradle.*
import org.gradle.api.*
import org.gradle.api.JavaVersion.*
import org.gradle.kotlin.dsl.*
import java.io.*

@Suppress("MagicNumber", "LongMethod")
fun Project.commonAndroid() {

  configure<TestedExtension> {
    compileSdkVersion(30)

    defaultConfig {
      minSdkVersion(21)
      targetSdkVersion(30)
      versionName = "1.0.0-beta09"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
      getByName("release") {
        isMinifyEnabled = false
        proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro"
        )
      }
    }

    compileOptions {
      sourceCompatibility = VERSION_1_8
      targetCompatibility = VERSION_1_8
    }

    lintOptions {
      disable("ObsoleteLintCustomCheck")
      disable("MissingTranslation")
      enable("InvalidPackage")
      enable("Interoperability")
      isAbortOnError = true
      baselineFile = File("$projectDir/lint-baseline.xml")
    }

    testOptions {
      unitTests.isIncludeAndroidResources = true
      unitTests.isReturnDefaultValues = true
      animationsDisabled = true
    }
  }
}
