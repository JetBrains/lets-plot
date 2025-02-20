/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.Stat
import org.jetbrains.letsPlot.core.plot.base.stat.*
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.Option.Stat.Bin
import org.jetbrains.letsPlot.core.spec.Option.Stat.Bin2d
import org.jetbrains.letsPlot.core.spec.Option.Stat.BinHex
import org.jetbrains.letsPlot.core.spec.Option.Stat.Boxplot
import org.jetbrains.letsPlot.core.spec.Option.Stat.Contour
import org.jetbrains.letsPlot.core.spec.Option.Stat.Density
import org.jetbrains.letsPlot.core.spec.Option.Stat.Density2d
import org.jetbrains.letsPlot.core.spec.Option.Stat.DensityRidges
import org.jetbrains.letsPlot.core.spec.Option.Stat.ECDF
import org.jetbrains.letsPlot.core.spec.Option.Stat.QQ
import org.jetbrains.letsPlot.core.spec.Option.Stat.QQLine
import org.jetbrains.letsPlot.core.spec.Option.Stat.Smooth
import org.jetbrains.letsPlot.core.spec.Option.Stat.Summary
import org.jetbrains.letsPlot.core.spec.Option.Stat.YDensity
import org.jetbrains.letsPlot.core.spec.StatKind.*
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor

object StatProto {

    internal fun defaultOptions(
        statName: String,
        @Suppress("UNUSED_PARAMETER") geomKind: GeomKind
    ): Map<String, Any> {
        return when (StatKind.safeValueOf(statName)) {
            else -> emptyMap()
        }
    }

    internal fun preferredCoordinateSystem(statKind: StatKind): CoordProvider? {
        @Suppress("UNUSED_EXPRESSION")
        return when (statKind) {
//            BIN2D,
//            BINHEX,
//            CONTOUR,
//            CONTOURF,
//            DENSITY2D,
//            DENSITY2DF -> CoordProviders.fixed(1.0)

            else -> null
        }
    }

    internal fun createStat(statKind: StatKind, options: OptionsAccessor): Stat {
        return when (statKind) {
            IDENTITY -> Stats.IDENTITY
            COUNT -> Stats.count()
            COUNT2D -> Stats.count2d()
            BIN -> {
                Stats.bin(
                    binCount = options.getIntegerDef(Bin.BINS, BinStat.DEF_BIN_COUNT),
                    binWidth = options.getDouble(Bin.BINWIDTH),
                    center = options.getDouble(Bin.CENTER),
                    boundary = options.getDouble(Bin.BOUNDARY),
                    threshold = options.getDouble(Bin.THRESHOLD)
                )
            }

            BIN2D -> {
                val (binCountX, binCountY) = options.getNumPairDef(
                    Bin2d.BINS,
                    Pair(Bin2dStat.DEF_BINS, Bin2dStat.DEF_BINS)
                )
                val (binWidthX, binWidthY) = options.getNumQPairDef(
                    Bin2d.BINWIDTH,
                    Pair(Bin2dStat.DEF_BINWIDTH, Bin2dStat.DEF_BINWIDTH)
                )

                Bin2dStat(
                    binCountX = binCountX.toInt(),
                    binCountY = binCountY.toInt(),
                    binWidthX = binWidthX?.toDouble(),
                    binWidthY = binWidthY?.toDouble(),
                    drop = options.getBoolean(Bin2d.DROP, def = Bin2dStat.DEF_DROP)
                )
            }

            BINHEX -> {
                val (binCountX, binCountY) = options.getNumPairDef(
                    BinHex.BINS,
                    Pair(BinHexStat.DEF_BINS, BinHexStat.DEF_BINS)
                )
                val (binWidthX, binWidthY) = options.getNumQPairDef(
                    BinHex.BINWIDTH,
                    Pair(BinHexStat.DEF_BINWIDTH, BinHexStat.DEF_BINWIDTH)
                )

                BinHexStat(
                    binCountX = binCountX.toInt(),
                    binCountY = binCountY.toInt(),
                    binWidthX = binWidthX?.toDouble(),
                    binWidthY = binWidthY?.toDouble(),
                    drop = options.getBoolean(BinHex.DROP, def = BinHexStat.DEF_DROP)
                )
            }

            DOTPLOT -> configureDotplotStat(options)

            CONTOUR -> {
                ContourStat(
                    binCount = options.getIntegerDef(Contour.BINS, ContourStat.DEF_BIN_COUNT),
                    binWidth = options.getDouble(Contour.BINWIDTH),
                )
            }

            CONTOURF -> {
                ContourfStat(
                    binCount = options.getIntegerDef(Contour.BINS, ContourStat.DEF_BIN_COUNT),
                    binWidth = options.getDouble(Contour.BINWIDTH),
                )
            }

            SMOOTH -> configureSmoothStat(options)

            BOXPLOT -> {
                Stats.boxplot(
                    whiskerIQRRatio = options.getDouble(Boxplot.COEF) ?: BoxplotStat.DEF_WHISKER_IQR_RATIO,
                    computeWidth = options.getBoolean(Boxplot.VARWIDTH, BoxplotStat.DEF_COMPUTE_WIDTH)
                )
            }

            BOXPLOT_OUTLIER -> {
                Stats.boxplotOutlier(
                    whiskerIQRRatio = options.getDouble(Boxplot.COEF) ?: BoxplotStat.DEF_WHISKER_IQR_RATIO
                )
            }

            DENSITYRIDGES -> configureDensityRidgesStat(options)
            YDENSITY -> configureYDensityStat(options)
            YDOTPLOT -> configureYDotplotStat(options)
            DENSITY -> configureDensityStat(options)
            DENSITY2D -> configureDensity2dStat(options, false)
            DENSITY2DF -> configureDensity2dStat(options, true)
            StatKind.QQ -> configureQQStat(options)
            QQ2 -> Stats.qq2()
            QQ_LINE -> configureQQLineStat(options)
            QQ2_LINE -> configureQQ2LineStat(options)

            StatKind.ECDF -> {
                ECDFStat(
                    n = options.getInteger(ECDF.N),
                    padded = options.getBoolean(ECDF.PADDED, ECDFStat.DEF_PADDED)
                )
            }

            SUM -> Stats.sum()
            SUMMARY -> configureSummaryStat(options)
            SUMMARYBIN -> configureSummaryBinStat(options)
        }
    }

    private fun configureDotplotStat(options: OptionsAccessor): DotplotStat {

        val method = options.getString(Bin.METHOD)?.let {
            DotplotStat.Method.safeValueOf(it)
        }

        return Stats.dotplot(
            binCount = options.getIntegerDef(Bin.BINS, BinStat.DEF_BIN_COUNT),
            binWidth = options.getDouble(Bin.BINWIDTH),
            center = options.getDouble(Bin.CENTER),
            boundary = options.getDouble(Bin.BOUNDARY),
            method = method ?: DotplotStat.DEF_METHOD
        )
    }

    private fun configureSmoothStat(options: OptionsAccessor): SmoothStat {
        // Params:
        //  method - smoothing method: lm, glm, gam, loess, rlm
        //  n (80) - number of points to evaluate smoother at
        //  se (TRUE ) - display confidence interval around smooth?
        //  level (0.95) - level of confidence interval to use
        //  deg ( >= 1 ) - degree of polynomial for regression
        // seed  - random seed for LOESS sampling
        // max_n (1000)  - maximum points in DF for LOESS

        val smoothingMethod = options.getString(Smooth.METHOD)?.let {
            when (it.lowercase()) {
                "lm" -> SmoothStat.Method.LM
                "loess", "lowess" -> SmoothStat.Method.LOESS
                "glm" -> SmoothStat.Method.GLM
                "gam" -> SmoothStat.Method.GAM
                "rlm" -> SmoothStat.Method.RLM
                else -> throw IllegalArgumentException(
                    "Unsupported smoother method: '$it'\n" +
                            "Use one of: lm, loess, lowess, glm, gam, rlm."
                )
            }
        }

        return SmoothStat(
            smootherPointCount = options.getIntegerDef(Smooth.POINT_COUNT, SmoothStat.DEF_EVAL_POINT_COUNT),
            smoothingMethod = smoothingMethod ?: SmoothStat.DEF_SMOOTHING_METHOD,
            confidenceLevel = options.getDoubleDef(Smooth.CONFIDENCE_LEVEL, SmoothStat.DEF_CONFIDENCE_LEVEL),
            displayConfidenceInterval = options.getBoolean(
                Smooth.DISPLAY_CONFIDENCE_INTERVAL,
                SmoothStat.DEF_DISPLAY_CONFIDENCE_INTERVAL
            ),
            span = options.getDoubleDef(Smooth.SPAN, SmoothStat.DEF_SPAN),
            polynomialDegree = options.getIntegerDef(Smooth.POLYNOMIAL_DEGREE, SmoothStat.DEF_DEG),
            loessCriticalSize = options.getIntegerDef(Smooth.LOESS_CRITICAL_SIZE, SmoothStat.DEF_LOESS_CRITICAL_SIZE),
            samplingSeed = options.getLongDef(Smooth.SAMPLING_SEED, SmoothStat.DEF_SAMPLING_SEED)
        )
    }

    private fun configureDensityRidgesStat(options: OptionsAccessor): DensityRidgesStat {
        var bwValue: Double? = null
        var bwMethod: DensityStat.BandWidthMethod = DensityStat.DEF_BW
        options[Density.BAND_WIDTH]?.run {
            if (this is Number) {
                bwValue = this.toDouble()
            } else if (this is String) {
                bwMethod = DensityStatUtil.toBandWidthMethod(this)
            }
        }

        val kernel = options.getString(Density.KERNEL)?.let {
            DensityStatUtil.toKernel(it)
        }

        val quantiles = if (options.hasOwn(DensityRidges.QUANTILES)) {
            options.getBoundedDoubleList(DensityRidges.QUANTILES, 0.0, 1.0)
        } else DensityRidgesStat.DEF_QUANTILES

        return DensityRidgesStat(
            trim = options.getBoolean(DensityRidges.TRIM, DensityRidgesStat.DEF_TRIM),
            tailsCutoff = options.getDouble(DensityRidges.TAILS_CUTOFF),
            bandWidth = bwValue,
            bandWidthMethod = bwMethod,
            adjust = options.getDoubleDef(Density.ADJUST, DensityStat.DEF_ADJUST),
            kernel = kernel ?: DensityStat.DEF_KERNEL,
            n = options.getIntegerDef(Density.N, DensityStat.DEF_N),
            fullScanMax = options.getIntegerDef(Density.FULL_SCAN_MAX, DensityStat.DEF_FULL_SCAN_MAX),
            quantiles = quantiles
        )
    }

    private fun configureYDensityStat(options: OptionsAccessor): YDensityStat {
        val scale = options.getString(YDensity.SCALE)?.let {
            when (it.lowercase()) {
                "area" -> YDensityStat.Scale.AREA
                "count" -> YDensityStat.Scale.COUNT
                "width" -> YDensityStat.Scale.WIDTH
                else -> throw IllegalArgumentException(
                    "Unsupported scale: '$it'\n" +
                            "Use one of: area, count, width."
                )
            }
        }

        var bwValue: Double? = null
        var bwMethod: DensityStat.BandWidthMethod = DensityStat.DEF_BW
        options[Density.BAND_WIDTH]?.run {
            if (this is Number) {
                bwValue = this.toDouble()
            } else if (this is String) {
                bwMethod = DensityStatUtil.toBandWidthMethod(this)
            }
        }

        val kernel = options.getString(Density.KERNEL)?.let {
            DensityStatUtil.toKernel(it)
        }

        val quantiles = if (options.hasOwn(YDensity.QUANTILES)) {
            options.getBoundedDoubleList(YDensity.QUANTILES, 0.0, 1.0)
        } else {
            YDensityStat.DEF_QUANTILES
        }

        return YDensityStat(
            scale = scale ?: YDensityStat.DEF_SCALE,
            trim = options.getBoolean(YDensity.TRIM, YDensityStat.DEF_TRIM),
            tailsCutoff = options.getDoubleDef(YDensity.TAILS_CUTOFF, YDensityStat.DEF_TAILS_CUTOFF),
            bandWidth = bwValue,
            bandWidthMethod = bwMethod,
            adjust = options.getDoubleDef(Density.ADJUST, DensityStat.DEF_ADJUST),
            kernel = kernel ?: DensityStat.DEF_KERNEL,
            n = options.getIntegerDef(Density.N, DensityStat.DEF_N),
            fullScanMax = options.getIntegerDef(Density.FULL_SCAN_MAX, DensityStat.DEF_FULL_SCAN_MAX),
            quantiles = quantiles
        )
    }

    private fun configureYDotplotStat(options: OptionsAccessor): YDotplotStat {

        val method = options.getString(Bin.METHOD)?.let {
            DotplotStat.Method.safeValueOf(it)
        }

        return Stats.ydotplot(
            binCount = options.getIntegerDef(Bin.BINS, BinStat.DEF_BIN_COUNT),
            binWidth = options.getDouble(Bin.BINWIDTH),
            center = options.getDouble(Bin.CENTER),
            boundary = options.getDouble(Bin.BOUNDARY),
            method = method ?: DotplotStat.DEF_METHOD
        )
    }

    private fun configureDensityStat(options: OptionsAccessor): DensityStat {
        var bwValue: Double? = null
        var bwMethod: DensityStat.BandWidthMethod = DensityStat.DEF_BW
        options[Density.BAND_WIDTH]?.run {
            if (this is Number) {
                bwValue = this.toDouble()
            } else if (this is String) {
                bwMethod = DensityStatUtil.toBandWidthMethod(this)
            }
        }

        val kernel = options.getString(Density.KERNEL)?.let {
            DensityStatUtil.toKernel(it)
        }

        val quantiles = if (options.hasOwn(Density.QUANTILES)) {
            options.getBoundedDoubleList(Density.QUANTILES, 0.0, 1.0)
        } else DensityStat.DEF_QUANTILES

        return DensityStat(
            trim = options.getBoolean(Density.TRIM, DensityStat.DEF_TRIM),
            bandWidth = bwValue,
            bandWidthMethod = bwMethod,
            adjust = options.getDoubleDef(Density.ADJUST, DensityStat.DEF_ADJUST),
            kernel = kernel ?: DensityStat.DEF_KERNEL,
            n = options.getIntegerDef(Density.N, DensityStat.DEF_N),
            fullScanMax = options.getIntegerDef(Density.FULL_SCAN_MAX, DensityStat.DEF_FULL_SCAN_MAX),
            quantiles = quantiles
        )
    }

    private fun configureDensity2dStat(options: OptionsAccessor, filled: Boolean): AbstractDensity2dStat {
        var bwValueX: Double? = null
        var bwValueY: Double? = null
        var bwMethod: DensityStat.BandWidthMethod? = null
        options[Density2d.BAND_WIDTH]?.run {
            if (this is Number) {
                bwValueX = this.toDouble()
                bwValueY = this.toDouble()
            } else if (this is String) {
                bwMethod = DensityStatUtil.toBandWidthMethod(this)
            } else if (this is List<*>) {
                for ((i, v) in this.withIndex()) {
                    when (i) {
                        0 -> bwValueX = v?.let { (v as Number).toDouble() }
                        1 -> bwValueY = v?.let { (v as Number).toDouble() }
                        else -> break
                    }
                }
            }
        }

        val kernel = options.getString(Density2d.KERNEL)?.let {
            DensityStatUtil.toKernel(it)
        }

        var nX: Int? = null
        var nY: Int? = null
        options[Density2d.N]?.run {
            if (this is Number) {
                nX = this.toInt()
                nY = this.toInt()
            } else if (this is List<*>) {
                for ((i, v) in this.withIndex()) {
                    when (i) {
                        0 -> nX = v?.let { (v as Number).toInt() }
                        1 -> nY = v?.let { (v as Number).toInt() }
                        else -> break
                    }
                }
            }
        }

        return if (filled) {
            Density2dfStat(
                bandWidthX = bwValueX,
                bandWidthY = bwValueY,
                bandWidthMethod = bwMethod ?: AbstractDensity2dStat.DEF_BW,
                adjust = options.getDoubleDef(Density2d.ADJUST, AbstractDensity2dStat.DEF_ADJUST),
                kernel = kernel ?: AbstractDensity2dStat.DEF_KERNEL,
                nX = nX ?: AbstractDensity2dStat.DEF_N,
                nY = nY ?: AbstractDensity2dStat.DEF_N,
                isContour = options.getBoolean(Density2d.IS_CONTOUR, AbstractDensity2dStat.DEF_CONTOUR),
                binCount = options.getIntegerDef(Density2d.BINS, AbstractDensity2dStat.DEF_BIN_COUNT),
                binWidth = options.getDoubleDef(Density2d.BINWIDTH, AbstractDensity2dStat.DEF_BIN_WIDTH)
            )
        } else {
            Density2dStat(
                bandWidthX = bwValueX,
                bandWidthY = bwValueY,
                bandWidthMethod = bwMethod ?: AbstractDensity2dStat.DEF_BW,
                adjust = options.getDoubleDef(Density2d.ADJUST, AbstractDensity2dStat.DEF_ADJUST),
                kernel = kernel ?: AbstractDensity2dStat.DEF_KERNEL,
                nX = nX ?: AbstractDensity2dStat.DEF_N,
                nY = nY ?: AbstractDensity2dStat.DEF_N,
                isContour = options.getBoolean(Density2d.IS_CONTOUR, AbstractDensity2dStat.DEF_CONTOUR),
                binCount = options.getIntegerDef(Density2d.BINS, AbstractDensity2dStat.DEF_BIN_COUNT),
                binWidth = options.getDoubleDef(Density2d.BINWIDTH, AbstractDensity2dStat.DEF_BIN_WIDTH)
            )
        }
    }

    private fun configureQQStat(options: OptionsAccessor): QQStat {
        val distribution = options.getString(QQ.DISTRIBUTION)?.let {
            QQStat.Distribution.safeValueOf(it)
        }
        val distributionParameters = options.getDoubleList(QQ.DISTRIBUTION_PARAMETERS)

        return Stats.qq(distribution ?: QQStat.DEF_DISTRIBUTION, distributionParameters)
    }

    private fun configureQQLineStat(options: OptionsAccessor): QQLineStat {
        val distribution = options.getString(QQLine.DISTRIBUTION)?.let {
            QQStat.Distribution.safeValueOf(it)
        }
        val distributionParameters = options.getDoubleList(QQLine.DISTRIBUTION_PARAMETERS)
        val lineQuantiles: Pair<Double, Double>? = options[QQLine.LINE_QUANTILES]?.let {
            options.getOrderedBoundedDoubleDistinctPair(QQLine.LINE_QUANTILES, 0.0, 1.0)
        }

        return Stats.qqline(
            distribution ?: QQStat.DEF_DISTRIBUTION,
            distributionParameters,
            lineQuantiles ?: QQLineStat.DEF_LINE_QUANTILES
        )
    }

    private fun configureQQ2LineStat(options: OptionsAccessor): QQ2LineStat {
        val lineQuantiles: Pair<Double, Double>? = options[QQLine.LINE_QUANTILES]?.let {
            options.getOrderedBoundedDoubleDistinctPair(QQLine.LINE_QUANTILES, 0.0, 1.0)
        }

        return Stats.qq2line(lineQuantiles ?: QQLineStat.DEF_LINE_QUANTILES)
    }

    private fun configureSummaryStat(options: OptionsAccessor): SummaryStat {
        val (yAggFun, yMinFun, yMaxFun) = getSummaryAggFunctions(options)

        return SummaryStat(yAggFun, yMinFun, yMaxFun)
    }

    private fun configureSummaryBinStat(options: OptionsAccessor): SummaryBinStat {
        val (yAggFun, yMinFun, yMaxFun) = getSummaryAggFunctions(options)

        val boundary = options.getDouble(Bin.BOUNDARY)
        val center = options.getDouble(Bin.CENTER)
        val (xPos, xPosKind) = when {
            boundary != null -> Pair(boundary, BinStat.XPosKind.BOUNDARY)
            center != null -> Pair(center, BinStat.XPosKind.CENTER)
            else -> Pair(0.0, BinStat.XPosKind.NONE)
        }

        return SummaryBinStat(
            options.getIntegerDef(Bin.BINS, BinStat.DEF_BIN_COUNT),
            options.getDouble(Bin.BINWIDTH),
            xPosKind,
            xPos,
            yAggFun, yMinFun, yMaxFun
        )
    }

    private fun getSummaryAggFunctions(options: OptionsAccessor): Triple<(List<Double>) -> Double, (List<Double>) -> Double, (List<Double>) -> Double> {
        val (lq, mq, uq) = getSummaryQuantiles(options)
        return Triple(
            options.getString(Summary.FUN)?.let { getAggFunction(it, lq, mq, uq) } ?: AggregateFunctions::mean,
            options.getString(Summary.FUN_MIN)?.let { getAggFunction(it, lq, mq, uq) } ?: AggregateFunctions::min,
            options.getString(Summary.FUN_MAX)?.let { getAggFunction(it, lq, mq, uq) } ?: AggregateFunctions::max
        )
    }

    private fun getAggFunction(funcName: String, lq: Double, mq: Double, uq: Double): ((List<Double>) -> Double) {
        val functions = mapOf(
            Summary.Functions.COUNT to AggregateFunctions::count,
            Summary.Functions.SUM to AggregateFunctions::sum,
            Summary.Functions.MEAN to AggregateFunctions::mean,
            Summary.Functions.MEDIAN to AggregateFunctions::median,
            Summary.Functions.MIN to AggregateFunctions::min,
            Summary.Functions.MAX to AggregateFunctions::max,
            Summary.Functions.LQ to { values -> AggregateFunctions.quantile(values, lq) },
            Summary.Functions.MQ to { values -> AggregateFunctions.quantile(values, mq) },
            Summary.Functions.UQ to { values -> AggregateFunctions.quantile(values, uq) }
        )

        return functions[funcName]
            ?: throw IllegalArgumentException(
                "Unsupported function name: '$funcName'\n" +
                        "Use one of: ${functions.keys.joinToString()}."
            )
    }

    private fun getSummaryQuantiles(options: OptionsAccessor): Triple<Double, Double, Double> {
        return if (options.hasOwn(Summary.QUANTILES)) {
            val quantiles = options.getBoundedDoubleList(Summary.QUANTILES, 0.0, 1.0)
            require(quantiles.size == 3) { "Parameter 'quantiles' should contains 3 values" }
            val (lq, mq, uq) = quantiles.sorted()
            Triple(lq, mq, uq)
        } else {
            SummaryStat.DEF_QUANTILES
        }
    }
}
