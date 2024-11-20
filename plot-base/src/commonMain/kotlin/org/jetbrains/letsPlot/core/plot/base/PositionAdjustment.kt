/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

interface PositionAdjustment {
    val isIdentity: Boolean
        get() = false

    fun handlesGroups(): Boolean

    fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector
}

class CompositePos(
    private val pos1: PositionAdjustment,
    private val pos2: PositionAdjustment
) : PositionAdjustment {
    override fun handlesGroups(): Boolean {
        return pos1.handlesGroups() || pos2.handlesGroups()
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        return pos2.translate(pos1.translate(v, p, ctx), p, ctx)
    }
}