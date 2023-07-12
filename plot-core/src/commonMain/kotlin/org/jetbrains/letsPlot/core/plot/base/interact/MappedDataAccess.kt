/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.interact

import org.jetbrains.letsPlot.core.plot.base.Aes

interface MappedDataAccess {
    val isYOrientation: Boolean

    fun isMapped(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean

    fun getOriginalValue(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>, index: Int): Any?

//    fun getMappedDataValue(aes: Aes<*>, index: Int, ctx: PlotContext): String

    fun getMappedDataLabel(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): String
}