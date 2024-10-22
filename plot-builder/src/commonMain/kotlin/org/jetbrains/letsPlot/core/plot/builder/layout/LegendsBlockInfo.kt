/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

// ToDo: internal
/*internal*/ class LegendsBlockInfo(
    val boxWithLocationList: List<LegendBoxesLayout.BoxWithLocation>
) {
    fun size(): DoubleVector {
        var bounds: DoubleRectangle? = null
        for (boxWithLocation in boxWithLocationList) {
            bounds = bounds?.union(boxWithLocation.bounds()) ?: boxWithLocation.bounds()
        }
        return bounds?.dimension ?: DoubleVector.ZERO
    }

    fun moveAll(delta: DoubleVector): LegendsBlockInfo {
        val newList = boxWithLocationList.map {
            LegendBoxesLayout.BoxWithLocation(it.legendBox, it.location.add(delta))
        }
        return LegendsBlockInfo(newList)
    }
}
