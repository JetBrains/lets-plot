package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.gog.core.render.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.gog.plot.assemble.PosProvider

internal object PosProto {
    // position adjustments
    private val IDENTITY = "identity"
    private val STACK = "stack"
    private val DODGE = "dodge"
    private val FILL = "fill"
    private val NUDGE = "nudge"
    private val JITTER = "jitter"
    private val JITTER_DODGE = "jitterdodge"

    // option names
    private val DODGE_WIDTH = "width"
    private val JITTER_WIDTH = "width"
    private val JITTER_HEIGHT = "height"
    private val NUDGE_WIDTH = "x"
    private val NUDGE_HEIGHT = "y"
    private val JD_DODGE_WIDTH = "dodge_width"
    private val JD_JITTER_WIDTH = "jitter_width"
    private val JD_JITTER_HEIGHT = "jitter_height"

    fun createPosProvider(posName: String, options: Map<*, *>): PosProvider {
        val opts = OptionsAccessor.over(options)
        when (posName) {
            IDENTITY -> return PosProvider.wrap(PositionAdjustments.identity())
            STACK -> return PosProvider.barStack()
            DODGE -> return PosProvider.dodge(opts.getDouble(DODGE_WIDTH))
            FILL -> return PosProvider.fill()
            JITTER -> return PosProvider.jitter(opts.getDouble(JITTER_WIDTH), opts.getDouble(JITTER_HEIGHT))
            NUDGE -> return PosProvider.nudge(opts.getDouble(NUDGE_WIDTH), opts.getDouble(NUDGE_HEIGHT))
            JITTER_DODGE -> return PosProvider.jitterDodge(opts.getDouble(JD_DODGE_WIDTH), opts.getDouble(JD_JITTER_WIDTH), opts.getDouble(JD_JITTER_HEIGHT))
            else -> throw IllegalArgumentException("Unknown position adjustments name: '$posName'")
        }
    }
}
