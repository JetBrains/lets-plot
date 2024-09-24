/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.qq

import org.jetbrains.letsPlot.commons.intern.indicesOf
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.stat.QQStat
import org.jetbrains.letsPlot.core.plot.base.stat.QQStatUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Layer.Marginal.SIDE_BOTTOM
import org.jetbrains.letsPlot.core.spec.Option.Layer.Marginal.SIDE_LEFT
import org.jetbrains.letsPlot.core.spec.Option.Layer.Marginal.SIDE_RIGHT
import org.jetbrains.letsPlot.core.spec.Option.Layer.Marginal.SIDE_TOP
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.DataUtil.standardiseData
import org.jetbrains.letsPlot.core.spec.conversion.LineTypeOptionConverter
import org.jetbrains.letsPlot.core.spec.conversion.ShapeOptionConverter
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.qq.Option.QQ
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.*

class QQPlotOptionsBuilder(
    data: Map<*, *>,
    private val sample: String? = null,
    private val x: String? = null,
    private val y: String? = null,
    private val distribution: String? = DEF_DISTRIBUTION,
    private val distributionParameters: List<Double>? = null,
    private val quantiles: List<Double>? = null,
    private val group: String? = null,
    private val showLegend: Boolean? = null,
    private val marginal: String = DEF_MARGINAL,
    private val color: String? = null,
    private val fill: String? = null,
    private val alpha: Double? = DEF_POINT_ALPHA,
    private val size: Double? = DEF_POINT_SIZE,
    private val shape: Any? = null,
    private val lineColor: String? = null,
    private val lineSize: Double? = DEF_LINE_SIZE,
    private val lineType: Any? = null
) {
    private val statData = getStatData(standardiseData(data), distribution, distributionParameters)

    fun build(): PlotOptions {
        val mappings = getMappings(sample, x, y, group)
        val scaleNames = getScaleNames(sample, x, y, distribution)
        return plot {
            layerOptions = listOf(
                LayerOptions().also {
                    it.geom = if (sample != null) GeomKind.Q_Q else GeomKind.Q_Q_2
                    it.data = statData
                    it.mappings = mappings
                    it[DISTRIBUTION_PROP] = distribution
                    it[DISTRIBUTION_PARAMETERS_PROP] = distributionParameters
                    it[QUANTILES_PROP] = quantiles
                    it.showLegend = showLegend
                    it.color = color
                    it.fill = fill
                    it.alpha = alpha
                    it.size = this@QQPlotOptionsBuilder.size
                    it.shape = ShapeOptionConverter().apply(shape)
                },
                LayerOptions().also {
                    it.geom = if (sample != null) GeomKind.Q_Q_LINE else GeomKind.Q_Q_2_LINE
                    it.data = statData
                    it.mappings = mappings
                    it[DISTRIBUTION_PROP] = distribution
                    it[DISTRIBUTION_PARAMETERS_PROP] = distributionParameters
                    it[QUANTILES_PROP] = quantiles
                    it.showLegend = showLegend
                    it.color = lineColor ?:
                        if (group == null) DEF_LINE_COLOR else null
                    it.size = lineSize
                    it.linetype = LineTypeOptionConverter().apply(lineType)
                }
            ) + getMarginalLayers()
            scaleOptions = listOf(
                scale {
                    aes = Aes.X
                    name = scaleNames[Aes.X]
                },
                scale {
                    aes = Aes.Y
                    name = scaleNames[Aes.Y]
                },
                scale {
                    aes = Aes.COLOR
                    isDiscrete = true
                },
                scale {
                    aes = Aes.FILL
                    isDiscrete = true
                },
            )
            themeOptions = theme {
                name = ThemeOption.Name.R_LIGHT
            }
        }
    }

    private fun getMappings(
        sample: String?,
        x: String?,
        y: String?,
        group: String?
    ): HashMap<Aes<*>, String> {
        val mappings: HashMap<Aes<*>, String> = if (sample != null) {
            require(x == null)
                { "Parameter x shouldn't be specified when parameter sample is." }
            require(y == null)
                { "Parameter y shouldn't be specified when parameter sample is." }
            hashMapOf(
                Pair(Aes.SAMPLE, sample)
            )
        } else {
            require(x != null)
                { "Parameter x should be specified when parameter sample isn't." }
            require(y != null)
                { "Parameter y should be specified when parameter sample isn't." }
            hashMapOf(
                Pair(Aes.X, x!!),
                Pair(Aes.Y, y!!)
            )
        }
        if (group != null) {
            mappings[Aes.COLOR] = group
            mappings[Aes.FILL] = group
        }

        return mappings
    }

    private fun getScaleNames(
        sample: String?,
        x: String?,
        y: String?,
        distribution: String?
    ): Map<Aes<*>, String> {
        val distributionName: String = distribution ?: DEF_DISTRIBUTION
        return mapOf(
            Aes.X to if (sample != null) "\"$distributionName\" distribution quantiles" else "$x quantiles",
            Aes.Y to if (sample != null) "$sample quantiles" else "$y quantiles",
        )
    }

    private fun getMarginalLayers(): List<LayerOptions> {
        if (marginal == Option.Layer.NONE) {
            return emptyList()
        }
        return marginal.split(",").map { layerDescription ->
            val params = layerDescription.trim().split(":")
            require(params.size >= 2) { "Invalid format of the marginal parameter" }
            val geomKind = getMarginGeom(params[0].trim())
            val sides = MarginSide.parseSides(params[1].trim())
            val size = params.getOrNull(2)?.trim()?.toDouble()
            sides.map { side ->
                getMarginalLayer(geomKind, side, size)
            }
        }.flatten()
    }

    private fun getMarginGeom(geom: String): GeomKind {
        return when (geom) {
            "dens", "density" -> GeomKind.DENSITY
            "hist", "histogram" -> GeomKind.HISTOGRAM
            "box", "boxplot" -> GeomKind.BOX_PLOT
            else -> throw IllegalArgumentException("Unknown geom $geom")
        }
    }

    private fun getMarginalLayer(geomKind: GeomKind, side: MarginSide, size: Double?): LayerOptions {
        val mappings = getMarginalMappings(sample, x, y, group, side)
        val orientation = if ((geomKind == GeomKind.BOX_PLOT).xor(MarginSide.isVerticallyOriented(side))) "y" else "x"
        return LayerOptions().also {
            it.geom = geomKind
            it.data = statData
            it.mappings = mappings
            it.orientation = orientation
            it.marginal = true
            it.marginSide = side.value
            it.marginSize = size
            it.color = color
            it.fill = fill
            it.alpha = alpha ?: DEF_MARGINAL_ALPHA
        }
    }

    private fun getStatData(
        data: Map<String, List<Any?>>,
        distribution: String?,
        distributionParameters: List<Double>?
    ): Map<String, List<Any?>> {
        if (sample == null) {
            return data
        }
        val originalDf = DataFrameUtil.fromMap(data)
        val sampleVar = DataFrameUtil.findVariableOrFail(originalDf, sample)
        val statDf = originalDf.groupBy(group).map { (_, groupDf) ->
            groupDf.let { df ->
                val indices = df.getNumeric(sampleVar).indicesOf { it != null }
                df.slice(indices)
            }.let { filteredDf ->
                val indices = filteredDf.getNumeric(sampleVar).filterNotNull().sortedIndices(IndexedValue<Double>::value)
                filteredDf.slice(indices)
            }.let { sortedDf ->
                val statSample = sortedDf.getNumeric(sampleVar)
                val t = (1..statSample.size).map { (it - 0.5) / statSample.size }
                val quantileFunction = QQStatUtil.getQuantileFunction(
                    QQStat.Distribution.safeValueOf(distribution ?: DEF_DISTRIBUTION),
                    distributionParameters ?: emptyList()
                )
                sortedDf.setColumn(THEORETICAL_VAR, t.map(quantileFunction))
            }
        }.let { dataframes ->
           DataFrameUtil.concat(dataframes)
        }
        return DataFrameUtil.toMap(statDf)
    }

    private fun getMarginalMappings(
        sample: String?,
        x: String?,
        y: String?,
        group: String?,
        side: MarginSide
    ): HashMap<Aes<*>, String> {
        val mappings: HashMap<Aes<*>, String> = if (MarginSide.isVerticallyOriented(side)) {
            hashMapOf(
                Pair(Aes.Y, sample ?: y!!)
            )
        } else {
            hashMapOf(
                Pair(Aes.X, if (sample != null) THEORETICAL_VAR.name else x!!)
            )
        }
        if (group != null) {
            mappings[Aes.COLOR] = group
            mappings[Aes.FILL] = group
        }

        return mappings
    }

    private fun <T, R : Comparable<R>> Iterable<T>.sortedIndices(selector: (IndexedValue<T>) -> R?) =
        withIndex().sortedWith(compareBy(selector)).map(IndexedValue<T>::index)

    enum class MarginSide(val value: String) {
        LEFT(SIDE_LEFT),
        RIGHT(SIDE_RIGHT),
        TOP(SIDE_TOP),
        BOTTOM(SIDE_BOTTOM);

        companion object {
            fun parseSides(sides: String): Set<MarginSide> {
                return MarginSide.entries.filter { it.value in sides }.toSet()
            }

            fun isVerticallyOriented(side: MarginSide): Boolean {
                return side in listOf(LEFT, RIGHT)
            }
        }
    }

    companion object {
        const val DEF_DISTRIBUTION: String = "norm"
        const val DEF_POINT_ALPHA: Double = 0.5
        const val DEF_POINT_SIZE: Double = 3.0
        val DEF_LINE_COLOR: String = Color.RED.toHexColor()
        const val DEF_LINE_SIZE: Double = 0.75
        const val DEF_MARGINAL: String = "dens:tr"
        const val DEF_MARGINAL_ALPHA = 0.25

        private val DISTRIBUTION_PROP = PropSpec<String?>(QQ.DISTRIBUTION)
        private val DISTRIBUTION_PARAMETERS_PROP = PropSpec<List<Double>?>(QQ.DISTRIBUTION_PARAMETERS)
        private val QUANTILES_PROP = PropSpec<List<Double>?>(QQ.QUANTILES)

        val THEORETICAL_VAR = DataFrame.Variable("..theoretical_bistro..")
    }
}