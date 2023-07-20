/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf.bistro.qq

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.conversion.LineTypeOptionConverter
import org.jetbrains.letsPlot.core.spec.conversion.ShapeOptionConverter
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.qq.Option.QQ
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.util.LayerOptions
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.util.plot
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.util.scale

class QQPlotOptionsBuilder(
    private val sample: String? = null,
    private val x: String? = null,
    private val y: String? = null,
    private val distribution: String? = DEF_DISTRIBUTION,
    private val distributionParameters: List<*>? = null,
    private val quantiles: List<*>? = null,
    private val group: String? = null,
    private val showLegend: Boolean? = null,
    private val color: String? = null,
    private val fill: String? = null,
    private val alpha: Double? = DEF_POINT_ALPHA,
    private val size: Double? = DEF_POINT_SIZE,
    private val shape: Any? = null,
    private val lineColor: String? = null,
    private val lineSize: Double? = DEF_LINE_SIZE,
    private val lineType: Any? = null
) {
    fun build(): PlotOptions {
        val mappings = getMappings(sample, x, y, group)
        val scaleNames = getScaleNames(sample, x, y, distribution)
        return plot {
            layerOptions = listOf(
                LayerOptions().apply {
                    geom = if (this@QQPlotOptionsBuilder.sample != null) GeomKind.Q_Q else GeomKind.Q_Q_2
                    setParameter(Option.PlotBase.MAPPING, mappings)
                    setParameter(QQ.DISTRIBUTION, distribution)
                    setParameter(QQ.DISTRIBUTION_PARAMETERS, distributionParameters)
                    setParameter(QQ.QUANTILES, quantiles)
                    showLegend = this@QQPlotOptionsBuilder.showLegend
                    color = this@QQPlotOptionsBuilder.color
                    fill = this@QQPlotOptionsBuilder.fill
                    alpha = this@QQPlotOptionsBuilder.alpha
                    size = this@QQPlotOptionsBuilder.size
                    shape = ShapeOptionConverter().apply(this@QQPlotOptionsBuilder.shape)
                },
                LayerOptions().apply {
                    geom = if (this@QQPlotOptionsBuilder.sample != null) GeomKind.Q_Q_LINE else GeomKind.Q_Q_2_LINE
                    setParameter(Option.PlotBase.MAPPING, mappings)
                    setParameter(QQ.DISTRIBUTION, distribution)
                    setParameter(QQ.DISTRIBUTION_PARAMETERS, distributionParameters)
                    setParameter(QQ.QUANTILES, quantiles)
                    showLegend = this@QQPlotOptionsBuilder.showLegend
                    color = this@QQPlotOptionsBuilder.lineColor ?:
                        if (this@QQPlotOptionsBuilder.group == null) DEF_LINE_COLOR else null
                    size = this@QQPlotOptionsBuilder.lineSize
                    linetype = LineTypeOptionConverter().apply(this@QQPlotOptionsBuilder.lineType)
                }
            )
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
        }
    }

    private fun getMappings(
        sample: String?,
        x: String?,
        y: String?,
        group: String?
    ): HashMap<String, String> {
        val mappings: HashMap<String, String> = if (sample != null) {
            require(x == null)
                { "Parameter x shouldn't be specified when parameter sample is." }
            require(y == null)
                { "Parameter y shouldn't be specified when parameter sample is." }
            hashMapOf(
                Pair(QQ.SAMPLE, sample)
            )
        } else {
            require(x != null)
                { "Parameter x should be specified when parameter sample isn't." }
            require(y != null)
                { "Parameter y should be specified when parameter sample isn't." }
            hashMapOf(
                Pair(QQ.X, x),
                Pair(QQ.Y, y)
            )
        }
        if (group != null) {
            mappings[QQ.GROUP] = group
            mappings[QQ.POINT_COLOR] = group
            mappings[QQ.POINT_FILL] = group
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

    companion object {
        const val DEF_DISTRIBUTION: String = "norm"
        const val DEF_POINT_ALPHA: Double = 0.5
        const val DEF_POINT_SIZE: Double = 3.0
        val DEF_LINE_COLOR: String = Color.RED.toHexColor()
        const val DEF_LINE_SIZE: Double = 0.75
    }
}