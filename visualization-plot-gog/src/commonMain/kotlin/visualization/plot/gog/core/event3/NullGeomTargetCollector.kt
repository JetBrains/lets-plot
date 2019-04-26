package jetbrains.datalore.visualization.plot.gog.core.event3

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

class NullGeomTargetCollector : GeomTargetCollector {
    override fun addPoint(index: Int, point: DoubleVector, radius: Double, tooltipParams: GeomTargetCollector.TooltipParams) {
    }

    override fun addRectangle(index: Int, rectangle: DoubleRectangle, tooltipParams: GeomTargetCollector.TooltipParams) {
    }

    override fun addPath(points: List<DoubleVector>, localToGlobalIndex: Function<Int, Int>, tooltipParams: GeomTargetCollector.TooltipParams, closePath: Boolean) {
    }
}
