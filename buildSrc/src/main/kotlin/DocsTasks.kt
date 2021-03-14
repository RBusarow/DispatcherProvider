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

@file:Suppress("TooManyFunctions", "MagicNumber")

import org.gradle.api.*
import java.io.*

fun cleanDocs() {

  val root = File("docs")

  root.walkTopDown()
    .forEach {
      if (it != root) {
        it.deleteRecursively()
      }
    }
}

fun copyRootFiles() {

  val root = File(".")

  val markdown = "./(.*).md".toRegex()
  val readMe = "./README.md".toRegex()

  root.walkTopDown()
    .maxDepth(1)
    .filter { file ->
      file.path.matches(markdown)
    }
    .forEach { file ->

      val newName = if (file.path.matches(readMe)) {

        file.updateLibraryVersions()
        "docs/index.md"
      } else {
        file.path.replace(markdown) { m1 ->
          "docs/$m1.md"
        }
      }

      file.copyTo(File(newName))
    }
}

fun Project.copyKdoc() {

  mkdir("$rootDir/docs/api")

  val root = File("$buildDir/dokka/${projectDir.name}")

  if (root.exists()) {
    root.copyRecursively(File("$rootDir/docs/api/${projectDir.name}"), overwrite = true)
  }
}

fun Project.copyReadMe() {

  mkdir("$rootDir/docs")

  val readme = File("$projectDir/README.md")

  if (readme.exists()) {

    val newName = "$rootDir/docs/${projectDir.name}.md"

    readme.copyTo(File(newName))
  }
}

fun Project.fixDocsReferencePaths() {

  val pathRegex = """https://rbusarow.github.io/Dispatch(.*)""".toRegex()

  rootDir.walkTopDown()
    .filter { file ->
      file.path.endsWith(".md")
    }.forEach { file ->

      val matches = pathRegex.findAll(file.readText())

      val newText = matches.fold(file.readText()) { acc, matchResult ->

        val oldPath = matchResult.destructured.component1()
        val newPath = oldPath.replace("//", "/")
        acc.replace(oldPath, newPath)
      }

      file.writeText(newText)
    }
}

fun Project.copySite() {

  val root = File("$rootDir/site")

  root.walkTopDown()
    .maxDepth(1)
    .forEach { file ->

      if (!file.name.matches(".*mkdocs.yml".toRegex()) && file != root) {
        file.copyRecursively(File("$rootDir/docs/api"), true)
      }
    }
}

fun Project.updateReadMeArtifactVersions() {

  val regex = "$projectDir/README.md".toRegex()

  projectDir.walkTopDown()
    // A depth of 3 allows for nested modules to introduce their README's.
    // This probably won't ever be necessary but it's like 10ms.
    .maxDepth(3)
    .filter { file ->

      file.path.matches(regex)
    }
    .forEach { file ->

      file.updateLibraryVersions()
    }
}

fun File.updateLibraryVersions(): File {

  val new = createTempFile()

  new.printWriter()
    .use { writer ->

      val dependencyMatchers = listOf(
        Libs.AndroidX.Lifecycle.common.toDependencyMatcher(),
        Libs.AndroidX.Lifecycle.extensions.toDependencyMatcher(),
        Libs.AndroidX.Lifecycle.liveData.toDependencyMatcher(),
        Libs.AndroidX.Lifecycle.runtime.toDependencyMatcher(),
        Libs.AndroidX.Lifecycle.viewModel.toDependencyMatcher(),
        Libs.AndroidX.Test.runner.toDependencyMatcher(),
        Libs.AndroidX.Test.Espresso.core.toDependencyMatcher(),
        Libs.Detekt.api.toDependencyMatcher(),
        Libs.Detekt.cli.toDependencyMatcher(),
        Libs.Detekt.formatting.toDependencyMatcher(),
        Libs.Detekt.test.toDependencyMatcher(),
        Libs.JUnit.jUnit4.toDependencyMatcher(),
        Libs.JUnit.jUnit5Vintage.toDependencyMatcher(),
        Libs.JUnit.jUnit5Runtime.toDependencyMatcher(),
        Libs.JUnit.jUnit5Params.toDependencyMatcher(),
        Libs.JUnit.jUnit5Api.toDependencyMatcher(),
        Libs.JUnit.jUnit5.toDependencyMatcher(),
        Libs.Kotlinx.Coroutines.core.toDependencyMatcher(),
        Libs.Kotlinx.Coroutines.android.toDependencyMatcher(),
        Libs.Kotlinx.Coroutines.test.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.detekt.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.espresso.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.lifecycle.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.lifecycleExtensions.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.viewModel.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.Test.core.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.Test.jUnit4.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.Test.jUnit5.toDependencyMatcher(),
        Libs.RickBusarow.Dispatch.core.toDependencyMatcher(),
        Libs.RickBusarow.Hermit.core.toDependencyMatcher(),
        Libs.RickBusarow.Hermit.junit4.toDependencyMatcher(),
        Libs.RickBusarow.Hermit.junit5.toDependencyMatcher(),
        Libs.RickBusarow.Hermit.mockk.toDependencyMatcher(),
        Libs.RickBusarow.Hermit.coroutines.toDependencyMatcher(),
        Libs.Kotest.assertions.toDependencyMatcher(),
        Libs.Kotest.properties.toDependencyMatcher(),
        Libs.Kotest.runner.toDependencyMatcher()
      )

      forEachLine { originalLine ->

        val newLine = dependencyMatchers.firstOrNull { matcher ->

          matcher.regex.matches(originalLine)
        }
          ?.let { matcher ->

            originalLine.replace(matcher)
          } ?: originalLine

        writer.println(newLine)
      }
    }

  check(delete() && new.renameTo(this)) { "failed to replace file" }

  return new
}

/**
 * examples:
 * ```
 * (.*)(['\"])com.rickbusarow.dispatch:dispatch-core*(.*)(['\"])(.*)
 * (.*)(['\"])androidx.test:runner.*(['"])(.*)
 * (.*)(['\"])androidx.test.espresso:espresso-core.*(['"])(.*)
 * ```
 */
private fun String.toMavenDependencyRegex() =
  "(.*)(['\"])${this.removeVersionSuffix()}.*(['\"])(.*)".toRegex()

private data class DependencyMatcher(val fullCoordinate: String, val regex: Regex)

private fun String.toDependencyMatcher() = DependencyMatcher(
  fullCoordinate = this,
  regex = this.toMavenDependencyRegex()
)

private fun String.replace(
  dependencyMatcher: DependencyMatcher
): String = this.replace(dependencyMatcher.regex) { m1, m2, m3, m4 ->
  "$m1$m2${dependencyMatcher.fullCoordinate}$m3$m4"
}

private fun String.removeVersionSuffix(): String = split(":").subList(0, 2)
  .joinToString(":") + ":"
