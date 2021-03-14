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

import io.gitlab.arturbosch.detekt.test.*
import io.kotest.core.spec.style.*
import io.kotest.data.*
import io.kotest.matchers.*
import io.kotest.property.*
import io.kotest.property.exhaustive.*

internal class HardCodedDispatcherTest : FreeSpec(
  {

    fun findingMessage(expression: String) =
      "Using Dispatchers singleton reference (`$expression`) instead of a DispatcherProvider property."

    "positive cases" {

      forAll(
        row("import kotlinx.coroutines.Dispatchers.Default", "Default"),
        row("import kotlinx.coroutines.Dispatchers.IO", "IO"),
        row("import kotlinx.coroutines.Dispatchers.Main", "Main"),
        row("import kotlinx.coroutines.Dispatchers.Main", "Main.immediate"),
        row("import kotlinx.coroutines.Dispatchers.Unconfined", "Unconfined"),
        row("import kotlinx.coroutines.Dispatchers.*", "Default"),
        row("import kotlinx.coroutines.Dispatchers.*", "IO"),
        row("import kotlinx.coroutines.Dispatchers.*", "Main"),
        row("import kotlinx.coroutines.Dispatchers.*", "Main.immediate"),
        row("import kotlinx.coroutines.Dispatchers.*", "Unconfined"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.Default"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.IO"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.Main"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.Main.immediate"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.Unconfined"),
        row("import kotlinx.coroutines.*", "Dispatchers.Default"),
        row("import kotlinx.coroutines.*", "Dispatchers.IO"),
        row("import kotlinx.coroutines.*", "Dispatchers.Main"),
        row("import kotlinx.coroutines.*", "Dispatchers.Main.immediate"),
        row("import kotlinx.coroutines.*", "Dispatchers.Unconfined"),
        row("", "kotlinx.coroutines.Dispatchers.Default"),
        row("", "kotlinx.coroutines.Dispatchers.IO"),
        row("", "kotlinx.coroutines.Dispatchers.Main"),
        row("", "kotlinx.coroutines.Dispatchers.Main.immediate"),
        row("", "kotlinx.coroutines.Dispatchers.Unconfined")
      ) { import, expression ->

        val code = """
            |package a.b.c
            |
            |$import
            |
            |class MyClass {
            |
            |  suspend fun bad() = withContext($expression) { }
            |
            |}
            |
          """.trimMargin()

        val rule = HardCodedDispatcher()

        val findings = rule.lint(code)

        findings.messages shouldBe listOf(findingMessage(expression))
      }
    }

    "importing without using it should not report finding" {

      forAll(
        row("import kotlinx.coroutines.Dispatchers.Default"),
        row("import kotlinx.coroutines.Dispatchers.IO"),
        row("import kotlinx.coroutines.Dispatchers.Main"),
        row("import kotlinx.coroutines.Dispatchers.Main"),
        row("import kotlinx.coroutines.Dispatchers.Unconfined"),
        row("import kotlinx.coroutines.Dispatchers.*"),
        row("import kotlinx.coroutines.Dispatchers.*"),
        row("import kotlinx.coroutines.Dispatchers.*"),
        row("import kotlinx.coroutines.Dispatchers.*"),
        row("import kotlinx.coroutines.Dispatchers.*"),
        row("import kotlinx.coroutines.Dispatchers"),
        row("import kotlinx.coroutines.Dispatchers"),
        row("import kotlinx.coroutines.Dispatchers"),
        row("import kotlinx.coroutines.Dispatchers"),
        row("import kotlinx.coroutines.Dispatchers"),
        row("import kotlinx.coroutines.*"),
        row("import kotlinx.coroutines.*"),
        row("import kotlinx.coroutines.*"),
        row("import kotlinx.coroutines.*"),
        row("import kotlinx.coroutines.*")
      ) { import ->

        val code = """
            |package a.b.c
            |
            |$import
            |
            |class MyClass {
            |
            |  suspend fun bad() = withContext(MyDispatcher) { }
            |
            |}
            |
          """.trimMargin()

        val rule = HardCodedDispatcher()

        val findings = rule.lint(code)

        findings.messages shouldBe emptyList()
      }
    }

    "issue id should be AndroidXLifecycleScope" {

      val rule = HardCodedDispatcher()

      rule.issue.id shouldBe "HardCodedDispatcher"
    }

    "issue should not be reported if suppressing HardCodedDispatcher" {

      forAll(
        row("import kotlinx.coroutines.Dispatchers.Default", "Default"),
        row("import kotlinx.coroutines.Dispatchers.IO", "IO"),
        row("import kotlinx.coroutines.Dispatchers.Main", "Main"),
        row("import kotlinx.coroutines.Dispatchers.Main", "Main.immediate"),
        row("import kotlinx.coroutines.Dispatchers.Unconfined", "Unconfined"),
        row("import kotlinx.coroutines.Dispatchers.*", "Default"),
        row("import kotlinx.coroutines.Dispatchers.*", "IO"),
        row("import kotlinx.coroutines.Dispatchers.*", "Main"),
        row("import kotlinx.coroutines.Dispatchers.*", "Main.immediate"),
        row("import kotlinx.coroutines.Dispatchers.*", "Unconfined"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.Default"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.IO"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.Main"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.Main.immediate"),
        row("import kotlinx.coroutines.Dispatchers", "Dispatchers.Unconfined"),
        row("import kotlinx.coroutines.*", "Dispatchers.Default"),
        row("import kotlinx.coroutines.*", "Dispatchers.IO"),
        row("import kotlinx.coroutines.*", "Dispatchers.Main"),
        row("import kotlinx.coroutines.*", "Dispatchers.Main.immediate"),
        row("import kotlinx.coroutines.*", "Dispatchers.Unconfined"),
        row("", "kotlinx.coroutines.Dispatchers.Default"),
        row("", "kotlinx.coroutines.Dispatchers.IO"),
        row("", "kotlinx.coroutines.Dispatchers.Main"),
        row("", "kotlinx.coroutines.Dispatchers.Main.immediate"),
        row("", "kotlinx.coroutines.Dispatchers.Unconfined")
      ) { import, expression ->

        val code = """
        |@file:Suppress("HardCodedDispatcher")
        |
        |package a.b.c
        |
        |$import
        |
        |class MyClass {
        |
        |  suspend fun bad() = withContext($expression) { }
        |
        |}
        |
          """.trimMargin()

        val rule = HardCodedDispatcher()

        val findings = rule.lint(code)

        findings.messages shouldBe listOf()
      }
    }

    "allowing a dispatcher should mean its usage is not reported" {

      checkAll(
        Exhaustive.boolean(),
        Exhaustive.boolean(),
        Exhaustive.boolean(),
        Exhaustive.boolean(),
        Exhaustive.boolean()
      ) { default, io, main, mainImmediate, unconfined ->

        val config = TestConfig(
          mapOf(
            "allowDefaultDispatcher" to default,
            "allowIODispatcher" to io,
            "allowMainDispatcher" to main,
            "allowMainImmediateDispatcher" to mainImmediate,
            "allowUnconfinedDispatcher" to unconfined
          )
        )

        val code = """
        |package a.b.c
        |
        |import kotlinx.coroutines.Dispatchers.*
        |
        |class MyClass {
        |
        |  suspend fun bad1() = withContext(Default) { }
        |  suspend fun bad2() = withContext(IO) { }
        |  suspend fun bad3() = withContext(Main) { }
        |  suspend fun bad4() = withContext(Main.immediate) { }
        |  suspend fun bad5() = withContext(Unconfined) { }
        |
        |}
        |
          """.trimMargin()

        val rule = HardCodedDispatcher(config)

        val findings = rule.lint(code)

        val expected = mutableListOf<String>().apply {
          if (!default) add(findingMessage("Default"))
          if (!io) add(findingMessage("IO"))
          if (!main) add(findingMessage("Main"))
          if (!mainImmediate) add(findingMessage("Main.immediate"))
          if (!unconfined) add(findingMessage("Unconfined"))
        }

        findings.messages shouldBe expected
      }
    }
  })
