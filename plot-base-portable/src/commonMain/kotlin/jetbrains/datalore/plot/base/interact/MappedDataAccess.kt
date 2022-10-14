/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext

interface MappedDataAccess {

    fun isMapped(aes: Aes<*>): Boolean

    fun getOriginalValue(aes: Aes<*>, index: Int): Any?

    fun getMappedDataValue(aes: Aes<*>, index: Int, ctx: PlotContext): String

    fun getMappedDataLabel(aes: Aes<*>): String
}