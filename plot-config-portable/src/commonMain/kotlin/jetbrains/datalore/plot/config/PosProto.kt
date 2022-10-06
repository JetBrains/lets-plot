/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.builder.assemble.PosProvider

@Suppress("MemberVisibilityCanBePrivate")
internal object PosProto {
    // position adjustments
    const val IDENTITY = "identity"
    const val STACK = "stack"
    const val DODGE = "dodge"
    const val FILL = "fill"
    const val NUDGE = "nudge"
    const val JITTER = "jitter"
    const val JITTER_DODGE = "jitterdodge"

    // ToDo: move option names to the plot.config.Option to make them available in LPK and to remove
    //  duplicates in org.jetbrains.letsPlot.pos.
    // option names
    const val DODGE_WIDTH = "width"
    const val JITTER_WIDTH = "width"
    const val JITTER_HEIGHT = "height"
    const val NUDGE_WIDTH = "x"
    const val NUDGE_HEIGHT = "y"
    const val JD_DODGE_WIDTH = "dodge_width"
    const val JD_JITTER_WIDTH = "jitter_width"
    const val JD_JITTER_HEIGHT = "jitter_height"
    const val STACK_VJUST = "vjust"
    const val FILL_VJUST = "vjust"

    fun createPosProvider(posOptions: Map<String, Any>): PosProvider {
        val posName = ConfigUtil.featureName(posOptions)
        val opts = OptionsAccessor(posOptions)
        return when (posName) {
            IDENTITY -> PosProvider.wrap(PositionAdjustments.identity())
            STACK -> PosProvider.barStack(opts.getDouble(STACK_VJUST))
            DODGE -> PosProvider.dodge(opts.getDouble(DODGE_WIDTH))
            FILL -> PosProvider.fill(opts.getDouble(FILL_VJUST))
            JITTER -> PosProvider.jitter(
                opts.getDouble(JITTER_WIDTH),
                opts.getDouble(JITTER_HEIGHT)
            )
            NUDGE -> PosProvider.nudge(
                opts.getDouble(NUDGE_WIDTH),
                opts.getDouble(NUDGE_HEIGHT)
            )
            JITTER_DODGE -> PosProvider.jitterDodge(
                opts.getDouble(JD_DODGE_WIDTH),
                opts.getDouble(JD_JITTER_WIDTH),
                opts.getDouble(JD_JITTER_HEIGHT)
            )
            else -> throw IllegalArgumentException("Unknown position adjustments name: '$posName'")
        }
    }
}
