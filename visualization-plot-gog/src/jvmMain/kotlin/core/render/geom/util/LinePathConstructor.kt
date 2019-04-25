package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.svg.LinePath

import java.util.ArrayList

import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.MultiPointDataConstructor.reducer
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.MultiPointDataConstructor.singlePointAppender

class LinePathConstructor(
        private val myTargetCollector: GeomTargetCollector,
        private val myDataPoints: Iterable<DataPointAesthetics>,
        private val myLinesHelper: LinesHelper,
        private val myClosePath: Boolean) {

    fun construct(): List<LinePath> {
        val linePaths = ArrayList<LinePath>()

        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
                myDataPoints,
                singlePointAppender { p -> myLinesHelper.toClient(GeomUtil.TO_LOCATION_X_Y(p)!!, p) },
                reducer(DROP_POINT_DISTANCE, myClosePath)
        )

        for (multiPointData in multiPointDataList) {
            val pointToColor = if (myClosePath) HintColorUtil::fromFill else HintColorUtil::fromColor
            myTargetCollector.addPath(
                    multiPointData.points,
                    multiPointData.localToGlobalIndex,
                    params().setColor(pointToColor(multiPointData.aes)),
                    myClosePath
            )
            linePaths.addAll(myLinesHelper.createPaths(multiPointData.aes, multiPointData.points, myClosePath))
        }

        return linePaths
    }

    companion object {
        private val DROP_POINT_DISTANCE = 0.999
    }
}
