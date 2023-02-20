/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext

internal class FillPos(aes: Aesthetics, vjust: Double?) : StackablePos() {

    private val myOffsetByIndex: Map<Int, StackOffset> = mapIndexToOffset(aes, vjust ?: 1.0)

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        val scale = 1.0 / myOffsetByIndex.getValue(p.index()).max
        return DoubleVector(v.x, scale * (v.y + myOffsetByIndex.getValue(p.index()).value))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.FILL.handlesGroups()
    }
}
