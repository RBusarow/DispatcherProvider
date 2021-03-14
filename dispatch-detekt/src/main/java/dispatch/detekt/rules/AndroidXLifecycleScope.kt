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
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*

/**
 * Detects use of `androidx.lifecycle.lifecycleScope`,
 * which shares a namespace with `dispatch.android.lifecycle.lifecycleScope`.
 *
 * The AndroidX library uses a hard-coded `Dispatchers.Main` and does not contain a `DispatcherProvider`,
 * and also leaks its pausing behavior.
 */
public class AndroidXLifecycleScope(config: Config = Config.empty) : Rule(config) {

  /**
   * @suppress
   */
  override val issue: Issue = Issue(
    id = "AndroidXLifecycleScope",
    severity = Severity.Defect,
    description = "The AndroidX lifecycleScope leaks its pausing behavior and uses hard-coded dispatchers.",
    debt = Debt.FIVE_MINS
  )

  /**
   * @suppress
   */
  private companion object {
    private val starImportRegex = "androidx\\.lifecycle\\.\\*".toRegex()
    private val explicitImportRegex = "androidx\\.lifecycle\\.lifecycleScope".toRegex()
    private const val functionName = "lifecycleScope"
  }

  private val calls = mutableListOf<KtReferenceExpression>()
  private var importPresent = false

  /**
   * @suppress
   */
  override fun visitReferenceExpression(expression: KtReferenceExpression) {
    super.visitReferenceExpression(expression)

    if (!expression.isInImportDirective() && expression.textMatches(functionName)) {

      calls.add(expression)

      if (importPresent) {

        val finding = CodeSmell(
          issue = issue,
          entity = Entity.from(expression),
          message = "Using androidx.lifecycle.lifecycleScope instead of dispatch.lifecycle.lifecycleScope."
        )

        report(finding)
      }
    }
  }

  /**
   * @suppress
   */
  override fun visitImportDirective(importDirective: KtImportDirective) {

    val importString = importDirective.importPath?.pathStr

    if (importString?.matches(explicitImportRegex) == true || importString?.matches(
        starImportRegex
      ) == true
    ) {

      importPresent = true

      calls.forEach { call ->

        val finding = CodeSmell(
          issue = issue,
          entity = Entity.from(call),
          message = "Using androidx.lifecycle.lifecycleScope instead of dispatch.android.lifecycle.lifecycleScope."
        )

        report(finding)
      }
    }
    super.visitImportDirective(importDirective)
  }
}
