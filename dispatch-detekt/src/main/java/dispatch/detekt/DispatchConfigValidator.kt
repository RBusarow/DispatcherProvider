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

package dispatch.detekt

import io.gitlab.arturbosch.detekt.api.*

/**
 * @suppress
 */
public class DispatchConfigValidator : ConfigValidator {

  override fun validate(config: Config): Collection<Notification> {
    val result = mutableListOf<Notification>()
    runCatching {
      config.subConfig("dispatch")
        .subConfig("AndroidXLifecycleScope")
        .valueOrNull<Boolean>("active")
    }.onFailure {
      result.add(ConfigError("'active' property must be of type boolean."))
    }
    runCatching {
      config.subConfig("dispatch")
        .subConfig("HardCodedDispatcher")
        .valueOrNull<Boolean>("active")
    }.onFailure {
      result.add(ConfigError("'active' property must be of type boolean."))
    }
    runCatching {
      config.subConfig("dispatch")
        .subConfig("HardCodedDispatcher")
        .valueOrNull<Boolean>("allowDefaultDispatcher")
    }.onFailure { result.add(ConfigError("'allowDefaultDispatcher' property must be of type boolean.")) }
    runCatching {
      config.subConfig("dispatch")
        .subConfig("HardCodedDispatcher")
        .valueOrNull<Boolean>("allowIODispatcher")
    }.onFailure { result.add(ConfigError("'allowIODispatcher' property must be of type boolean.")) }
    runCatching {
      config.subConfig("dispatch")
        .subConfig("HardCodedDispatcher")
        .valueOrNull<Boolean>("allowMainDispatcher")
    }.onFailure { result.add(ConfigError("'allowMainDispatcher' property must be of type boolean.")) }
    runCatching {
      config.subConfig("dispatch")
        .subConfig("HardCodedDispatcher")
        .valueOrNull<Boolean>("allowMainImmediateDispatcher")
    }.onFailure { result.add(ConfigError("'allowMainImmediateDispatcher' property must be of type boolean.")) }
    runCatching {
      config.subConfig("dispatch")
        .subConfig("HardCodedDispatcher")
        .valueOrNull<Boolean>("allowUnconfinedDispatcher")
    }.onFailure { result.add(ConfigError("'allowUnconfinedDispatcher' property must be of type boolean.")) }

    return result
  }
}

/**
 * @suppress
 */
public class ConfigError(
  override val message: String,
  override val level: Notification.Level = Notification.Level.Error
) : Notification
