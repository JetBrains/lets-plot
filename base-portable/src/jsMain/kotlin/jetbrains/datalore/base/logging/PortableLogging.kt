/*
 * Copyright (c) 2020. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.logging

import mu.KotlinLogging
import kotlin.reflect.KClass

actual object PortableLogging {
    actual fun logger(cl: KClass<*>): Logger {
        val kl = KotlinLogging.logger(cl.simpleName ?: "<anonymous>")
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

