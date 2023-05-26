/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.svg.LinePath

class LinePathConstructor(
    private val myTargetCollector: GeomTargetCollector,
    private val myDataPoints: Iterable<DataPointAesthetics>,
    private val myLinesHelper: LinesHelper,
    private val myClosePath: Boolean,
    private val myColorsByDataPoint: (DataPointAesthetics) -> List<Color>,
    private val myFlipped: Boolean
) {
    fun construct(): List<LinePath> {
        val linePaths = ArrayList<LinePath>()
        val multiPointDataList = createMultiPointDataByGroup()
        for (multiPointData in multiPointDataList) {
            linePaths.addAll(myLinesHelper.createPaths(multiPointData.aes, multiPointData.points, myClosePath))
            buildHint(multiPointData)
        }
        return linePaths
    }

    private fun buildHint(multiPointData: MultiPointData) {
        if (myClosePath) {
            myTargetCollector.addPolygon(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                GeomTargetCollector.TooltipParams(
                    markerColors = myColorsByDataPoint(multiPointData.aes)
                )
            )
        } else {
            myTargetCollector.addPath(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                GeomTargetCollector.TooltipParams(
                    markerColors = myColorsByDataPoint(multiPointData.aes)
                ),
                if (myFlipped) {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )
        }
    }

    private fun createMultiPointDataByGroup(): List<MultiPointData> {
        return myLinesHelper.createMultiPointDataByGroup(
            myDataPoints,
            GeomUtil.TO_LOCATION_X_Y,
            myClosePath
        )
    }
}
