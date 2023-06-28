/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.algorithms.reduce
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

    fun addPaths(pathDataList: List<PathData>) {
        val hintKind = VERTICAL_TOOLTIP.takeIf { ctx.flipped } ?: HORIZONTAL_TOOLTIP

        pathDataList.map {
            PathData(
                reduce(it.points, 0.5) { p1, p2 -> p1.coord.subtract(p2.coord).length() }
            )
        }.forEach { pathData ->
            targetCollector.addPath(
                pathData.coordinates,
                { i -> pathData.aesthetics[i].index() },
                TooltipParams(markerColors = colorMarkerMapper(pathData.aes)),
                hintKind
            )
        }
    }

    fun addVariadicPaths(paths: List<List<PathData>>) {
        val hintKind = VERTICAL_TOOLTIP.takeIf { ctx.flipped } ?: HORIZONTAL_TOOLTIP

        for (path in paths) {
            val pathAesIndex = mutableMapOf<Int, DataPointAesthetics>()
            for (subPath in path) {
                for (p in subPath.aesthetics) {
                    // Do not use aes from p - if size and colour are mapped the colour of the tooltip marker
                    // and line colour will be different
                    pathAesIndex[p.index()] = subPath.aes
                }
            }

            val flattenPathData = path
                .flatMap(PathData::points) // make a single path to make tooltips in HOVER mode work correctly
                .let { reduce(it, 0.5) { p1, p2 -> p1.coord.subtract(p2.coord).length() } }
                .let(::PathData)

            targetCollector.addPath(
                flattenPathData.coordinates,
                { i -> flattenPathData.aesthetics[i].index() },
                TooltipParams(markerColorsFactory = { i: Int -> colorMarkerMapper(pathAesIndex[i]!!) }),
                hintKind
            )
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
}