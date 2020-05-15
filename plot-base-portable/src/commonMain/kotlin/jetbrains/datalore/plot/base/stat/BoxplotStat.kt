/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.WIDTH
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.math.sqrt

/**
 * Calculate components of box and whisker plot.
 *
 *
 * Adds columns:
 * width    - width of boxplot
 * ymin     - lower whisker = smallest observation greater than or equal to lower hinge - 1.5 * IQR
 * lower    - lower hinge, 25% quantile
 * middle   - median, 50% quantile
 * upper    - upper hinge, 75% quantile
 * ymax     - upper whisker = largest observation less than or equal to upper hinge + 1.5 * IQR
 *
 *
 * notchlower   - lower edge of notch = median - 1.58 * IQR / sqrt(n)
 * notchupper   - upper edge of notch = median + 1.58 * IQR / sqrt(n)
 */
class BoxplotStat : BaseStat(DEF_MAPPING) {

    private var myWhiskerIQRRatio: Double = 0.toDouble()          // ggplot: 'coef'
    private var myComputeWidth = false    // ggplot: 'varWidth'

    /**
     * Calculate components of box and whisker plot.
     */
    init {
        myWhiskerIQRRatio = DEF_WHISKER_IQR_RATIO
    }

    fun setWhiskerIQRRatio(v: Double) {
        myWhiskerIQRRatio = v
    }

    fun setComputeWidth(b: Boolean) {
        myComputeWidth = b
    }

    override fun hasDefaultMapping(aes: Aes<*>): Boolean {
        return super.hasDefaultMapping(aes) || aes == WIDTH && myComputeWidth
    }

    override fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable {
        return if (aes == WIDTH) {
            Stats.WIDTH
        } else super.getDefaultMapping(aes)
    }

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X, Aes.Y)) {
            return withEmptyStatValues()
        }

        val xs = data.getNumeric(TransformVar.X)
        val ys = data.getNumeric(TransformVar.Y)
        val collector = HashMap<DataFrame.Variable, MutableList<Double>>()

        val maxCountPerBinOverall =
            BoxplotStatUtil.buildStat(xs, ys, myWhiskerIQRRatio, 0, collector)

        if (maxCountPerBinOverall == 0) {
            return withEmptyStatValues()
        }

        val statCount = collector.remove(Stats.WIDTH)
        if (myComputeWidth) {
            // 'width' is in range 0..1
            val statWidth = ArrayList<Double>()
            val norm = sqrt(maxCountPerBinOverall.toDouble())
            for (count in statCount!!) {
                statWidth.add(sqrt(count) / norm)
            }
            collector[Stats.WIDTH] = statWidth
        }

        val builder = DataFrame.Builder()
        for (key in collector.keys) {
            builder.putNumeric(key, collector[key]!!)
        }
        return builder.build()
    }

    companion object {
        const val DEF_WHISKER_IQR_RATIO = 1.5
        const val DEF_COMPUTE_WIDTH = false

        const val P_COEF = "coef"
        const val P_VARWIDTH = "varwidth"

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.YMIN to Stats.Y_MIN,
            Aes.YMAX to Stats.Y_MAX,
            Aes.LOWER to Stats.LOWER,
            Aes.MIDDLE to Stats.MIDDLE,
            Aes.UPPER to Stats.UPPER
        )
    }
}
