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

package formatting

import formatting.GradleDependencyVisitor.*
import org.gradle.api.*
import org.jetbrains.kotlin.psi.*
import util.psi.*
import util.psi.visitor.*
import java.io.*
import kotlin.properties.*
import kotlin.system.*

fun Project.sortDependencies() {

  val totalParseTime = measureTimeMillis {

    subprojects.forEach {

      val maybeGradleFile = File("${it.projectDir}/build.gradle.kts")

      if (maybeGradleFile.exists()) {

        val gradle = maybeGradleFile.asKtFile()

        val props = GradleDependencyVisitor().parse(gradle)

        val old = props.oldDependencies ?: "---------------"
        val new = props.newDependencies ?: "---------------"

        val newText = gradle.text.replace(old, new)

        maybeGradleFile.writeText(newText)
      }
    }
  }

  println("total parsing time: $totalParseTime ms")
}

class GradleDependencyVisitor : KotlinVisitor<Properties>() {

  class Properties {
    var root: KtFile by Delegates.notNull()
    var oldDependencies: String? = null
    var newDependencies: String? = null
  }

  override val properties = Properties()

  override fun visitKtFile(file: KtFile) {
    properties.root = file

    super.visitKtFile(file)
  }

  override fun visitCallExpression(expression: KtCallExpression) {
    super.visitCallExpression(expression)

    if (expression.text.startsWith("dependencies {")) {

      val declarations = expression.children
        .mapNotNull { it as? KtLambdaArgument }
        .map { lambdaArg ->
          lambdaArg.children.mapNotNull { it as? KtLambdaExpression }
            .map { lambdaExpression ->
              lambdaExpression.children.mapNotNull { it as? KtExpression }
                .map { ktExpression ->
                  ktExpression.children.mapNotNull { declarationExpression ->
                    properties.oldDependencies = declarationExpression.text
                    declarationExpression as? KtExpression
                  }
                    .map { declarationExpression ->
                      declarationExpression.children.mapNotNull { it as? KtCallExpression }
                        .map { it.text }
                    }
                }
            }
        }
        .flatten()
        .flatten()
        .flatten()
        .flatten()
        .groupBy { it.split("[(.]".toRegex()).take(2).joinToString("-") }

      val comparator = compareBy<String>({ it.startsWith("kapt") }, { it })

      val sortedKeys = declarations.keys.sortedWith(comparator)

      val newDeps = sortedKeys.joinToString("\n\n") { key ->

        declarations.getValue(key)
          .toSet()
          .sortedBy {
            @Suppress("DefaultLocale")
            it.toLowerCase()
          }
          .joinToString("\n") { "  $it" }
      }.trim()

      properties.newDependencies = newDeps
    }
  }
}
