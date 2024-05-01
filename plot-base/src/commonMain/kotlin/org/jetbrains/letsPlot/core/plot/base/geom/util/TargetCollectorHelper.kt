/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.reduce
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector.TooltipParams
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP

class TargetCollectorHelper(
    geomKind: GeomKind,
    private val ctx: GeomContext
) {
    private val colorMarkerMapper = HintColorUtil.createColorMarkerMapper(geomKind, ctx)
    private val targetCollector: GeomTargetCollector = ctx.targetCollector

    fun addPaths(paths: Map<Int, PathData>) {
        for (path in paths) {
            val simplifiedPath = reduce(path.value) ?: continue
            addPath(simplifiedPath, TooltipParams(markerColors = colorMarkerMapper(simplifiedPath.aes)))
        }
    }

    fun addVariadicPaths(paths: Map<Int, List<PathData>>) {
        for (subPaths in paths.values) {
            val simplifiedSubPaths = subPaths.mapNotNull(::reduce)

            // build a subpaths aes index so later we would fetch a proper tooltip marker.
            // This is needed as we flatten the path for target detector (otherwise tooltips won't show up as we expect)
            val subPathAesIndex = mutableMapOf<Int, DataPointAesthetics>()
            for (subPath in simplifiedSubPaths) {
                for (p in subPath.aesthetics) {
                    // Do not use aes from p - if size and colour are mapped the colour of the tooltip marker
                    // and line colour will be different
                    subPathAesIndex[p.index()] = subPath.aes
                }
            }

            // dump to a single path to show proper tooltips in a HOVER mode
            val flattenPath = PathData.create(simplifiedSubPaths.flatMap(PathData::points)) ?: continue
            addPath(flattenPath, TooltipParams(markerColorsFactory = { i -> colorMarkerMapper(subPathAesIndex[i]!!) }))
        }
    }

    fun addPolygons(polygonData: PolygonData) {
        targetCollector.addPolygon(
            polygonData.flattenCoordinates,
            polygonData.aes.index(),
            TooltipParams(markerColors = colorMarkerMapper(polygonData.aes)),
            TipLayoutHint.Kind.CURSOR_TOOLTIP
        )
    }

    private fun addPath(path: PathData, tooltipParams: TooltipParams) {
        targetCollector.addPath(
            points = path.coordinates,
            localToGlobalIndex = { i -> path.aesthetics[i].index() },
            tooltipParams = tooltipParams,
            tooltipKind = VERTICAL_TOOLTIP.takeIf { ctx.flipped } ?: HORIZONTAL_TOOLTIP
        )
    }

    private fun reduce(path: PathData): PathData? {
        return PathData.create(reduce(path.points, 0.5) { p1, p2 -> p1.coord.subtract(p2.coord).length() })
    }

    fun addLine(lineString: List<DoubleVector>, p: DataPointAesthetics) {
        targetCollector.addPath(
            points = lineString,
            localToGlobalIndex = { p.index() },
            tooltipParams = TooltipParams(markerColors = colorMarkerMapper(p)),
            tooltipKind = HORIZONTAL_TOOLTIP
        )
    }
}