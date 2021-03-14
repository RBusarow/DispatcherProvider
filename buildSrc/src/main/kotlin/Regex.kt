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

fun String.matches(vararg regex: Regex) = regex.any { it.matches(this) }

fun String.replace(regex: Regex, block: (String) -> String): String =
  regex.replace(this) { match ->
    block(match.destructured.component1())
  }

fun String.replace(regex: Regex, block: (String, String) -> String): String =
  regex.replace(this) { match ->
    block(match.destructured.component1(), match.destructured.component2())
  }

fun String.replace(regex: Regex, block: (String, String, String) -> String): String =
  regex.replace(this) { match ->
    block(
      match.destructured.component1(),
      match.destructured.component2(),
      match.destructured.component3()
    )
  }

fun String.replace(regex: Regex, block: (String, String, String, String) -> String): String =
  regex.replace(this) { match ->
    block(
      match.destructured.component1(),
      match.destructured.component2(),
      match.destructured.component3(),
      match.destructured.component4()
    )
  }
