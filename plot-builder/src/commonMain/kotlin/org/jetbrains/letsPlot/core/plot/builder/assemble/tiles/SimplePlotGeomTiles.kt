/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.tiles

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.assemble.PositionalScalesUtil
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider

class SimplePlotGeomTiles constructor(
    private val geomLayers: List<GeomLayer>,
    scalesBeforeFacets: Map<Aes<*>, Scale>,
    override val mappersNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
    coordProvider: CoordProvider,
    containsLiveMap: Boolean
) : PlotGeomTilesBase(
    scalesBeforeFacets,
    coordProvider,
    containsLiveMap
) {
    private val scaleXProto: Scale = scalesBeforeFacets.getValue(Aes.X)
    private val scaleYProto: Scale = scalesBeforeFacets.getValue(Aes.Y)

    override val isSingleTile: Boolean = true

    override fun layersByTile(): List<List<GeomLayer>> {
        return listOf(geomLayers)
    }

    override fun scaleXByTile(): List<Scale> {
        return listOf(scaleXProto)
    }

    override fun scaleYByTile(): List<Scale> {
        return listOf(scaleYProto)
    }

    override fun overallXYContinuousDomains(): Pair<DoubleSpan?, DoubleSpan?> {
        check(!containsLiveMap) { "Not applicable to LiveMap." }
        val xyTransformedDomains = PositionalScalesUtil.computePlotXYTransformedDomains(
            coreLayersByTile(),
            listOf(scaleXProto),   // Just one tile
            listOf(scaleYProto),
            PlotFacets.UNDEFINED,
            coordProvider
        )
        val pair = xyTransformedDomains[0].let {
            val xTransform = scaleXProto.transform
            val yTransform = scaleYProto.transform
            Pair(
                if (xTransform is ContinuousTransform) xTransform.applyInverse(it.first) else null,
                if (yTransform is ContinuousTransform) yTransform.applyInverse(it.second) else null,
            )
        }
        return pair
    }
}