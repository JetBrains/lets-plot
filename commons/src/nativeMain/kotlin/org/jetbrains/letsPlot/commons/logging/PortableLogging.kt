/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.logging

import org.jetbrains.letsPlot.commons.logging.Logger
import org.jetbrains.letsPlot.commons.logging.PrintlnLogger
import kotlin.reflect.KClass

actual object PortableLogging {
    actual fun logger(cl: KClass<*>): Logger {
        return PrintlnLogger(cl.simpleName ?: "<anonymous>")
    }

    actual fun logger(name: String): Logger {
        return PrintlnLogger(name)
    }
}