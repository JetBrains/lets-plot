/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector
import jetbrains.datalore.plot.server.config.transform.bistro.qq.Option.QQ
import jetbrains.datalore.plot.server.config.transform.bistro.qq.QQPlotOptionsBuilder
import jetbrains.datalore.plot.server.config.transform.bistro.util.OptionsUtil

class QQPlotSpecChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val qqPlotSpec = buildQQPlotSpec(spec)
        spec[Option.Plot.LAYERS] = qqPlotSpec.get(Option.Plot.LAYERS) ?: error("Missing layers in Q-Q plot")
        spec.remove("bistro")
    }

    private fun buildQQPlotSpec(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        val bistroSpec = plotSpec.getMap(Option.Plot.BISTRO) ?: error("'bistro' not found in PlotSpec")
        val qqPlotOptionsBuilder = QQPlotOptionsBuilder(
            sample = bistroSpec.getString(QQ.SAMPLE),
            x = bistroSpec.getString(QQ.X),
            y = bistroSpec.getString(QQ.Y)
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