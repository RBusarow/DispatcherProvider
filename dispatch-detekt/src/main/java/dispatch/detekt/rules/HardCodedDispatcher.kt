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

package dispatch.detekt.rules

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.calls.callUtil.*

/**
 * Detects use of a hard-coded reference to the `kotlinx.coroutines.Dispatchers` singleton.
 *
 * The `CoroutineDispatcher`'s in this singleton do not contain a `DispatcherProvider`,
 * so they're unaffected by this library.
 *
 * ## Config
 * - allowDefaultDispatcher
 * - allowIODispatcher
 * - allowMainDispatcher
 * - allowMainImmediateDispatcher
 * - allowUnconfinedDispatcher
 */
public class HardCodedDispatcher(config: Config = Config.empty) : Rule(config) {

  private companion object {
    private const val coroutinesStarImport = "kotlinx.coroutines.*"
    private const val explicitDispatchersImport = "kotlinx.coroutines.Dispatchers"
    private const val dispatchersStarImport = "kotlinx.coroutines.Dispatchers.*"

    private val importCandidates = setOf(
      coroutinesStarImport,
      explicitDispatchersImport,
      "$explicitDispatchersImport.Default",
      "$explicitDispatchersImport.IO",
      "$explicitDispatchersImport.Main",
      "$explicitDispatchersImport.Main.immediate",
      "$explicitDispatchersImport.Unconfined",
      dispatchersStarImport
    )
  }

  /**
   * @suppress
   */
  override val issue: Issue = Issue(
    id = "HardCodedDispatcher",
    severity = Severity.Defect,
    description = "Dispatchers properties do not contain DispatcherProviders and don't work with this library.",
    debt = Debt.FIVE_MINS
  )

  private val allowDefaultDispatcher get() = valueOrDefault("allowDefaultDispatcher", false)
  private val allowIODispatcher get() = valueOrDefault("allowIODispatcher", false)
  private val allowMainDispatcher get() = valueOrDefault("allowMainDispatcher", false)
  private val allowMainImmediateDispatcher
    get() = valueOrDefault("allowMainImmediateDispatcher", false)
  private val allowUnconfinedDispatcher get() = valueOrDefault("allowUnconfinedDispatcher", false)

  private val defaultMatcher = Matcher("Default", allowDefaultDispatcher)
  private val ioMatcher = Matcher("IO", allowIODispatcher)
  private val mainMatcher = Matcher("Main", allowMainDispatcher)
  private val mainImmediateMatcher = Matcher("Main.immediate", allowMainImmediateDispatcher)
  private val unconfinedMatcher = Matcher("Unconfined", allowUnconfinedDispatcher)

  private val matchers = listOf(
    defaultMatcher,
    ioMatcher,
    mainMatcher,
    mainImmediateMatcher,
    unconfinedMatcher
  )

  private val imports = mutableSetOf<String>()

  /**
   * @suppress
   */
  @Suppress("ReturnCount", "ComplexMethod", "NestedBlockDepth")
  override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {

    super.visitDotQualifiedExpression(expression)

    if (expression.isInImportDirective()) return

    if (!expression.isDotReceiver() &&
      expression.text != "Dispatchers.Main.immediate" &&
      matchers.any { matcher -> matcher.matches(expression) }
    ) {
      report(CodeSmell(issue, Entity.from(expression), message(expression.text)))
      return
    }

    if (expression.receiverExpression.text == "Main") {

      val selector = expression.selectorExpression ?: return

      if (mainMatcher.matches(expression) || mainImmediateMatcher.matches(selector)) {
        report(CodeSmell(issue, Entity.from(expression), message(expression.text)))
      }
    } else if (expression.receiverExpression.text == "Dispatchers") {

      val selector = expression.selectorExpression ?: return

      if (selector.text == "Main") {

        selector.parent?.parent?.let { maybeImmediate ->

          if (maybeImmediate.text == "Dispatchers.Main.immediate") {
            report(CodeSmell(issue, Entity.from(expression), message("Dispatchers.Main.immediate")))
          } else {
            report(CodeSmell(issue, Entity.from(expression), message(expression.text)))
          }
        } ?: report(CodeSmell(issue, Entity.from(expression), message(expression.text)))
      }

      if (expression.getCalleeExpressionIfAny()?.text in listOf(
          "Default",
          "IO",
          "Unconfined"
        )
      ) {
        report(CodeSmell(issue, Entity.from(expression), message(expression.text)))
      }
    }
  }

  private fun message(reference: String) =
    "Using Dispatchers singleton reference (`$reference`) instead of a DispatcherProvider property."

  /**
   * @suppress
   */
  override fun visitReferenceExpression(expression: KtReferenceExpression) {
    super.visitReferenceExpression(expression)

    val matches = matchers.any { it.matches(expression) }

    if (expression.isDotReceiver() || expression.isDotSelector()) return

    if (!expression.isInImportDirective() && expression.text != "immediate") {

      if (matches) {
        report(CodeSmell(issue, Entity.from(expression), message(expression.text)))
      }
    }
  }

  /**
   * @suppress
   */
  override fun visitImportDirective(importDirective: KtImportDirective) {
    val importString = importDirective.importPath?.pathStr

    if (importString == null) {
      super.visitImportDirective(importDirective)
      return
    }

    if (importCandidates.contains(importString)) {
      imports.add(importString)
    }
    super.visitImportDirective(importDirective)
  }

  private inner class Matcher(val reference: String, val allowed: Boolean) {

    val fullyQualifiedName = "$explicitDispatchersImport.$reference"

    fun matches(expression: PsiElement): Boolean {

      if (allowed) return false

      val trimmedImports = imports.map { it.removeSuffix(".*") }
        .toSet()

      val text = expression.text

      return text == fullyQualifiedName ||
        expression.parent?.text == fullyQualifiedName ||
        trimmedImports.any { import ->

          ((text == reference && import == fullyQualifiedName) || "$import.$text" == fullyQualifiedName)
        }
    }
  }
}
