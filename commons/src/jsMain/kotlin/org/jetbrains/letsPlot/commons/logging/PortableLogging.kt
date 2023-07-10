/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.logging

import mu.KotlinLogging
import org.jetbrains.letsPlot.commons.logging.Logger
import kotlin.reflect.KClass

actual object PortableLogging {
    actual fun logger(cl: KClass<*>): Logger {
        return logger(cl.simpleName ?: "<anonymous>")
    }

    actual fun logger(name: String): Logger {
        val kl = KotlinLogging.logger(name)
        return object : Logger {
            override fun error(e: Throwable, message: () -> String) {
                kl.error(e, message)
            }

            override fun info(message: () -> String) {
                kl.info(message)
            }
        }
    }
}