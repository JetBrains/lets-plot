/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.pos

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment

object PositionAdjustments {

    fun identity(): PositionAdjustment {
        return object : PositionAdjustment {

            override val isIdentity: Boolean
                get() = true

            override fun translate(v: DoubleVector, p: DataPointAesthetics, ctx: GeomContext): DoubleVector {
                return v
            }

            override fun handlesGroups(): Boolean {
                return Meta.IDENTITY.handlesGroups()
            }
        }
    }

    /**
     * @param aesthetics
     * @param groupCount
     * @param width      NULL - default
     * @return
     */
    fun dodge(aesthetics: Aesthetics, groupCount: Int, width: Double?): PositionAdjustment {
        return DodgePos(aesthetics, groupCount, width)
    }

    fun dodgev(aesthetics: Aesthetics, groupCount: Int, height: Double?): PositionAdjustment {
        return DodgeVPos(aesthetics, groupCount, height)
    }

    fun stack(aes: Aesthetics, vjust: Double?, stackingMode: StackingMode): PositionAdjustment {
        return StackPos(aes, vjust, stackingMode)
    }

    fun fill(aesthetics: Aesthetics, vjust: Double?, stackingMode: StackingMode): PositionAdjustment {
        return FillPos(aesthetics, vjust, stackingMode)
    }

    fun jitter(width: Double?, height: Double?, seed: Long?): PositionAdjustment {
        return JitterPos(width, height, seed)
    }

    fun nudge(width: Double?, height: Double?): PositionAdjustment {
        return NudgePos(width, height)
    }

    fun jitterDodge(
        aesthetics: Aesthetics,
        groupCount: Int,
        width: Double?,
        jitterWidth: Double?,
        jitterHeight: Double?,
        seed: Long?
    ): PositionAdjustment {
        return JitterDodgePos(aesthetics, groupCount, width, jitterWidth, jitterHeight, seed)
    }

    fun composition(firstPos: PositionAdjustment, secondPos: PositionAdjustment): PositionAdjustment {
        return CompositionPos(firstPos, secondPos)
    }

    enum class Meta(private val handlesGroups: Boolean) {
        IDENTITY(false),
        DODGE(true),
        STACK(true),
        FILL(true),
        JITTER(false),
        NUDGE(false),
        JITTER_DODGE(true);

        fun handlesGroups(): Boolean {
            return handlesGroups
        }
    }
}
