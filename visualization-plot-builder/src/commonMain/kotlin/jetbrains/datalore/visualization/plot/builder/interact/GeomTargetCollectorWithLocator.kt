package jetbrains.datalore.visualization.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.interact.ContextualMapping
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.interact.HitShape

class GeomTargetCollectorWithLocator(
        private val myGeomKind: GeomKind,
        private val myLookupSpec: GeomTargetLocator.LookupSpec,
        private val myContextualMapping: ContextualMapping) : GeomTargetCollector, GeomTargetLocator {

    private val myTargets = ArrayList<GeomTargetPrototype>()
    private var myGeomTargetLocator: GeomTargetLocator? = null

    override fun addPoint(index: Int, point: DoubleVector, radius: Double, tooltipParams: GeomTargetCollector.TooltipParams) {
        addTarget(GeomTargetPrototype(HitShape.point(point, radius), { index }, tooltipParams))
    }

    override fun addRectangle(index: Int, rectangle: DoubleRectangle, tooltipParams: GeomTargetCollector.TooltipParams) {
        addTarget(GeomTargetPrototype(HitShape.rect(rectangle), { index }, tooltipParams))
    }

    override fun addPath(points: List<DoubleVector>, localToGlobalIndex: (Int) -> Int, tooltipParams: GeomTargetCollector.TooltipParams, closePath: Boolean) {
        addTarget(GeomTargetPrototype(HitShape.path(points, closePath), localToGlobalIndex, tooltipParams))
    }

    override fun findTargets(coord: DoubleVector): GeomTargetLocator.LocatedTargets? {
        if (myGeomTargetLocator == null) {
            myGeomTargetLocator = GeomTargetLocatorImpl(myGeomKind, myLookupSpec, myContextualMapping, myTargets)
        }
        return myGeomTargetLocator!!.findTargets(coord)
    }

    private fun addTarget(targetPrototype: GeomTargetPrototype) {
        myTargets.add(targetPrototype)
        myGeomTargetLocator = null
    }
}
