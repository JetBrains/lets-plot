/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.asMapOfMaps
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getMap
import kotlin.collections.get

internal open class MarkConverterBase protected constructor(
    protected val vegaSpec: Map<*, *>,
    protected val plotOptions: PlotOptions

){
    protected val markVegaSpec = Util.readMark(vegaSpec[Option.MARK]!!).second // can't get into BarMarkTransform without MARK
    protected val encodingVegaSpec = (vegaSpec.getMap(Option.Encodings.ENCODING) ?: emptyMap()).asMapOfMaps()
    protected val dataVegaSpec = vegaSpec.getMap(Option.DATA) ?: emptyMap()
}