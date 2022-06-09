/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.qq

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.server.config.transform.bistro.qq.Option.QQ
import jetbrains.datalore.plot.server.config.transform.bistro.util.*

class QQPlotOptionsBuilder(
    private val sample: String?,
    private val x: String?,
    private val y: String?,
    private val distribution: String? = null,
    private val distributionParameters: List<Double>? = null,
    private val quantiles: List<Double>? = null,
    private val group: String? = null,
    private val showLegend: Boolean? = null
) {
    fun build(): PlotOptions {
        val aesthetics = getMappings(sample, x, y, group)
        val layers = listOf(
            LayerOptions().apply {
                geom = if (Aes.SAMPLE in aesthetics.keys) GeomKind.Q_Q else GeomKind.Q_Q_2
                mappings = aesthetics
                setParameter(QQ.DISTRIBUTION, distribution)
                setParameter(QQ.DISTRIBUTION_PARAMETERS, distributionParameters)
                setParameter(QQ.QUANTILES, quantiles)
                showLegend = this@QQPlotOptionsBuilder.showLegend
            },
            LayerOptions().apply {
                geom = if (Aes.SAMPLE in aesthetics.keys) GeomKind.Q_Q_LINE else GeomKind.Q_Q_2_LINE
                mappings = aesthetics
                setParameter(QQ.DISTRIBUTION, distribution)
                setParameter(QQ.DISTRIBUTION_PARAMETERS, distributionParameters)
                setParameter(QQ.QUANTILES, quantiles)
                showLegend = this@QQPlotOptionsBuilder.showLegend
            }
        )
        return plot {
            layerOptions = layers
        }
    }

    private fun getMappings(
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
}