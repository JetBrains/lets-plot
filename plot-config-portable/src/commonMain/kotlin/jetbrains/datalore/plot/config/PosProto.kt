/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.pos.StackingType
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.config.Option.Pos

@Suppress("MemberVisibilityCanBePrivate")
internal object PosProto {
    // position adjustments
    const val IDENTITY = "identity"
    const val STACK = "stack"
    const val STACK_LN = "stack_ln"
    const val DODGE = "dodge"
    const val FILL = "fill"
    const val NUDGE = "nudge"
    const val JITTER = "jitter"
    const val JITTER_DODGE = "jitterdodge"

    fun createPosProvider(posOptions: Map<String, Any>): PosProvider {
        val posName = ConfigUtil.featureName(posOptions)
        val opts = OptionsAccessor(posOptions)
        return when (posName) {
            IDENTITY -> PosProvider.wrap(PositionAdjustments.identity())
            STACK -> PosProvider.barStack(opts.getDouble(Pos.Stack.VJUST), StackingType.SUM)
            STACK_LN -> PosProvider.barStack(opts.getDouble(Pos.Stack.VJUST), StackingType.LN)
            DODGE -> PosProvider.dodge(opts.getDouble(Pos.Dodge.WIDTH))
            FILL -> PosProvider.fill(opts.getDouble(Pos.Fill.VJUST))
            JITTER -> PosProvider.jitter(
                opts.getDouble(Pos.Jitter.WIDTH),
                opts.getDouble(Pos.Jitter.HEIGHT)
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
}
