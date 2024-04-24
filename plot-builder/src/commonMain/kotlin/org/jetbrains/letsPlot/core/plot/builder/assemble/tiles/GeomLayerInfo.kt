/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.tiles

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer

class GeomLayerInfo(
    private val geomLayer: GeomLayer
) {
    val isLegendDisabled = geomLayer.isLegendDisabled
    val aestheticsDefaults = geomLayer.aestheticsDefaults
    val legendKeyElementFactory = geomLayer.legendKeyElementFactory
    val legendItem = geomLayer.legendItem
    val colorByAes: Aes<Color> = geomLayer.colorByAes
    val fillByAes: Aes<Color> = geomLayer.fillByAes
    val isMarginal: Boolean = geomLayer.isMarginal

    fun renderedAes(): List<Aes<*>> = geomLayer.renderedAes()
    fun hasBinding(aes: Aes<*>): Boolean = geomLayer.hasBinding(aes)
    fun hasConstant(aes: Aes<*>): Boolean = geomLayer.hasConstant(aes)
    fun <T> getConstant(aes: Aes<T>): T = geomLayer.getConstant(aes)
}
