/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.OptionsUtil.toSpec
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings
import org.jetbrains.letsPlot.core.spec.vegalite.Util.readMark
import org.jetbrains.letsPlot.core.spec.vegalite.Util.transformData

internal object Transform {
    fun transform(spec: MutableMap<String, Any>): MutableMap<String, Any> {
        val plotOptions = PlotOptions()

        when (VegaConfig.getPlotKind(spec)) {
            VegaPlotKind.SINGLE_LAYER -> processLayerSpec(spec, plotOptions)
            VegaPlotKind.MULTI_LAYER -> {
                spec.getMap(Option.DATA)?.let { plotOptions.data = transformData(it) }
                spec.getMap(Encodings.ENCODING)?.let { plotOptions.mappings = Util.transformMappings(it) }
                spec.getMaps(Option.LAYER)!!.forEach { layerSpec -> processLayerSpec(layerSpec, plotOptions) }
            }
            VegaPlotKind.FACETED -> error("Not implemented - faceted plot")
        }

        return toSpec(plotOptions)
    }

    private fun processLayerSpec(layerSpec: Map<*, *>, plotOptions: PlotOptions) {
        val (mark, _) = readMark(layerSpec[Option.MARK] ?: error("Mark is not specified"))
        when (mark) {
            Option.Mark.Types.BAR -> BarMarkTransform.process(layerSpec, plotOptions)
            Option.Mark.Types.LINE -> LineMarkTransform.process(layerSpec, plotOptions)
            Option.Mark.Types.POINT -> PointMarkTransform.process(layerSpec, plotOptions)
            Option.Mark.Types.AREA -> AreaMarkTransform.process(layerSpec, plotOptions)
            Option.Mark.Types.BOXPLOT -> PointMarkTransform.process(layerSpec, plotOptions)
            else -> error("Unsupported mark type: $mark")
        }
    }
}
