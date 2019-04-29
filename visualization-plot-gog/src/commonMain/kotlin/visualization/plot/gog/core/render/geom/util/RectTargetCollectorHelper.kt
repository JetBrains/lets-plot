package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector.TooltipParams
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics

class RectTargetCollectorHelper(private val myRectanglesHelper: RectanglesHelper,
                                private val myRectangleByDataPoint: (DataPointAesthetics) -> DoubleRectangle?,
                                private val myFillByDataPoint: (DataPointAesthetics) -> Color) {

    fun collectTo(targetCollector: GeomTargetCollector) {
        myRectanglesHelper.iterateRectangleGeometry(myRectangleByDataPoint, { p, rectangle -> targetCollector.addRectangle(p.index(), rectangle, tooltipParams(p)) })
    }

    private fun tooltipParams(p: DataPointAesthetics): TooltipParams {
        val params = params()
        params.setColor(myFillByDataPoint(p))
        return params
    }
}
