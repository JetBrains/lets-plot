/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.qq

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.server.config.transform.bistro.util.*

class QQPlotOptionsBuilder(
    private val sample: String?,
    private val x: String?,
    private val y: String?,
) {
    fun build(): PlotOptions {
        val aes = getMappings(sample, x, y)
        val layers = listOf(
            LayerOptions().apply {
                geom = if (Aes.SAMPLE in aes.keys) GeomKind.Q_Q else GeomKind.Q_Q_2
                mappings = aes
            },
            LayerOptions().apply {
                geom = if (Aes.SAMPLE in aes.keys) GeomKind.Q_Q_LINE else GeomKind.Q_Q_2_LINE
                mappings = aes
            }
        )
        return plot {
            layerOptions = layers
        }
    }

    private fun getMappings(sample: String?, x: String?, y: String?): Map<Aes<*>, String> {
        return if (sample != null) {
            require(x == null)
                { "Parameter x shouldn't be specified when parameter sample is." }
            require(y == null)
                { "Parameter y shouldn't be specified when parameter sample is." }
            mapOf(
                Aes.SAMPLE to sample
            )
        } else {
            require(x != null)
                { "Parameter x should be specified when parameter sample isn't." }
            require(y != null)
                { "Parameter y should be specified when parameter sample isn't." }
            mapOf(
                Aes.X to x,
                Aes.Y to y
            )
        }
    }
}