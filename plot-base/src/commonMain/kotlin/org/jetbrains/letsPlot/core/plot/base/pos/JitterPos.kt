/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import kotlin.random.Random

internal class JitterPos(width: Double?, height: Double?, seed: Long? = null) : PositionAdjustment {

    //uniform distribution
    private val myWidth: Double
    private val myHeight: Double
    private val random: Random = seed?.let { Random(seed) } ?: Random.Default

    init {
        myWidth = width ?: DEF_JITTER_WIDTH
        myHeight = height ?: DEF_JITTER_HEIGHT
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        val x = (2 * random.nextDouble() - 1) * myWidth * ctx.getResolution(Aes.X)
        val y = (2 * random.nextDouble() - 1) * myHeight * ctx.getResolution(Aes.Y)
        return v.add(DoubleVector(x, y))
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.JITTER.handlesGroups()
    }

    companion object {
        val DEF_JITTER_WIDTH = 0.4
        val DEF_JITTER_HEIGHT = 0.4
    }
}
