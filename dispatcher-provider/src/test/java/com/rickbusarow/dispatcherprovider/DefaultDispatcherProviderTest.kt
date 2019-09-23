/*
 * Copyright (C) 2019 Rick Busarow
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

package com.rickbusarow.dispatcherprovider

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DispatcherProviderTest {

  val default: CoroutineDispatcher = mockk()
  val io: CoroutineDispatcher = mockk()
  val main: MainCoroutineDispatcher = mockk()
  val mainImmediate: MainCoroutineDispatcher = mockk()
  val unconfined: CoroutineDispatcher = mockk()

  @BeforeAll
  fun beforeAll() {
    mockkStatic(Dispatchers::class)

    every { Dispatchers.Default } returns default
    every { Dispatchers.IO } returns io
    every { Dispatchers.Main } returns main
    every { Dispatchers.Unconfined } returns unconfined

    every { main.immediate } returns mainImmediate
  }

  @AfterAll
  fun afterAll() {
    unmockkStatic(Dispatchers::class)
  }

  @Nested
  inner class `DispatcherProvider factory` {

    @Test
    fun `created provider default dispatcher should use Dispatchers Default`() {

      DispatcherProvider().default shouldBe default
    }

    @Test
    fun `created provider io dispatcher should use Dispatchers IO`() {

      DispatcherProvider().io shouldBe io
    }

    @Test
    fun `created provider main dispatcher should use Dispatchers Main`() {

      DispatcherProvider().main shouldBe main
    }

    @Test
    fun `created provider mainImmediate dispatcher should use Dispatchers Main immediate`() {

      DispatcherProvider().mainImmediate shouldBe mainImmediate
    }

    @Test
    fun `created provider unconfined dispatcher should use Dispatchers Unconfined`() {

      DispatcherProvider().unconfined shouldBe unconfined
    }

    @Test
    fun `DispatcherProvider factory should create DefaultDispatcherProvider`() {

      DispatcherProvider().shouldBeInstanceOf<DefaultDispatcherProvider>()
    }
  }

  @Nested
  inner class `Default DispatcherProvider` {

    @Test
    fun `DefaultDispatcherProvider default dispatcher should use Dispatchers Default`() {

      DefaultDispatcherProvider().default shouldBe default
    }

    @Test
    fun `DefaultDispatcherProvider io dispatcher should use Dispatchers IO`() {

      DefaultDispatcherProvider().io shouldBe io
    }

    @Test
    fun `DefaultDispatcherProvider main dispatcher should use Dispatchers Main`() {

      DefaultDispatcherProvider().main shouldBe main
    }

    @Test
    fun `DefaultDispatcherProvider mainImmediate dispatcher should use Dispatchers Main immediate`() {

      DefaultDispatcherProvider().mainImmediate shouldBe mainImmediate
    }

    @Test
    fun `DefaultDispatcherProvider unconfined dispatcher should use Dispatchers Unconfined`() {

      DefaultDispatcherProvider().unconfined shouldBe unconfined
    }
  }

}