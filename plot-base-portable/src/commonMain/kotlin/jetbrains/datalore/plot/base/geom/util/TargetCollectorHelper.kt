/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.typedGeometry.algorithms.reduce
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.VERTICAL_TOOLTIP

class TargetCollectorHelper(
    geomKind: GeomKind,
    private val ctx: GeomContext
) {
    private val colorMarkerMapper = HintColorUtil.createColorMarkerMapper(geomKind, ctx)
    private val targetCollector: GeomTargetCollector = ctx.targetCollector

    fun addPaths(paths: List<PathData>) {
        for (path in paths) {
            val simplifiedPath = reduce(path)
            addPath(simplifiedPath, TooltipParams(markerColors = colorMarkerMapper(simplifiedPath.aes)))
        }
    }

    fun addVariadicPaths(paths: List<List<PathData>>) {
        for (subPaths in paths) {
            val simplifiedSubPaths = subPaths.map(::reduce)

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
            val flattenPath = PathData(simplifiedSubPaths.flatMap(PathData::points))
            addPath(flattenPath, TooltipParams(markerColorsFactory = { i -> colorMarkerMapper(subPathAesIndex[i]!!) }))
        }
    }

    fun addPolygons(pathDataList: List<PathData>) {
        pathDataList.forEach { pathData ->
            targetCollector.addPolygon(
                pathData.coordinates,
                pathData.aes.index(),
                TooltipParams(markerColors = colorMarkerMapper(pathData.aes)),
                TipLayoutHint.Kind.CURSOR_TOOLTIP
            )
        }
    }

    private fun addPath(path: PathData, tooltipParams: TooltipParams) {
        targetCollector.addPath(
            points = path.coordinates,
            localToGlobalIndex = { i -> path.aesthetics[i].index() },
            tooltipParams = tooltipParams,
            tooltipKind = VERTICAL_TOOLTIP.takeIf { ctx.flipped } ?: HORIZONTAL_TOOLTIP
        )
    }

    private fun reduce(path: PathData): PathData {
        return PathData(reduce(path.points, 0.5) { p1, p2 -> p1.coord.subtract(p2.coord).length() })
    }
}