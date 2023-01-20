/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.logging

import kotlin.reflect.KClass

actual object PortableLogging {
    actual fun logger(cl: KClass<*>): Logger {
        return PrintlnLogger(cl.simpleName ?: "<anonymous>")
    }
}