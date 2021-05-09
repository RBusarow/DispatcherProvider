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

import org.gradle.api.*
import org.gradle.api.tasks.testing.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.*

fun Project.common() {

  tasks.withType<Test> {

    useJUnitPlatform {
      includeEngines("junit-vintage", "junit-jupiter")
    }
  }

  tasks.withType<KotlinCompile>()
    .configureEach {

      kotlinOptions {

        allWarningsAsErrors = true

        jvmTarget = "1.8"
      }
    }

  // force update all transitive dependencies (prevents some library leaking an old version)
  configurations.all {
    resolutionStrategy {
      force(
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3",
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3",
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3"
      )
      eachDependency {
        when {
          requested.group == "org.jetbrains.kotlin" -> useVersion("1.5.0")
        }
      }
    }
  }
}
