/*
 * Copyright (C) 2020 Rick Busarow
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

@file:Suppress("MagicNumber")

import io.gitlab.arturbosch.detekt.*
import kotlinx.knit.*
import kotlinx.validation.*
import org.jetbrains.dokka.gradle.*
import org.jetbrains.kotlin.gradle.tasks.*

buildscript {
  repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    google()
    jcenter()
    gradlePluginPortal()
    maven("https://dl.bintray.com/kotlin/kotlinx")
  }
  dependencies {

    classpath(BuildPlugins.androidGradlePlugin)
    classpath(BuildPlugins.atomicFu)
    classpath(BuildPlugins.benManesVersions)
    classpath(BuildPlugins.binaryCompatibility)
    classpath(BuildPlugins.kotlinGradlePlugin)
    classpath(BuildPlugins.gradleMavenPublish)
    classpath(BuildPlugins.knit)
  }
}

plugins {
  id("io.gitlab.arturbosch.detekt") version Libs.Detekt.version
  kotlin("jvm") version Versions.kotlin
  id(Plugins.dokka) version Versions.dokka
}

apply(plugin = "base")

allprojects {

  repositories {
    mavenCentral()
    google()
    jcenter()
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }
}

tasks.dokkaHtmlMultiModule.configure {

  outputDirectory.set(buildDir.resolve("dokka"))

  // missing from 1.4.10  https://github.com/Kotlin/dokka/issues/1530
  // documentationFileName.set("README.md")
}

subprojects {

  tasks.withType<DokkaTask>().configureEach {

    dependsOn(allprojects.mapNotNull { it.tasks.findByName("assemble") })

    outputDirectory.set(buildDir.resolve("dokka"))

    dokkaSourceSets.configureEach {

      jdkVersion.set(8)
      reportUndocumented.set(true)
      skipEmptyPackages.set(true)
      noAndroidSdkLink.set(false)

      samples.from(files("samples"))

      if (File("$projectDir/README.md").exists()) {
        includes.from(files("README.md"))
      }

      sourceLink {

        val modulePath = this@subprojects.path.replace(":", "/").replaceFirst("/", "")

        // Unix based directory relative path to the root of the project (where you execute gradle respectively).
        localDirectory.set(file("src/main"))

        // URL showing where the source code can be accessed through the web browser
        remoteUrl.set(uri("https://github.com/RBusarow/Dispatch/blob/main/$modulePath/src/main").toURL())
        // Suffix which is used to append the line number to the URL. Use #L for GitHub
        remoteLineSuffix.set("#L")
      }
    }
  }
}
subprojects {
  @Suppress("UNUSED_VARIABLE")
  val buildDocs by tasks.registering {

    description = "recreates all documentation for the /docs directory"
    group = "documentation"

    doFirst {
      updateReadMeArtifactVersions()
    }

    dependsOn(
      rootProject.tasks.findByName("cleanDocs"),
      rootProject.tasks.findByName("copyRootFiles"),
      rootProject.tasks.findByName("knit")
    )

    doLast {
      copyKdoc()
      copyReadMe()
    }
  }
}

subprojects {
  tasks.withType<KotlinCompile>()
    .configureEach {

      kotlinOptions {
        allWarningsAsErrors = true

        jvmTarget = "1.8"

        // https://youtrack.jetbrains.com/issue/KT-24946
        // freeCompilerArgs = listOf(
        //     "-progressive",
        //     "-Xskip-runtime-version-check",
        //     "-Xdisable-default-scripting-plugin",
        //     "-Xuse-experimental=kotlin.Experimental"
        // )
      }
    }
}

val cleanDocs by tasks.registering {

  description = "cleans /docs"
  group = "documentation"

  doLast {
    cleanDocs()
  }
}

val copyRootFiles by tasks.registering {

  description = "copies documentation files from the project root into /docs"
  group = "documentation"

  dependsOn("cleanDocs")

  doLast {
    copySite()
    copyRootFiles()
  }
}

detekt {
  parallel = true
  config = files("$rootDir/detekt/detekt-config.yml")

  val unique = "${rootProject.relativePath(projectDir)}/${project.name}"

  reports {
    xml {
      enabled = false
      destination = file("$rootDir/build/detekt-reports/$unique-detekt.xml")
    }
    html {
      enabled = true
      destination = file("$rootDir/build/detekt-reports/$unique-detekt.html")
    }
    txt {
      enabled = false
      destination = file("$rootDir/build/detekt-reports/$unique-detekt.txt")
    }
  }
}

dependencies {
  detekt(Libs.Detekt.cli)
  detektPlugins(project(path = ":dispatch-detekt"))
}

apply(plugin = Plugins.binaryCompatilibity)

extensions.configure<ApiValidationExtension> {

  /**
   * Packages that are excluded from public API dumps even if they
   * contain public API.
   */
  ignoredPackages = mutableSetOf("sample", "samples")

  /**
   * Sub-projects that are excluded from API validation
   */
  ignoredProjects = mutableSetOf(
    "dispatch-internal-test",
    "dispatch-internal-test-android",
    "dispatch-sample",
    "samples"
  )
}

apply(plugin = Plugins.knit)

extensions.configure<KnitPluginExtension> {

  rootDir = rootProject.rootDir
  moduleRoots = listOf(".")

  moduleDocs = "build/dokka"
  moduleMarkers = listOf("build.gradle", "build.gradle.kts")
  siteRoot = "https://rbusarow.github.io/Dispatch/api"
}

// Build API docs for all modules with dokka before running Knit
tasks.withType<KnitTask> {
  dependsOn(allprojects.mapNotNull { it.tasks.findByName("dokkaHtml") })
  doLast {
    fixDocsReferencePaths()
  }
}

val generateDependencyGraph by tasks.registering {

  description = "generate a visual dependency graph"
  group = "refactor"

  doLast {
    createDependencyGraph()
  }
}

subprojects {

  // force update all transitive dependencies (prevents some library leaking an old version)
  configurations.all {
    resolutionStrategy {
      force(
        // androidx is currently leaking coroutines 1.1.1 everywhere
        Libs.Kotlinx.Coroutines.core,
        Libs.Kotlinx.Coroutines.test,
        Libs.Kotlinx.Coroutines.android,
        // prevent dependency libraries from leaking their own old version of this library
        Libs.RickBusarow.Dispatch.core,
        Libs.RickBusarow.Dispatch.detekt,
        Libs.RickBusarow.Dispatch.espresso,
        Libs.RickBusarow.Dispatch.lifecycle,
        Libs.RickBusarow.Dispatch.lifecycleExtensions,
        Libs.RickBusarow.Dispatch.viewModel,
        Libs.RickBusarow.Dispatch.Test.core,
        Libs.RickBusarow.Dispatch.Test.jUnit4,
        Libs.RickBusarow.Dispatch.Test.jUnit5
      )
    }
  }
}
