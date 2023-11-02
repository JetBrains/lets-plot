/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.pos.StackablePos
import org.jetbrains.letsPlot.core.plot.base.pos.StackingMode
import org.jetbrains.letsPlot.core.plot.builder.assemble.PosProvider
import org.jetbrains.letsPlot.core.spec.Option.Pos
import org.jetbrains.letsPlot.core.spec.config.ConfigUtil
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor

@Suppress("MemberVisibilityCanBePrivate")
internal object PosProto {
    // position adjustments
    const val IDENTITY = "identity"
    const val STACK = "stack"
    const val DODGE = "dodge"
    const val DODGE_V = "dodgev"
    const val FILL = "fill"
    const val NUDGE = "nudge"
    const val JITTER = "jitter"
    const val JITTER_DODGE = "jitterdodge"

    fun createPosProvider(posOptions: Map<String, Any>): PosProvider {
        val posName = ConfigUtil.featureName(posOptions)
        val opts = OptionsAccessor(posOptions)
        return when (posName) {
            IDENTITY -> PosProvider.wrap(PositionAdjustments.identity())
            STACK -> configureStackPosition(opts)
            DODGE -> PosProvider.dodge(opts.getDouble(Pos.Dodge.WIDTH))
            DODGE_V -> PosProvider.dodgev(opts.getDouble(Pos.DodgeV.HEIGHT))
            FILL -> configureFillPosition(opts)
            JITTER -> PosProvider.jitter(
                opts.getDouble(Pos.Jitter.WIDTH),
                opts.getDouble(Pos.Jitter.HEIGHT),
                opts.getLong(Pos.Jitter.SEED)
            )

            NUDGE -> PosProvider.nudge(
                opts.getDouble(Pos.Nudge.WIDTH),
                opts.getDouble(Pos.Nudge.HEIGHT)
            )

            JITTER_DODGE -> PosProvider.jitterDodge(
                opts.getDouble(Pos.JitterDodge.DODGE_WIDTH),
                opts.getDouble(Pos.JitterDodge.JITTER_WIDTH),
                opts.getDouble(Pos.JitterDodge.JITTER_HEIGHT)
            )

            else -> throw IllegalArgumentException("Unknown position adjustments name: '$posName'")
        }
    }

    private fun configureStackPosition(options: OptionsAccessor): PosProvider {
        val mode = options.getString(Pos.Stack.MODE)?.let {
            StackingMode.safeValueOf(it)
        } ?: StackablePos.DEF_STACKING_MODE

        return PosProvider.barStack(options.getDouble(Pos.Stack.VJUST), mode)
    }

    private fun configureFillPosition(options: OptionsAccessor): PosProvider {
        val mode = options.getString(Pos.Fill.MODE)?.let {
            StackingMode.safeValueOf(it)
        } ?: StackablePos.DEF_STACKING_MODE

        return PosProvider.fill(options.getDouble(Pos.Fill.VJUST), mode)
    }
}
