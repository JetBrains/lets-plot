/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.asMapOfMaps
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.plotson.LayerOptions
import org.jetbrains.letsPlot.core.spec.plotson.PlotOptions

internal open class MarkConverterBase protected constructor(
    val vegaSpec: Map<*, *>,
    val plotOptions: PlotOptions

){
    protected val markVegaSpec = Util.readMark(vegaSpec[Option.MARK]!!).second // can't get into BarMarkTransform without MARK
    protected val encodingVegaSpec = (vegaSpec.getMap(Option.Encodings.ENCODING) ?: emptyMap()).asMapOfMaps()
    protected val dataVegaSpec = vegaSpec.getMap(Option.DATA) ?: emptyMap()

    protected fun LayerOptions.initDataAndMappings(
        customChannelMapping: List<Pair<String, Aes<*>>> = emptyList()
    ) {
        data = Util.transformData(dataVegaSpec)
        mappings = Util.transformMappings(encodingVegaSpec, customChannelMapping)
        dataMeta = Util.transformDataMeta(plotOptions.data, data, encodingVegaSpec, customChannelMapping)
    }
}
