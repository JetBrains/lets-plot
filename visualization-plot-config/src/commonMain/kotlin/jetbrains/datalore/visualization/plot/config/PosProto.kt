package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.visualization.plot.base.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.builder.assemble.PosProvider

internal object PosProto {
    // position adjustments
    private const val IDENTITY = "identity"
    private const val STACK = "stack"
    private const val DODGE = "dodge"
    private const val FILL = "fill"
    private const val NUDGE = "nudge"
    private const val JITTER = "jitter"
    private const val JITTER_DODGE = "jitterdodge"

    // option names
    private const val DODGE_WIDTH = "width"
    private const val JITTER_WIDTH = "width"
    private const val JITTER_HEIGHT = "height"
    private const val NUDGE_WIDTH = "x"
    private const val NUDGE_HEIGHT = "y"
    private const val JD_DODGE_WIDTH = "dodge_width"
    private const val JD_JITTER_WIDTH = "jitter_width"
    private const val JD_JITTER_HEIGHT = "jitter_height"

    fun createPosProvider(posName: String, options: Map<*, *>): PosProvider {
        val opts = OptionsAccessor.over(options)
        return when (posName) {
            IDENTITY -> PosProvider.wrap(PositionAdjustments.identity())
            STACK -> PosProvider.barStack()
            DODGE -> PosProvider.dodge(opts.getDouble(DODGE_WIDTH))
            FILL -> PosProvider.fill()
            JITTER -> PosProvider.jitter(opts.getDouble(JITTER_WIDTH), opts.getDouble(JITTER_HEIGHT))
            NUDGE -> PosProvider.nudge(opts.getDouble(NUDGE_WIDTH), opts.getDouble(NUDGE_HEIGHT))
            JITTER_DODGE -> PosProvider.jitterDodge(opts.getDouble(JD_DODGE_WIDTH), opts.getDouble(JD_JITTER_WIDTH), opts.getDouble(JD_JITTER_HEIGHT))
            else -> throw IllegalArgumentException("Unknown position adjustments name: '$posName'")
        }
    }
}
