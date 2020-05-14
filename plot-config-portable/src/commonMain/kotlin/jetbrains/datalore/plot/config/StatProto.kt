/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.stat.*
import jetbrains.datalore.plot.base.stat.BoxplotStat.Companion.P_COEF
import jetbrains.datalore.plot.base.stat.BoxplotStat.Companion.P_VARWIDTH

class StatProto {

    internal fun defaultOptions(statName: String): Map<String, Any> {
        checkArgument(DEFAULTS.containsKey(statName), "Unknown stat name: '$statName'")
        return DEFAULTS[statName]!!
    }

    internal fun createStat(statKind: StatKind, options: Map<*, *>): Stat {
        // ToDo: pass 'option accessor'
        // ToDo: get rid of 'if'-s: everything must be present in the 'defaults'
        // ToDo: get rid of XXXStatBuilder
        // ToDo: see BIN2D
        when (statKind) {
            StatKind.IDENTITY -> return Stats.IDENTITY
            StatKind.COUNT -> return Stats.count()
            StatKind.BIN -> {
                val binStat = Stats.bin()
                if (options.containsKey("bins")) {
                    binStat.binCount((options["bins"] as Number).toInt())
                }
                if (options.containsKey("binwidth")) {
                    binStat.binWidth((options["binwidth"] as Number).toDouble())
                }
                if (options.containsKey("center")) {
                    binStat.center((options["center"] as Number).toDouble())
                }
                if (options.containsKey("boundary")) {
                    binStat.boundary((options["boundary"] as Number).toDouble())
                }
                return binStat.build()
            }

            StatKind.BIN2D -> {
                val opts = OptionsAccessor.over(options)
                val (binCountX, binCountY) = opts.getNumPair(Bin2dStat.P_BINS)
                val (binWidthX, binWidthY) = opts.getNumQPair(Bin2dStat.P_BINWIDTH)

                return Bin2dStat(
                    binCountX = binCountX.toInt(),
                    binCountY = binCountY.toInt(),
                    binWidthX = binWidthX?.toDouble(),
                    binWidthY = binWidthY?.toDouble(),
                    drop = opts.getBoolean(Bin2dStat.P_BINWIDTH, def = Bin2dStat.DEF_DROP)
                )
            }

            StatKind.CONTOUR -> {
                val contourStat = Stats.contour()
                if (options.containsKey("bins")) {
                    contourStat.binCount((options["bins"] as Number).toInt())
                }
                if (options.containsKey("binwidth")) {
                    contourStat.binWidth((options["binwidth"] as Number).toDouble())
                }
                return contourStat.build()
            }

            StatKind.CONTOURF -> {
                val contourfStat = Stats.contourf()
                if (options.containsKey("bins")) {
                    contourfStat.binCount((options["bins"] as Number).toInt())
                }
                if (options.containsKey("binwidth")) {
                    contourfStat.binWidth((options["binwidth"] as Number).toDouble())
                }
                return contourfStat.build()
            }

            StatKind.SMOOTH -> return configureSmoothStat(options)

            StatKind.BOXPLOT -> {
                val boxplotStat = Stats.boxplot()
                val opts = OptionsAccessor.over(options)
                boxplotStat.setComputeWidth(opts.getBoolean(P_VARWIDTH))
                boxplotStat.setWhiskerIQRRatio(opts.getDouble(P_COEF)!!)
                return boxplotStat
            }

            StatKind.DENSITY -> {
                val densityStat = Stats.density()
                if (options.containsKey("kernel")) {
                    val method = options["kernel"] as String
                    densityStat.setKernel(DensityStatUtil.toKernel(method))
                }
                if (options.containsKey("bw")) {
                    val bw = options["bw"]
                    if (bw is Number) {
                        densityStat.setBandWidth(bw.toDouble())
                    } else {
                        densityStat.setBandWidthMethod(DensityStatUtil.toBandWidthMethod(bw as String))
                    }
                }
                if (options.containsKey("n")) {
                    densityStat.setN((options["n"] as Number).toInt())
                }
                if (options.containsKey("adjust")) {
                    densityStat.setAdjust((options["adjust"] as Number).toDouble())
                }
                return densityStat
            }

            StatKind.DENSITY2D -> {
                val density2dStat = Stats.density2d()
                return configureDensity2dStat(density2dStat, options)
            }

            StatKind.DENSITY2DF -> {
                val density2dfStat = Stats.density2df()
                return configureDensity2dStat(density2dfStat, options)
            }

            else -> throw IllegalArgumentException("Unknown stat: '$statKind'")
        }
    }

    private fun configureSmoothStat(options: Map<*, *>): Stat {
        // Params:
        //  method - smoothing method: lm, glm, gam, loess, rlm
        //  n (80) - number of points to evaluate smoother at
        //  se (TRUE ) - display confidence interval around smooth?
        //  level (0.95) - level of confidence interval to use
        //  deg ( >= 1 ) - degree of polynomial for regression
        // seed  - random seed for LOESS sampling
        // max_n (1000)  - maximum points in DF for LOESS
        val stat = Stats.smooth()

        if (options.containsKey("n")) {
            stat.smootherPointCount = (options["n"] as Number).toInt()
        }

        if (options.containsKey("method")) {
            val method = options["method"] as String
            stat.smoothingMethod = when (method) {
                "lm" -> SmoothStat.Method.LM
                "loess", "lowess" -> SmoothStat.Method.LOESS
                "glm" -> SmoothStat.Method.GLM
                "gam" -> SmoothStat.Method.GAM
                "rlm" -> SmoothStat.Method.RLM
                else -> throw IllegalArgumentException("Unsupported smoother method: $method")
            }
        }

        if (options.containsKey("level")) {
            stat.confidenceLevel = (options["level"] as Number).toDouble()
        }

        if (options.containsKey("se")) {
            val se = options["se"]
            if (se is Boolean) {
                stat.isDisplayConfidenceInterval = se
            }
        }

        options["span"]?.let { stat.span = it.asDouble() }
        options["deg"]?.let { stat.deg = it.asInt() }

        options["seed"]?.let { stat.seed = it.asLong() }
        options["max_n"]?.let { stat.loessCriticalSize = it.asInt() }


        return stat
    }

    private fun configureDensity2dStat(stat: AbstractDensity2dStat, options: Map<*, *>): Stat {
        if (options.containsKey("kernel")) {
            val method = options["kernel"] as String
            stat.setKernel(DensityStatUtil.toKernel(method))
        }
        if (options.containsKey("bw")) {
            val bw = options["bw"]
            if (bw is List<*>) {
                for (i in bw.indices) {
                    val v = bw[i]
                    if (i == 0) {
                        stat.setBandWidthX((v as Number).toDouble())
                    } else {
                        stat.setBandWidthY((v as Number).toDouble())
                        break
                    }
                }
            } else if (bw is Number) {
                stat.setBandWidthX(bw.toDouble())
                stat.setBandWidthY(bw.toDouble())
            } else if (bw is String) {
                stat.bandWidthMethod = DensityStatUtil.toBandWidthMethod(bw)
            }
        }
        if (options.containsKey("n")) {
            val n = options["n"]
            if (n is List<*>) {
                for (i in n.indices) {
                    val v = n[i]
                    if (i == 0) {
                        stat.nx = (v as Number).toInt()
                    } else {
                        stat.ny = (v as Number).toInt()
                        break
                    }
                }
            } else if (n is Number) {
                stat.nx = n.toInt()
                stat.ny = n.toInt()
            }
        }
        if (options.containsKey("adjust")) {
            stat.adjust = (options["adjust"] as Number).toDouble()
        }
        if (options.containsKey("contour")) {
            stat.isContour = options["contour"] as Boolean
        }
        if (options.containsKey("bins")) {
            stat.setBinCount((options["bins"] as Number).toInt())
        }
        if (options.containsKey("binwidth")) {
            stat.setBinWidth((options["binwidth"] as Number).toDouble())
        }
        return stat
    }

    private fun Any?.asDouble() = (this as Number).toDouble()

    private fun Any?.asInt() = (this as Number).toInt()

    private fun Any?.asLong() = (this as Number).toLong()

    companion object {
        private val DEFAULTS = HashMap<String, Map<String, Any>>()

        // ToDo: add default geom
        init {
            DEFAULTS["identity"] = emptyMap()
            DEFAULTS["count"] = emptyMap()
            DEFAULTS["bin"] = createBinDefaults()
            DEFAULTS["bin2d"] = createBin2dDefaults()
            DEFAULTS["smooth"] = emptyMap()
            DEFAULTS["contour"] = createContourDefaults()
            DEFAULTS["contourf"] = createContourfDefaults()
            DEFAULTS["boxplot"] = createBoxplotDefaults()
            DEFAULTS["density"] = createDensityDefaults()
            DEFAULTS["density2d"] = createDensity2dDefaults()
            DEFAULTS["density2df"] = createDensity2dDefaults()
        }

        private fun createBinDefaults(): Map<String, Any> {
            return mapOf(
                "bins" to BinStatBuilder.DEF_BIN_COUNT
            )
        }

        private fun createBin2dDefaults(): Map<String, Any> {
            return mapOf(
                Bin2dStat.P_BINS to listOf(Bin2dStat.DEF_BINS, Bin2dStat.DEF_BINS),
                Bin2dStat.P_BINWIDTH to listOf(Bin2dStat.DEF_BINWIDTH, Bin2dStat.DEF_BINWIDTH),
                Bin2dStat.P_DROP to Bin2dStat.DEF_DROP
            )
        }

        private fun createContourDefaults(): Map<String, Any> {
            return mapOf(
                "bins" to ContourStatBuilder.DEF_BIN_COUNT
            )
        }

        private fun createContourfDefaults(): Map<String, Any> {
            return mapOf(
                "bins" to ContourfStatBuilder.DEF_BIN_COUNT
            )
        }

        private fun createBoxplotDefaults(): Map<String, Any> {
            return mapOf(
                P_COEF to BoxplotStat.DEF_WHISKER_IQR_RATIO,
                P_VARWIDTH to BoxplotStat.DEF_COMPUTE_WIDTH
            )
        }

        private fun createDensityDefaults(): Map<String, Any> {
            return mapOf(
                "n" to DensityStat.DEF_N,
                "kernel" to DensityStat.DEF_KERNEL,
                "bw" to DensityStat.DEF_BW,
                "adjust" to DensityStat.DEF_ADJUST
            )
        }

        private fun createDensity2dDefaults(): Map<String, Any> {
            return mapOf(
                "n" to AbstractDensity2dStat.DEF_N,
                "kernel" to AbstractDensity2dStat.DEF_KERNEL,
                "bw" to AbstractDensity2dStat.DEF_BW,
                "adjust" to AbstractDensity2dStat.DEF_ADJUST,
                "contour" to AbstractDensity2dStat.DEF_CONTOUR,
                "bins" to AbstractDensity2dStat.DEF_BIN_COUNT
            )
        }
    }
}
