/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.qq

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.config.aes.LineTypeOptionConverter
import jetbrains.datalore.plot.config.aes.ShapeOptionConverter
import jetbrains.datalore.plot.server.config.transform.bistro.qq.Option.QQ
import jetbrains.datalore.plot.server.config.transform.bistro.util.*

class QQPlotOptionsBuilder(
    private val sample: String? = null,
    private val x: String? = null,
    private val y: String? = null,
    private val distribution: String? = DEF_DISTRIBUTION,
    private val distributionParameters: List<Double>? = null,
    private val quantiles: List<Double>? = null,
    private val group: String? = null,
    private val showLegend: Boolean? = null,
    private val color: String? = null,
    private val fill: String? = null,
    private val alpha: Double? = DEF_POINT_ALPHA,
    private val size: Double? = DEF_POINT_SIZE,
    private val shape: Int? = null,
    private val lineColor: String? = DEF_LINE_COLOR,
    private val lineSize: Double? = DEF_LINE_SIZE,
    private val lineType: Int? = null
) {
    fun build(): PlotOptions {
        val aesthetics = getMappings(sample, x, y, group)
        val scaleNames = getScaleNames(sample, x, y, distribution)
        return plot {
            layerOptions = listOf(
                LayerOptions().apply {
                    geom = if (this@QQPlotOptionsBuilder.sample != null) GeomKind.Q_Q else GeomKind.Q_Q_2
                    mappings = aesthetics
                    setParameter(QQ.DISTRIBUTION, distribution)
                    setParameter(QQ.DISTRIBUTION_PARAMETERS, distributionParameters)
                    setParameter(QQ.QUANTILES, quantiles)
                    showLegend = this@QQPlotOptionsBuilder.showLegend
                    color = if (group == null) this@QQPlotOptionsBuilder.color else null
                    fill = if (group == null) this@QQPlotOptionsBuilder.fill else null
                    alpha = this@QQPlotOptionsBuilder.alpha
                    size = this@QQPlotOptionsBuilder.size
                    shape = ShapeOptionConverter().apply(this@QQPlotOptionsBuilder.shape)
                },
                LayerOptions().apply {
                    geom = if (this@QQPlotOptionsBuilder.sample != null) GeomKind.Q_Q_LINE else GeomKind.Q_Q_2_LINE
                    mappings = aesthetics
                    setParameter(QQ.DISTRIBUTION, distribution)
                    setParameter(QQ.DISTRIBUTION_PARAMETERS, distributionParameters)
                    setParameter(QQ.QUANTILES, quantiles)
                    showLegend = this@QQPlotOptionsBuilder.showLegend
                    color = if (group == null) this@QQPlotOptionsBuilder.lineColor else null
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
            )
        }
    }

    private inline fun getMappings(
        sample: String?,
        x: String?,
        y: String?,
        group: String?
    ): Map<Aes<*>, String> {
        val mappings: MutableMap<Aes<*>, String> = if (sample != null) {
            require(x == null)
                { "Parameter x shouldn't be specified when parameter sample is." }
            require(y == null)
                { "Parameter y shouldn't be specified when parameter sample is." }
            mutableMapOf(
                Aes.SAMPLE to sample
            )
        } else {
            require(x != null)
                { "Parameter x should be specified when parameter sample isn't." }
            require(y != null)
                { "Parameter y should be specified when parameter sample isn't." }
            mutableMapOf(
                Aes.X to x,
                Aes.Y to y
            )
        }
        if (group != null) {
            mappings[Aes.COLOR] = group
            mappings[Aes.FILL] = group
        }

        return mappings
    }

    private inline fun getScaleNames(
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