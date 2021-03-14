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

package dispatch.core

import dispatch.internal.test.*
import io.kotest.matchers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import kotlin.coroutines.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
internal class CoroutineScopesTest {

  val job = Job()
  val dispatcher = newSingleThreadContext("single thread dispatcher")
  val dispatcherProvider = DispatcherProvider()
  val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
  val coroutineName = CoroutineName("name")

  val originContext = job + dispatcher + dispatcherProvider + exceptionHandler + coroutineName

  val mainDispatcher = newSingleThreadContext("main dispatcher")

  @BeforeAll
  fun beforeAll() {
    Dispatchers.setMain(mainDispatcher)
  }

  @AfterAll
  fun afterAll() {
    Dispatchers.resetMain()
  }

  @Nested
  inner class `Default CoroutineScope` {

    @Nested
    inner class `itemized factory` {
      @Test
      fun `no args should use SupervisorJob`() {

        val scope = DefaultCoroutineScope()

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job arg should be used in coroutineContext`() {

        val scope = DefaultCoroutineScope(job = job)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `no args should use DefaultDispatcherProvider`() {

        val scope = DefaultCoroutineScope()

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider arg should be used in coroutineContext`() {

        val scope = DefaultCoroutineScope(dispatcherProvider = dispatcherProvider)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_Default`() {

        val scope = DefaultCoroutineScope()

        scope.coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.Default
      }
    }

    @Nested
    inner class `CoroutineContext factory` {

      @Test
      fun `EmptyCoroutineContext should use SupervisorJob`() {

        val scope = DefaultCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job contained in coroutineContext should be retained`() {

        val scope = DefaultCoroutineScope(job as CoroutineContext)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `EmptyCoroutineContext should use DefaultDispatcherProvider`() {

        val scope = DefaultCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider contained in coroutineContext should be retained`() {

        val scope = DefaultCoroutineScope(dispatcherProvider as CoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_Default`() {

        val scope = DefaultCoroutineScope(originContext)

        scope.coroutineContext shouldEqualFolded originContext + Dispatchers.Default
      }
    }
  }

  @Nested
  inner class `IO CoroutineScope` {

    @Nested
    inner class `itemized factory` {

      @Test
      fun `no args should use SupervisorJob`() {

        val scope = IOCoroutineScope()

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job arg should be used in coroutineContext`() {

        val scope = IOCoroutineScope(job = job)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `no args should use IODispatcherProvider`() {

        val scope = IOCoroutineScope()

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider arg should be used in coroutineContext`() {

        val scope = IOCoroutineScope(dispatcherProvider = dispatcherProvider)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_IO`() {

        val scope = IOCoroutineScope()

        scope.coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
      }
    }

    @Nested
    inner class `CoroutineContext factory` {

      @Test
      fun `EmptyCoroutineContext should use SupervisorJob`() {

        val scope = IOCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job contained in coroutineContext should be retained`() {

        val scope = IOCoroutineScope(job as CoroutineContext)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `EmptyCoroutineContext should use DefaultDispatcherProvider`() {

        val scope = IOCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider contained in coroutineContext should be retained`() {

        val scope = IOCoroutineScope(dispatcherProvider as CoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_IO`() {

        val scope = IOCoroutineScope(originContext)

        scope.coroutineContext shouldEqualFolded originContext + Dispatchers.IO
      }
    }
  }

  @Nested
  inner class `Main CoroutineScope` {

    @Nested
    inner class `itemized factory` {
      @Test
      fun `no args should use SupervisorJob`() {

        val scope = MainCoroutineScope()

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job arg should be used in coroutineContext`() {

        val scope = MainCoroutineScope(job = job)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `no args should use MainDispatcherProvider`() {

        val scope = MainCoroutineScope()

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider arg should be used in coroutineContext`() {

        val scope = MainCoroutineScope(dispatcherProvider = dispatcherProvider)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_Main`() {

        val scope = MainCoroutineScope()

        scope.coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.Main
      }
    }

    @Nested
    inner class `CoroutineContext factory` {

      @Test
      fun `EmptyCoroutineContext should use SupervisorJob`() {

        val scope = MainCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job contained in coroutineContext should be retained`() {

        val scope = MainCoroutineScope(job as CoroutineContext)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `EmptyCoroutineContext should use DefaultDispatcherProvider`() {

        val scope = MainCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider contained in coroutineContext should be retained`() {

        val scope = MainCoroutineScope(dispatcherProvider as CoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_Main`() {

        val scope = MainCoroutineScope(originContext)

        scope.coroutineContext shouldEqualFolded originContext + Dispatchers.Main
      }
    }
  }

  @Nested
  inner class `Main immediate CoroutineScope` {

    @Nested
    inner class `itemized factory` {

      @Test
      fun `no args should use SupervisorJob`() {

        val scope = MainImmediateCoroutineScope()

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job arg should be used in coroutineContext`() {

        val scope = MainImmediateCoroutineScope(job = job)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `no args should use MainDispatcherProvider`() {

        val scope = MainImmediateCoroutineScope()

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider arg should be used in coroutineContext`() {

        val scope = MainImmediateCoroutineScope(dispatcherProvider = dispatcherProvider)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_Main`() {

        val scope = MainImmediateCoroutineScope()

        // This is weak.  I'm not sure how to differentiate between Main and Main.immediate
        scope.coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.Main.immediate
      }
    }

    @Nested
    inner class `CoroutineContext factory` {

      @Test
      fun `EmptyCoroutineContext should use SupervisorJob`() {

        val scope = MainImmediateCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job contained in coroutineContext should be retained`() {

        val scope = MainImmediateCoroutineScope(job as CoroutineContext)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `EmptyCoroutineContext should use DefaultDispatcherProvider`() {

        val scope = MainImmediateCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider contained in coroutineContext should be retained`() {

        val scope = MainImmediateCoroutineScope(dispatcherProvider as CoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_Main_immediate`() {

        val scope = MainImmediateCoroutineScope(originContext)

        scope.coroutineContext shouldEqualFolded originContext + Dispatchers.Main.immediate
      }
    }
  }

  @Nested
  inner class `Unconfined CoroutineScope` {

    @Nested
    inner class `itemized factory` {
      @Test
      fun `no args should use SupervisorJob`() {

        val scope = UnconfinedCoroutineScope()

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job arg should be used in coroutineContext`() {

        val scope = UnconfinedCoroutineScope(job = job)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `no args should use UnconfinedDispatcherProvider`() {

        val scope = UnconfinedCoroutineScope()

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider arg should be used in coroutineContext`() {

        val scope = UnconfinedCoroutineScope(dispatcherProvider = dispatcherProvider)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_Unconfined`() {

        val scope = UnconfinedCoroutineScope()

        scope.coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.Unconfined
      }
    }

    @Nested
    inner class `CoroutineContext factory` {

      @Test
      fun `EmptyCoroutineContext should use SupervisorJob`() {

        val scope = UnconfinedCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[Job]!!.shouldBeSupervisorJob()
      }

      @Test
      fun `job contained in coroutineContext should be retained`() {

        val scope = UnconfinedCoroutineScope(job as CoroutineContext)

        scope.coroutineContext[Job] shouldBe job
      }

      @Test
      fun `EmptyCoroutineContext should use DefaultDispatcherProvider`() {

        val scope = UnconfinedCoroutineScope(EmptyCoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe DefaultDispatcherProvider.get()
      }

      @Test
      fun `dispatcherProvider contained in coroutineContext should be retained`() {

        val scope = UnconfinedCoroutineScope(dispatcherProvider as CoroutineContext)

        scope.coroutineContext[DispatcherProvider] shouldBe dispatcherProvider
      }

      @Test
      fun `coroutineContext should use Dispatchers_Unconfined`() {

        val scope = UnconfinedCoroutineScope(originContext)

        scope.coroutineContext shouldEqualFolded originContext + Dispatchers.Unconfined
      }
    }
  }
}
