/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes

interface MappedDataAccess {
    val isYOrientation: Boolean

    fun isMapped(aes: Aes<*>): Boolean

    fun getOriginalValue(aes: Aes<*>, index: Int): Any?

//    fun getMappedDataValue(aes: Aes<*>, index: Int, ctx: PlotContext): String

    fun getMappedDataLabel(aes: Aes<*>): String
}