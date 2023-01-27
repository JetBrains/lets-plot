/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment

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

    fun stack(aes: Aesthetics, vjust: Double?, offsetState: StackPos.OffsetState? = null): PositionAdjustment {
        return StackPos(aes, vjust, offsetState)
    }

    fun fill(aesthetics: Aesthetics, vjust: Double?): PositionAdjustment {
        return FillPos(aesthetics, vjust)
    }

    fun jitter(width: Double?, height: Double?): PositionAdjustment {
        return JitterPos(width, height)
    }

    fun nudge(width: Double?, height: Double?): PositionAdjustment {
        return NudgePos(width, height)
    }

    fun jitterDodge(
        aesthetics: Aesthetics,
        groupCount: Int,
        width: Double?,
        jitterWidth: Double?,
        jitterHeight: Double?
    ): PositionAdjustment {
        return JitterDodgePos(aesthetics, groupCount, width, jitterWidth, jitterHeight)
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
