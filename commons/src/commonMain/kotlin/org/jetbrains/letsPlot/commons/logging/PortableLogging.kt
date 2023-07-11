/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.logging

import kotlin.reflect.KClass

expect object PortableLogging {
    fun logger(cl: KClass<*>): Logger
    fun logger(name: String): Logger
}