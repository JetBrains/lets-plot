package jetbrains.datalore.visualization.plot.base.data.stat

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.StatContext

/**
 * See doc for stat_smooth / geom_smooth
 *
 *
 *
 *
 * Defaults:
 *
 *
 * geom = "smooth"
 * position = "identity"
 *
 *
 * Other params:
 *
 *
 * method - smoothing method: lm, glm, gam, loess, rlm
 * (For datasets with n < 1000 default is loess. For datasets with 1000 or more observations defaults to gam)
 * formula - formula to use in smoothing function
 * ( eg. y ~ x, y ~ poly(x, 2), y ~ log(x))
 * se (TRUE ) - display confidence interval around smooth?
 * n (80) - number of points to evaluate smoother at
 *
 *
 * span (0.75) - controls the amount of smoothing for the default loess smoother.
 * fullrange (FALSE) - should the fit span the full range of the plot, or just the data
 * level (0.95) - level of confidence interval to use
 * method.args - ist of additional arguments passed on to the modelling function defined by method
 *
 *
 *
 *
 *
 *
 * Adds columns:
 *
 *
 * y    - predicted value
 * ymin - lower pointwise confidence interval around the mean
 * ymax - upper pointwise confidence interval around the mean
 * se   - standard error
 */
open class SmoothStatShell : BaseStat(DEF_MAPPING) {
    var smootherPointCount = DEF_EVAL_POINT_COUNT
    // checkArgument(smoothingMethod == Method.LM, "Only linear model is supported, use: method='lm'");
    var smoothingMethod = DEF_SMOOTHING_METHOD
    var confidenceLevel = DEF_CONFIDENCE_LEVEL
    var isDisplayConfidenceInterval = DEF_DISPLAY_CONFIDENCE_INTERVAL

    override fun requires(): List<Aes<*>> {
        return listOf<Aes<*>>(Aes.Y)
    }

    override fun hasDefaultMapping(aes: Aes<*>): Boolean {
        return super.hasDefaultMapping(aes) ||
                aes == Aes.YMIN && isDisplayConfidenceInterval ||
                aes == Aes.YMAX && isDisplayConfidenceInterval
    }

    override fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable {
        if (aes == Aes.YMIN) {
            return Stats.Y_MIN
        }
        return if (aes == Aes.YMAX) {
            Stats.Y_MAX
        } else super.getDefaultMapping(aes)
    }

    override fun apply(data: DataFrame, statCtx: StatContext): DataFrame {
        throw IllegalStateException("'smooth' statistic can't be executed on the client side")
    }

    enum class Method {
        LM, // linear model
        GLM,
        GAM,
        LOESS,
        RLM
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
                Aes.X to Stats.X,
                Aes.Y to Stats.Y
        )  // also conditional Y_MIN / Y_MAX
        private val DEF_EVAL_POINT_COUNT = 80
        private val DEF_SMOOTHING_METHOD = Method.LM
        private val DEF_CONFIDENCE_LEVEL = 0.95    // 95 %
        private val DEF_DISPLAY_CONFIDENCE_INTERVAL = true
    }
}
