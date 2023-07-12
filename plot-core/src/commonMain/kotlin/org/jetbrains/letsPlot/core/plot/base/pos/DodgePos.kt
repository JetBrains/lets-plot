/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

internal abstract class BaseDodgePos(
    aesthetics: Aesthetics,
    private val myGroupCount: Int,
    private val mySize: Double?,
    private val myIsHorizontalPos: Boolean
) : PositionAdjustment {

    private val myDodgingNeeded: Boolean

    private fun isDodgingNeeded(aesthetics: Aesthetics): Boolean {
        // if for some Value there are more than just 1 group, then dodging is needed
        val groupBy = HashMap<Double, Int?>()
        val aes = if (myIsHorizontalPos) org.jetbrains.letsPlot.core.plot.base.Aes.X else org.jetbrains.letsPlot.core.plot.base.Aes.Y
        for (i in 0 until aesthetics.dataPointCount()) {
            val p = aesthetics.dataPointAt(i)
            if (p.defined(aes)) {
                val value = p[aes]!!
                val group = p.group()
                if (groupBy.containsKey(value)) {
                    if (groupBy[value] != group) {
                        // >1 group for this Y
                        return true
                    }
                } else {
                    groupBy[value] = group
                }
            }
        }
        return false
    }

    init {
        myDodgingNeeded = isDodgingNeeded(aesthetics)
    }

    override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
        if (!myDodgingNeeded) {
            return v
        }

        val aes = if (myIsHorizontalPos) org.jetbrains.letsPlot.core.plot.base.Aes.X else org.jetbrains.letsPlot.core.plot.base.Aes.Y
        val dataResolution = ctx.getResolution(aes)
        val size = mySize ?: if (myIsHorizontalPos) p.width() else p.height()

        if (!SeriesUtil.isFinite(size)) {
            return v
        }

        val slotIndex = p.group()!!
        val median = (myGroupCount - 1) / 2.0
        val offset = (slotIndex - median) * dataResolution * size!!
        val center = p[aes]!!
        val scaler = 1.0 / myGroupCount

        return if (myIsHorizontalPos) {
            val newX = (v.x + offset - center) * scaler + center
            DoubleVector(newX, v.y)
        } else {
            val newY = (v.y + offset - center) * scaler + center
            DoubleVector(v.x, newY)
        }
    }

    override fun handlesGroups(): Boolean {
        return PositionAdjustments.Meta.DODGE.handlesGroups()
    }
}

// adjusting horizontal position
internal class DodgePos(
    aesthetics: Aesthetics,
    groupCount: Int,
    width: Double?
) : BaseDodgePos(aesthetics, groupCount, width, myIsHorizontalPos = true)

// adjusting vertical position
internal class DodgeVPos(
    aesthetics: Aesthetics,
    groupCount: Int,
    height: Double?
) : BaseDodgePos(aesthetics, groupCount, height, myIsHorizontalPos = false)