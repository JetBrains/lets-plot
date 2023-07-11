/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext

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