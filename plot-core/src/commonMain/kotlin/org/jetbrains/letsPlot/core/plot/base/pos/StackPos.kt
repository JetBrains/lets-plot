/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext

internal class StackPos(aes: Aesthetics, vjust: Double?, stackingMode: StackingMode) : StackablePos() {

    private val myOffsetByIndex: Map<Int, StackOffset> = mapIndexToOffset(aes, vjust ?: DEF_VJUST, stackingMode)

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        return v.add(DoubleVector(0.0, myOffsetByIndex.getValue(p.index()).value))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.STACK.handlesGroups()
    }

    companion object {
        private const val DEF_VJUST = 1.0
    }
}