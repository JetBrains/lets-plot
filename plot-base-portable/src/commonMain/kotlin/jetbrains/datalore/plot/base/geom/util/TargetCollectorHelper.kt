/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.algorithms.reduce
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.VERTICAL_TOOLTIP

class TargetCollectorHelper(
    geomKind: GeomKind,
    private val ctx: GeomContext
) {
    private val colorMarkerMapper = HintColorUtil.createColorMarkerMapper(geomKind, ctx)
    private val targetCollector: GeomTargetCollector = ctx.targetCollector

    private fun createColorMarker(pd: PathData): GeomTargetCollector.TooltipParams {
        return GeomTargetCollector.TooltipParams(markerColors = colorMarkerMapper(pd.aes))
    }

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
                createColorMarker(pathData),
                hintKind
            )
        }
    }

    fun addPolygons(pathDataList: List<PathData>) {
        pathDataList.forEach { pathData ->
            targetCollector.addPolygon(
                pathData.coordinates,
                pathData.aes.index(),
                createColorMarker(pathData),
                TipLayoutHint.Kind.CURSOR_TOOLTIP
            )
        }
    }
}