/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable.Source.STAT
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.StatContext

object Stats {
    // stat variables can be referenced by name ..name.. (p 54)
    val X = DataFrame.Variable("..x..", STAT, "x")
    val Y = DataFrame.Variable("..y..", STAT, "y")
    val COUNT = DataFrame.Variable("..count..", STAT, "count")
    val DENSITY = DataFrame.Variable("..density..", STAT, "density")
    val Y_MIN = DataFrame.Variable("..ymin..", STAT, "y min")
    val Y_MAX = DataFrame.Variable("..ymax..", STAT, "y max")
    val SE = DataFrame.Variable("..se..", STAT, "standard error")
    val LEVEL = DataFrame.Variable("..level..", STAT, "level")

    val LOWER = DataFrame.Variable("..lower..", STAT, "lower")
    val MIDDLE = DataFrame.Variable("..middle..", STAT, "middle")
    val UPPER = DataFrame.Variable("..upper..", STAT, "upper")
    val WIDTH = DataFrame.Variable("..width..", STAT, "width")

    val CORR = DataFrame.Variable("..corr..", STAT, "corr")
    val CORR_ABS = DataFrame.Variable("..corr_abs..", STAT, "corr_abs")

    val SCALED = DataFrame.Variable("..scaled..", STAT, "scaled")

    val GROUP = DataFrame.Variable("..group..", STAT, "group")

    val IDENTITY: Stat = IdentityStat()

    private val VARS: Map<String, DataFrame.Variable> = run {
        val variableList = listOf(
            X,
            Y,
            COUNT,
            DENSITY,
            Y_MIN,
            Y_MAX,
            SE,
            LEVEL,
            LOWER,
            MIDDLE,
            UPPER,
            WIDTH,
            SCALED,
            GROUP,
            CORR,
            CORR_ABS
        )

        val result = HashMap<String, DataFrame.Variable>()
        for (variable in variableList) {
            result[variable.name] = variable
        }
        result
    }

    fun isStatVar(varName: String): Boolean {
        return VARS.containsKey(varName)
    }

    fun statVar(varName: String): DataFrame.Variable {
        checkArgument(VARS.containsKey(varName), "Unknown stat variable $varName")
        return VARS[varName]!!
    }

    fun defaultMapping(stat: Stat): Map<Aes<*>, DataFrame.Variable> {
        val map = HashMap<Aes<*>, DataFrame.Variable>()
        for (aes in Aes.values()) {
            if (stat.hasDefaultMapping(aes)) {
                val `var` = stat.getDefaultMapping(aes)
                map[aes] = `var`
            }
        }
        return map
    }

    fun count(): Stat {
        return CountStat()
    }

    fun bin(
        binCount: Int = BinStat.DEF_BIN_COUNT,
        binWidth: Double? = null,
        center: Double? = null,
        boundary: Double? = null
    ): BinStat {
        var xPosKind = BinStat.XPosKind.NONE
        var xPosValue = 0.0
        if (boundary != null) {
            xPosKind = BinStat.XPosKind.BOUNDARY
            xPosValue = boundary
        } else if (center != null) {
            xPosKind = BinStat.XPosKind.CENTER
            xPosValue = center
        }

        return BinStat(
            binCount = binCount,
            binWidth = binWidth,
            xPosKind = xPosKind,
            xPos = xPosValue
        )
    }

    fun smooth(
        smootherPointCount: Int = SmoothStat.DEF_EVAL_POINT_COUNT,
        smoothingMethod: SmoothStat.Method = SmoothStat.DEF_SMOOTHING_METHOD,
        confidenceLevel: Double = SmoothStat.DEF_CONFIDENCE_LEVEL,
        displayConfidenceInterval: Boolean = SmoothStat.DEF_DISPLAY_CONFIDENCE_INTERVAL,
        span: Double = SmoothStat.DEF_SPAN,
        polynomialDegree: Int = SmoothStat.DEF_DEG,
        loessCriticalSize: Int = SmoothStat.DEF_LOESS_CRITICAL_SIZE,
        samplingSeed: Long = SmoothStat.DEF_SAMPLING_SEED
    ): SmoothStat {
        return SmoothStat(
            smootherPointCount = smootherPointCount,
            smoothingMethod = smoothingMethod,
            confidenceLevel = confidenceLevel,
            displayConfidenceInterval = displayConfidenceInterval,
            span = span,
            polynomialDegree = polynomialDegree,
            loessCriticalSize = loessCriticalSize,
            samplingSeed = samplingSeed
        )
    }

    fun corr(
        correlationMethod: CorrelationStat.Method = CorrelationStat.DEF_CORRELATION_METHOD,
        type: CorrelationStat.Type = CorrelationStat.DEF_TYPE,
        fillDiagonal: Boolean = CorrelationStat.DEF_FILL_DIAGONAL,
        threshold: Double = CorrelationStat.DEF_THRESHOLD
    ): CorrelationStat {
        return CorrelationStat(
            correlationMethod = correlationMethod,
            type = type,
            fillDiagonal = fillDiagonal,
            threshold = threshold
        )
    }

    fun contour(
        binCount: Int = ContourStat.DEF_BIN_COUNT,
        binWidth: Double? = null
    ): ContourStat {
        return ContourStat(
            binCount = binCount,
            binWidth = binWidth
        )
    }

    fun contourf(
        binCount: Int = ContourStat.DEF_BIN_COUNT,
        binWidth: Double? = null
    ): ContourfStat {
        return ContourfStat(
            binCount = binCount,
            binWidth = binWidth
        )
    }

    fun boxplot(
        whiskerIQRRatio: Double = BoxplotStat.DEF_WHISKER_IQR_RATIO,
        computeWidth: Boolean = BoxplotStat.DEF_COMPUTE_WIDTH
    ): BoxplotStat {
        return BoxplotStat(whiskerIQRRatio, computeWidth)
    }

    fun density(
        bandWidth: Double? = null,
        bandWidthMethod: DensityStat.BandWidthMethod = DensityStat.DEF_BW,
        adjust: Double = DensityStat.DEF_ADJUST,
        kernel: DensityStat.Kernel = DensityStat.DEF_KERNEL,
        n: Int = DensityStat.DEF_N,
        fullScalMax: Int = DensityStat.DEF_FULL_SCAN_MAX
    ): DensityStat {
        return DensityStat(
            bandWidth = bandWidth,
            bandWidthMethod = bandWidthMethod,
            adjust = adjust,
            kernel = kernel,
            n = n,
            fullScalMax = fullScalMax
        )
    }

    fun density2d(
        bandWidthX: Double? = null,
        bandWidthY: Double? = null,
        bandWidthMethod: DensityStat.BandWidthMethod = AbstractDensity2dStat.DEF_BW,  // Used is `bandWidth` is not set.
        adjust: Double = AbstractDensity2dStat.DEF_ADJUST,
        kernel: DensityStat.Kernel = AbstractDensity2dStat.DEF_KERNEL,
        nX: Int = AbstractDensity2dStat.DEF_N,
        nY: Int = AbstractDensity2dStat.DEF_N,
        isContour: Boolean = AbstractDensity2dStat.DEF_CONTOUR,
        binCount: Int = AbstractDensity2dStat.DEF_BIN_COUNT,
        binWidth: Double = AbstractDensity2dStat.DEF_BIN_WIDTH
    ): AbstractDensity2dStat {
        return Density2dStat(
            bandWidthX = bandWidthX,
            bandWidthY = bandWidthY,
            bandWidthMethod = bandWidthMethod,
            adjust = adjust,
            kernel = kernel,
            nX = nX,
            nY = nY,
            isContour = isContour,
            binCount = binCount,
            binWidth = binWidth
        )
    }

    fun density2df(
        bandWidthX: Double? = null,
        bandWidthY: Double? = null,
        bandWidthMethod: DensityStat.BandWidthMethod,  // Used is `bandWidth` is not set.
        adjust: Double = AbstractDensity2dStat.DEF_ADJUST,
        kernel: DensityStat.Kernel = AbstractDensity2dStat.DEF_KERNEL,
        nX: Int = AbstractDensity2dStat.DEF_N,
        nY: Int = AbstractDensity2dStat.DEF_N,
        isContour: Boolean = AbstractDensity2dStat.DEF_CONTOUR,
        binCount: Int = AbstractDensity2dStat.DEF_BIN_COUNT,
        binWidth: Double = AbstractDensity2dStat.DEF_BIN_WIDTH
    ): AbstractDensity2dStat {
        return Density2dfStat(
            bandWidthX = bandWidthX,
            bandWidthY = bandWidthY,
            bandWidthMethod = bandWidthMethod,
            adjust = adjust,
            kernel = kernel,
            nX = nX,
            nY = nY,
            isContour = isContour,
            binCount = binCount,
            binWidth = binWidth
        )
    }

    private class IdentityStat internal constructor() : BaseStat(emptyMap()) {

        override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
            return DataFrame.Builder.emptyFrame()
        }

        override fun consumes(): List<Aes<*>> {
            return emptyList()
        }
    }
}
