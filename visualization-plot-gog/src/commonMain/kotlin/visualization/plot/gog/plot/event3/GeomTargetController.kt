package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.config.event3.GeomTargetInteraction.TooltipAesSpec
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetCollector
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.gog.core.event3.HitShape

class GeomTargetController(private val myGeomKind: GeomKind, private val myLookupSpec: GeomTargetLocator.LookupSpec, private val myTooltipAesSpec: TooltipAesSpec) : GeomTargetCollector, GeomTargetLocator {
    private val myTargets = ArrayList<GeomTargetPrototype>()
    private var myGeomTargetLocator: GeomTargetLocator? = null

    override fun addPoint(index: Int, point: DoubleVector, radius: Double, tooltipParams: GeomTargetCollector.TooltipParams) {
        if (point == null) {
            return
        }

        addTarget(GeomTargetPrototype(HitShape.point(point, radius), { index }, tooltipParams))
    }

    override fun addRectangle(index: Int, rectangle: DoubleRectangle, tooltipParams: GeomTargetCollector.TooltipParams) {
        if (rectangle == null) {
            return
        }

        addTarget(GeomTargetPrototype(HitShape.rect(rectangle), { index }, tooltipParams))
    }

    override fun addPath(points: List<DoubleVector>, localToGlobalIndex: (Int) -> Int, tooltipParams: GeomTargetCollector.TooltipParams, closePath: Boolean) {
        if (!isValidPoints(points)) {
            return
        }

        addTarget(GeomTargetPrototype(HitShape.path(points, closePath), localToGlobalIndex, tooltipParams))
    }

    override fun findTargets(coord: DoubleVector): GeomTargetLocator.LocatedTargets? {
        if (myGeomTargetLocator == null) {
            myGeomTargetLocator = GeomTargetLocatorImpl(myGeomKind, myLookupSpec, myTooltipAesSpec, myTargets)
        }
        return myGeomTargetLocator!!.findTargets(coord)
    }

    private fun addTarget(targetPrototype: GeomTargetPrototype) {
        myTargets.add(targetPrototype)
        myGeomTargetLocator = null
    }

    private fun isValidPoints(points: List<DoubleVector>): Boolean {
        if (points.isEmpty()) {
            return false
        }

        for (point in points) {
            if (point == null) {
                return false
            }
        }

        return true
    }
}
