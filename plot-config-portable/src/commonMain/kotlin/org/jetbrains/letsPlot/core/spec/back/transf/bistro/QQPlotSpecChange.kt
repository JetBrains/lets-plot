/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf.bistro

import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.transf.SpecChange
import org.jetbrains.letsPlot.core.spec.transf.SpecChangeContext
import org.jetbrains.letsPlot.core.spec.transf.SpecSelector
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.qq.Option.QQ
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.qq.QQPlotOptionsBuilder
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.util.OptionsUtil

class QQPlotSpecChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val qqPlotSpec = buildQQPlotSpec(spec)

        // Set layers
        spec[Option.Plot.LAYERS] = qqPlotSpec.get(Option.Plot.LAYERS) ?: error("Missing layers in Q-Q plot")

        // Merge scales
        val qqScales = qqPlotSpec.getList(Option.Plot.SCALES) ?: error("Missing scales in Q-Q plot")
        val plotScales = spec.getList(Option.Plot.SCALES) ?: emptyList<Any>()
        spec[Option.Plot.SCALES] = (qqScales + plotScales).toMutableList()

        spec.remove("bistro")
    }

    private fun buildQQPlotSpec(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        val bistroSpec = plotSpec.getMap(Option.Plot.BISTRO) ?: error("'bistro' not found in PlotSpec")
        val qqPlotOptionsBuilder = QQPlotOptionsBuilder(
            sample = bistroSpec.getString(QQ.SAMPLE),
            x = bistroSpec.getString(QQ.X),
            y = bistroSpec.getString(QQ.Y),
            distribution = bistroSpec.getString(QQ.DISTRIBUTION) ?: QQPlotOptionsBuilder.DEF_DISTRIBUTION,
            distributionParameters = bistroSpec.getList(QQ.DISTRIBUTION_PARAMETERS),
            quantiles = bistroSpec.getList(QQ.QUANTILES),
            group = bistroSpec.getString(QQ.GROUP),
            showLegend = bistroSpec.getBool(QQ.SHOW_LEGEND),
            color = bistroSpec.getString(QQ.POINT_COLOR),
            fill = bistroSpec.getString(QQ.POINT_FILL),
            alpha = bistroSpec.getDouble(QQ.POINT_ALPHA) ?: QQPlotOptionsBuilder.DEF_POINT_ALPHA,
            size = bistroSpec.getDouble(QQ.POINT_SIZE) ?: QQPlotOptionsBuilder.DEF_POINT_SIZE,
            shape = bistroSpec.read(QQ.POINT_SHAPE),
            lineColor = bistroSpec.getString(QQ.LINE_COLOR),
            lineSize = bistroSpec.getDouble(QQ.LINE_SIZE) ?: QQPlotOptionsBuilder.DEF_LINE_SIZE,
            lineType = bistroSpec.read(QQ.LINE_TYPE)
        )
        val qqPlotOptions = qqPlotOptionsBuilder.build()
        return OptionsUtil.toSpec(qqPlotOptions)
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return spec.getString(Option.Plot.BISTRO, Option.Meta.NAME) == QQ.NAME
    }

    companion object {
        fun specSelector(): SpecSelector {
            return SpecSelector.root()
        }
    }
}