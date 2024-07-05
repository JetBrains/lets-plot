/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.GeomLayerInfo
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider

interface PlotGeomTiles {
    val isSingleTile: Boolean
    val containsLiveMap: Boolean
    val xyContinuousTransforms: Pair<Transform?, Transform?> // same cont. transforms are shared by all tiles
    val scalesBeforeFacets: Map<Aes<*>, Scale>
    val mappersNP: Map<Aes<*>, ScaleMapper<*>>   // all non-positional mappers
    val defaultFormatters: Map<Pair<Aes<*>?, String?>, (Any) -> String>
    val coordProvider: CoordProvider

    fun layersByTile(): List<List<GeomLayer>>
    fun coreLayersByTile(): List<List<GeomLayer>>
    fun marginalLayersByTile(): List<List<GeomLayer>>

    fun scaleXByTile(): List<Scale>
    fun scaleYByTile(): List<Scale>

    fun overallTransformedDomain(aes: Aes<*>): DoubleSpan
    fun overallXYContinuousDomains(): Pair<DoubleSpan?, DoubleSpan?>

    //    fun coreLayerInfos(): List<GeomLayerInfo>
    fun layerInfos(): List<GeomLayerInfo>
}