/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment

internal class CompositionPos(
    val firstPos: PositionAdjustment,
    val secondPos: PositionAdjustment
) : PositionAdjustment {
    override fun handlesGroups(): Boolean {
        return firstPos.handlesGroups() || secondPos.handlesGroups()
    }

    override fun translate(
        v: DoubleVector,
        p: DataPointAesthetics,
        ctx: GeomContext
    ): DoubleVector {
        return secondPos.translate(firstPos.translate(v, p, ctx), p, ctx)
    }
}