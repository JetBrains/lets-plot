package jetbrains.datalore.visualization.plot.base.render.pos

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.render.Aesthetics
import jetbrains.datalore.visualization.plot.base.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.GeomContext
import jetbrains.datalore.visualization.plot.base.render.PositionAdjustment

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

    fun stack(aes: Aesthetics, strategy: StackingStrategy): PositionAdjustment {
        when (strategy) {
            PositionAdjustments.StackingStrategy.SPLIT_POSITIVE_NEGATIVE -> return StackPos.splitPositiveNegative(aes)
            PositionAdjustments.StackingStrategy.SUM_POSITIVE_NEGATIVE -> return StackPos.sumPositiveNegative(aes)
            else -> throw IllegalArgumentException("strategy: $strategy")
        }
    }

    fun fill(aesthetics: Aesthetics): PositionAdjustment {
        return FillPos(aesthetics)
    }

    fun jitter(width: Double?, height: Double?): PositionAdjustment {
        return JitterPos(width, height)
    }

    fun nudge(width: Double?, height: Double?): PositionAdjustment {
        return NudgePos(width, height)
    }

    fun jitterDodge(aesthetics: Aesthetics, groupCount: Int, width: Double?, jitterWidth: Double?, jitterHeight: Double?): PositionAdjustment {
        return JitterDodgePos(aesthetics, groupCount, width, jitterWidth, jitterHeight)
    }

    enum class Meta(private val myHandlesGroups: Boolean) {
        IDENTITY(false),
        DODGE(true),
        STACK(true),
        FILL(true),
        JITTER(false),
        NUDGE(false),
        JITTER_DODGE(true);

        fun handlesGroups(): Boolean {
            return myHandlesGroups
        }
    }

    enum class StackingStrategy {
        SUM_POSITIVE_NEGATIVE,
        SPLIT_POSITIVE_NEGATIVE
    }

}
