/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.reducer
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.singlePointAppender
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.plot.base.render.svg.LinePath

class LinePathConstructor(
    private val myTargetCollector: GeomTargetCollector,
    private val myDataPoints: Iterable<DataPointAesthetics>,
    private val myLinesHelper: LinesHelper,
    private val myClosePath: Boolean
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
                params().setColor(
                    HintColorUtil.fromFill(
                        multiPointData.aes
                    )
                )
            )
        } else {
            myTargetCollector.addPath(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                params().setColor(
                    HintColorUtil.fromColor(
                        multiPointData.aes
                    )
                )
            )
        }
    }

    private fun createMultiPointDataByGroup(): List<MultiPointData> {
        return MultiPointDataConstructor.createMultiPointDataByGroup(
            myDataPoints,
            singlePointAppender { p -> myLinesHelper.toClient(GeomUtil.TO_LOCATION_X_Y(p)!!, p) },
            reducer(DROP_POINT_DISTANCE, myClosePath)
        )
    }

    companion object {
        private const val DROP_POINT_DISTANCE = 0.999
    }
}
