/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.stat.*
import jetbrains.datalore.plot.config.Option.Stat.Bin
import jetbrains.datalore.plot.config.Option.Stat.Bin2d
import jetbrains.datalore.plot.config.Option.Stat.Boxplot
import jetbrains.datalore.plot.config.Option.Stat.Contour
import jetbrains.datalore.plot.config.Option.Stat.Density
import jetbrains.datalore.plot.config.Option.Stat.Density2d
import jetbrains.datalore.plot.config.Option.Stat.Smooth
import jetbrains.datalore.plot.config.Option.Stat.YDensity
import jetbrains.datalore.plot.config.Option.Stat.QQ
import jetbrains.datalore.plot.config.Option.Stat.QQLine

object StatProto {

    internal fun defaultOptions(
        statName: String,
        @Suppress("UNUSED_PARAMETER") geomKind: GeomKind
    ): Map<String, Any> {
        return when (StatKind.safeValueOf(statName)) {
            else -> emptyMap()
        }
    }

    internal fun createStat(statKind: StatKind, options: OptionsAccessor): Stat {
        when (statKind) {
            StatKind.IDENTITY -> return Stats.IDENTITY
            StatKind.COUNT -> return Stats.count()
            StatKind.COUNT2D -> return Stats.count2d()
            StatKind.BIN -> {
                return Stats.bin(
                    binCount = options.getIntegerDef(Bin.BINS, BinStat.DEF_BIN_COUNT),
                    binWidth = options.getDouble(Bin.BINWIDTH),
                    center = options.getDouble(Bin.CENTER),
                    boundary = options.getDouble(Bin.BOUNDARY)
                )
            }

            StatKind.BIN2D -> {
                val (binCountX, binCountY) = options.getNumPairDef(
                    Bin2d.BINS,
                    Pair(Bin2dStat.DEF_BINS, Bin2dStat.DEF_BINS)
                )
                val (binWidthX, binWidthY) = options.getNumQPairDef(
                    Bin2d.BINWIDTH,
                    Pair(Bin2dStat.DEF_BINWIDTH, Bin2dStat.DEF_BINWIDTH)
                )

                return Bin2dStat(
                    binCountX = binCountX.toInt(),
                    binCountY = binCountY.toInt(),
                    binWidthX = binWidthX?.toDouble(),
                    binWidthY = binWidthY?.toDouble(),
                    drop = options.getBoolean(Bin2d.DROP, def = Bin2dStat.DEF_DROP)
                )
            }

            StatKind.DOTPLOT -> return configureDotplotStat(options)

            StatKind.CONTOUR -> {
                return ContourStat(
                    binCount = options.getIntegerDef(Contour.BINS, ContourStat.DEF_BIN_COUNT),
                    binWidth = options.getDouble(Contour.BINWIDTH),
                )
            }

            StatKind.CONTOURF -> {
                return ContourfStat(
                    binCount = options.getIntegerDef(Contour.BINS, ContourStat.DEF_BIN_COUNT),
                    binWidth = options.getDouble(Contour.BINWIDTH),
                )
            }

            StatKind.SMOOTH -> return configureSmoothStat(options)

            StatKind.BOXPLOT -> {
                return Stats.boxplot(
                    whiskerIQRRatio = options.getDoubleDef(Boxplot.COEF, BoxplotStat.DEF_WHISKER_IQR_RATIO),
                    computeWidth = options.getBoolean(Boxplot.VARWIDTH, BoxplotStat.DEF_COMPUTE_WIDTH)
                )
            }

            StatKind.YDENSITY -> return configureYDensityStat(options)

            StatKind.YDOTPLOT -> return configureYDotplotStat(options)

            StatKind.DENSITY -> return configureDensityStat(options)

            StatKind.DENSITY2D -> return configureDensity2dStat(options, false)

            StatKind.DENSITY2DF -> return configureDensity2dStat(options, true)

            StatKind.QQ -> return configureQQStat(options)

            StatKind.QQ2 -> return Stats.qq2()

            StatKind.QQ_LINE -> return configureQQLineStat(options)

            StatKind.QQ2_LINE -> return configureQQ2LineStat(options)

            else -> throw IllegalArgumentException("Unknown stat: '$statKind'")
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

        return YDensityStat(
            scale = scale ?: YDensityStat.DEF_SCALE,
            trim = options.getBoolean(YDensity.TRIM, YDensityStat.DEF_TRIM),
            bandWidth = bwValue,
            bandWidthMethod = bwMethod,
            adjust = options.getDoubleDef(Density.ADJUST, DensityStat.DEF_ADJUST),
            kernel = kernel ?: DensityStat.DEF_KERNEL,
            n = options.getIntegerDef(Density.N, DensityStat.DEF_N),
            fullScanMax = options.getIntegerDef(Density.FULL_SCAN_MAX, DensityStat.DEF_FULL_SCAN_MAX)
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

        return DensityStat(
            trim = options.getBoolean(Density.TRIM, DensityStat.DEF_TRIM),
            bandWidth = bwValue,
            bandWidthMethod = bwMethod,
            adjust = options.getDoubleDef(Density.ADJUST, DensityStat.DEF_ADJUST),
            kernel = kernel ?: DensityStat.DEF_KERNEL,
            n = options.getIntegerDef(Density.N, DensityStat.DEF_N),
            fullScanMax = options.getIntegerDef(Density.FULL_SCAN_MAX, DensityStat.DEF_FULL_SCAN_MAX),
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
}
