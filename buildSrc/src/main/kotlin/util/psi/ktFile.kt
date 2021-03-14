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

@file:Suppress("TooManyFunctions")

package util.psi

import org.jetbrains.kotlin.com.intellij.openapi.util.*
import org.jetbrains.kotlin.idea.*
import org.jetbrains.kotlin.psi.*
import java.io.*

val RELATIVE_PATH: Key<String> = Key("relativePath")
val ABSOLUTE_PATH: Key<String> = Key("absolutePath")

fun File.asKtFile(): KtFile =
  (psiFileFactory.createFileFromText(name, KotlinLanguage.INSTANCE, readText()) as? KtFile)?.apply {
    putUserData(ABSOLUTE_PATH, this@asKtFile.absolutePath)
  } ?: throw FileNotFoundException("could not find file $this")

fun KtFile.asFile(): File = File(absolutePath())

fun KtFile.absolutePath(): String =
  getUserData(ABSOLUTE_PATH) ?: error("KtFile '$name' expected to have an absolute path.")

fun KtFile.relativePath(): String = getUserData(RELATIVE_PATH)
  ?: error("KtFile '$name' expected to have an relative path.")

fun KtFile.replaceClass(oldClass: KtClass, newClass: KtClass): KtFile {

  val newText = text.replace(oldClass.text, newClass.text)

  val path = absolutePath()

  return (psiFileFactory.createFileFromText(name, KotlinLanguage.INSTANCE, newText) as KtFile).apply {
    putUserData(ABSOLUTE_PATH, path)
  }
}

fun KtFile.write(path: String = absolutePath()) {

  val javaFile = File(path)

  javaFile.mkdirs()
  javaFile.writeText(text)
}
