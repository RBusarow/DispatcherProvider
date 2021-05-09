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
  javaLibrary
  id("com.vanniktech.maven.publish")
  id("org.jetbrains.dokka")
}

dependencies {

  api(libs.detekt.api)
  api(libs.kotlin.compiler)

  testImplementation(libs.detekt.api)
  testImplementation(libs.detekt.test)
  testImplementation(libs.junit.api)
  testImplementation(libs.kotest.assertions)
  testImplementation(libs.kotest.properties)
  testImplementation(libs.kotest.runner)

  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.junit.vintage)
}
