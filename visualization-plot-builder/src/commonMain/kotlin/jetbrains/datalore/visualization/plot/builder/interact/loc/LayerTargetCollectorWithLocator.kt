package jetbrains.datalore.visualization.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.interact.ContextualMapping
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.interact.HitShape
import jetbrains.datalore.visualization.plot.builder.interact.loc.GeomTargetPrototype
import jetbrains.datalore.visualization.plot.builder.interact.loc.LayerTargetLocator

class LayerTargetCollectorWithLocator(
        private val geomKind: GeomKind,
        private val lookupSpec: GeomTargetLocator.LookupSpec,
        private val contextualMapping: ContextualMapping) : GeomTargetCollector, GeomTargetLocator {

    private val myTargets = ArrayList<GeomTargetPrototype>()
    private var myLocator: GeomTargetLocator? = null

    override fun addPoint(index: Int, point: DoubleVector, radius: Double, tooltipParams: GeomTargetCollector.TooltipParams) {
        addTarget(GeomTargetPrototype(HitShape.point(point, radius), { index }, tooltipParams))
    }

    override fun addRectangle(index: Int, rectangle: DoubleRectangle, tooltipParams: GeomTargetCollector.TooltipParams) {
        addTarget(GeomTargetPrototype(HitShape.rect(rectangle), { index }, tooltipParams))
    }

    override fun addPath(points: List<DoubleVector>, localToGlobalIndex: (Int) -> Int, tooltipParams: GeomTargetCollector.TooltipParams, closePath: Boolean) {
        addTarget(GeomTargetPrototype(HitShape.path(points, closePath), localToGlobalIndex, tooltipParams))
    }

    private fun addTarget(targetPrototype: GeomTargetPrototype) {
        myTargets.add(targetPrototype)
        myLocator = null
    }

    override fun findTargets(coord: DoubleVector): GeomTargetLocator.LocatedTargets? {
        if (myLocator == null) {
            myLocator = LayerTargetLocator(geomKind, lookupSpec, contextualMapping, myTargets)
        }
        return myLocator!!.findTargets(coord)
    }
}
