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
import jetbrains.datalore.plot.config.Option.Stat.Corr
import jetbrains.datalore.plot.config.Option.Stat.Density
import jetbrains.datalore.plot.config.Option.Stat.Density2d
import jetbrains.datalore.plot.config.Option.Stat.Smooth

object StatProto {

    internal fun defaultOptions(statName: String, geomKind: GeomKind): Map<String, Any> {
        return when (StatKind.safeValueOf(statName)) {
            StatKind.CORR -> {
                when (geomKind) {
                    GeomKind.TILE -> mapOf<String, Any>(
                        "size" to 0.0   // 'corr' is mapped to the outline color 'color' - avoid on 'tiles'.
                    )
                    GeomKind.POINT,
                    GeomKind.TEXT -> mapOf<String, Any>(
                        "size" to 0.8,
                        "size_unit" to "x",
                        "label_format" to ".2f"
                    )
                    else -> emptyMap()
                }
            }
            else -> emptyMap()
        }
    }

    internal fun createStat(statKind: StatKind, options: OptionsAccessor): Stat {
        when (statKind) {
            StatKind.IDENTITY -> return Stats.IDENTITY
            StatKind.COUNT -> return Stats.count()
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

            StatKind.CORR -> return configureCorrStat(options)

            StatKind.BOXPLOT -> {
                return Stats.boxplot(
                    whiskerIQRRatio = options.getDoubleDef(Boxplot.COEF, BoxplotStat.DEF_WHISKER_IQR_RATIO),
                    computeWidth = options.getBoolean(Boxplot.VARWIDTH, BoxplotStat.DEF_COMPUTE_WIDTH)
                )
            }

            StatKind.DENSITY -> return configureDensityStat(options)

            StatKind.DENSITY2D -> return configureDensity2dStat(options, false)

            StatKind.DENSITY2DF -> return configureDensity2dStat(options, true)

            else -> throw IllegalArgumentException("Unknown stat: '$statKind'")
        }
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
            when (it.toLowerCase()) {
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
            samplingSeed = options.getLongDef(Smooth.LOESS_CRITICAL_SIZE, SmoothStat.DEF_SAMPLING_SEED)
        )
    }

    private fun configureCorrStat(options: OptionsAccessor): CorrelationStat {
        val correlationMethod = options.getString(Corr.METHOD)?.let {
            when (it.toLowerCase()) {
                "pearson" -> CorrelationStat.Method.PEARSON
                else -> throw IllegalArgumentException("Unsupported correlation method: '$it'. Must be: 'pearson'")
            }
        }

        val type = options.getString(Corr.TYPE)?.let {
            when (it.toLowerCase()) {
                "full" -> CorrelationStat.Type.FULL
                "upper" -> CorrelationStat.Type.UPPER
                "lower" -> CorrelationStat.Type.LOWER
                else -> throw IllegalArgumentException("Unsupported matrix type: '$it'. Expected: 'full', 'upper' or 'lower'.")
            }
        }

        return CorrelationStat(
            correlationMethod = correlationMethod ?: CorrelationStat.DEF_CORRELATION_METHOD,
            type = type ?: CorrelationStat.DEF_TYPE,
            fillDiagonal = options.getBoolean(Corr.FILL_DIAGONAL, CorrelationStat.DEF_FILL_DIAGONAL),
            threshold = options.getDouble(Corr.THRESHOLD) ?: CorrelationStat.DEF_THRESHOLD
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
            bandWidth = bwValue,
            bandWidthMethod = bwMethod,
            adjust = options.getDoubleDef(Density.ADJUST, DensityStat.DEF_ADJUST),
            kernel = kernel ?: DensityStat.DEF_KERNEL,
            n = options.getIntegerDef(Density.N, DensityStat.DEF_N)
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
}
