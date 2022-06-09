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
    private val sample: String?,
    private val x: String?,
    private val y: String?,
    private val distribution: String?,
    private val distributionParameters: List<Double>?,
    private val quantiles: List<Double>?,
    private val group: String?,
    private val showLegend: Boolean?,
    private val color: String?,
    private val fill: String?,
    private val alpha: Double?,
    private val size: Double?,
    private val shape: Int?,
    private val lineColor: String?,
    private val lineSize: Double?,
    private val lineType: Int?
) {
    fun build(): PlotOptions {
        val aesthetics = getMappings(sample, x, y, group)
        val layers = listOf(
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
        return plot {
            layerOptions = layers
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

    companion object {
        const val DEF_POINT_ALPHA: Double = 0.5
        const val DEF_POINT_SIZE: Double = 3.0
        val DEF_LINE_COLOR: String = Color.RED.toHexColor()
        const val DEF_LINE_SIZE: Double = 0.75
    }
}