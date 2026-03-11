package org.jetbrains.letsPlot.commons.logging

import kotlin.reflect.KClass

actual object PortableLogging {
    actual fun logger(cl: KClass<*>): Logger {
        TODO("Not yet implemented")
    }

    actual fun logger(name: String): Logger {
        TODO("Not yet implemented")
    }
}