package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.HitShape.Kind.*
import jetbrains.datalore.plot.builder.interact.MathUtil.ClosestPointChecker
import kotlin.math.max

internal class LayerTargetLocator(
    private val geomKind: GeomKind,
    lookupSpec: GeomTargetLocator.LookupSpec,
    private val contextualMapping: ContextualMapping,
    targetPrototypes: List<jetbrains.datalore.plot.builder.interact.loc.TargetPrototype>) :
    GeomTargetLocator {

    private val myTargets = ArrayList<jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Target>()
    private val myTargetDetector: jetbrains.datalore.plot.builder.interact.loc.TargetDetector =
        jetbrains.datalore.plot.builder.interact.loc.TargetDetector(lookupSpec.lookupSpace, lookupSpec.lookupStrategy)

    private val myCollectingStrategy: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy =
            if (lookupSpec.lookupSpace === GeomTargetLocator.LookupSpace.X) {
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.APPEND
            } else if (lookupSpec.lookupStrategy === GeomTargetLocator.LookupStrategy.HOVER) {
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.APPEND
            } else if (lookupSpec.lookupStrategy === GeomTargetLocator.LookupStrategy.NONE ||
                    lookupSpec.lookupSpace === GeomTargetLocator.LookupSpace.NONE) {
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.IGNORE
            } else {
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.REPLACE
            }

    init {
        fun toProjection(prototype: jetbrains.datalore.plot.builder.interact.loc.TargetPrototype): jetbrains.datalore.plot.builder.interact.loc.TargetProjection {
            return when (prototype.hitShape.kind) {
                POINT -> jetbrains.datalore.plot.builder.interact.loc.PointTargetProjection.Companion.create(
                    prototype.hitShape.point.center,
                    lookupSpec.lookupSpace
                )

                RECT -> jetbrains.datalore.plot.builder.interact.loc.RectTargetProjection.Companion.create(
                    prototype.hitShape.rect,
                    lookupSpec.lookupSpace
                )

                POLYGON -> jetbrains.datalore.plot.builder.interact.loc.PolygonTargetProjection.Companion.create(
                    prototype.hitShape.points,
                    lookupSpec.lookupSpace
                )

                PATH -> jetbrains.datalore.plot.builder.interact.loc.PathTargetProjection.Companion.create(
                    prototype.hitShape.points,
                    prototype.indexMapper,
                    lookupSpec.lookupSpace
                )
            }
        }

        for (prototype in targetPrototypes) {
            myTargets.add(
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Target(
                    toProjection(prototype),
                    prototype
                )
            )
        }
    }

    private fun addLookupResults(collector: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>, targets: MutableList<GeomTargetLocator.LookupResult>) {
        if (collector.size() == 0) {
            return
        }

        targets.add(
                GeomTargetLocator.LookupResult(
                        collector.collection(),
                        // Distance can be negative when lookup space is X
                        // In this case use 0.0 as a distance - we have a direct hit.
                        max(0.0, collector.closestPointChecker.distance),
                        geomKind,
                        contextualMapping
                )
        )
    }

    override fun search(coord: DoubleVector): GeomTargetLocator.LookupResult? {
        if (myTargets.isEmpty()) {
            return null
        }

        val rectCollector = jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>(
            coord,
            myCollectingStrategy
        )
        val pointCollector = jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>(
            coord,
            myCollectingStrategy
        )
        val pathCollector = jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>(
            coord,
            myCollectingStrategy
        )

        // Should always replace because of polygon with holes - only top should have tooltip.
        val polygonCollector = jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>(
            coord,
            jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.REPLACE
        )

        for (target in myTargets) {
            when (target.prototype.hitShape.kind) {
                RECT -> processRect(coord, target, rectCollector)

                POINT -> processPoint(coord, target, pointCollector)

                PATH -> processPath(coord, target, pathCollector)

                POLYGON -> processPolygon(coord, target, polygonCollector)
            }
        }

        val lookupResults = ArrayList<GeomTargetLocator.LookupResult>()

        addLookupResults(pathCollector, lookupResults)
        addLookupResults(rectCollector, lookupResults)
        addLookupResults(pointCollector, lookupResults)
        addLookupResults(polygonCollector, lookupResults)

        return getClosestTarget(lookupResults)
    }

    private fun getClosestTarget(lookupResults: List<GeomTargetLocator.LookupResult>): GeomTargetLocator.LookupResult? {
        if (lookupResults.isEmpty()) {
            return null
        }

        var closestTargets: GeomTargetLocator.LookupResult = lookupResults[0]
        checkArgument(closestTargets.distance >= 0)

        for (lookupResult in lookupResults) {
            if (lookupResult.distance < closestTargets.distance) {
                closestTargets = lookupResult
            }
        }
        return closestTargets
    }

    private fun processRect(coord: DoubleVector, target: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Target, resultCollector: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>) {
        if (myTargetDetector.checkRect(coord, target.rectProjection, resultCollector.closestPointChecker)) {

            val rect = target.prototype.hitShape.rect
            resultCollector.collect(
                    target.prototype.crateGeomTarget(
                            rect.origin.add(DoubleVector(rect.width / 2, 0.0)),
                            getKeyForSingleObjectGeometry(target.prototype)
                    )
            )
        }
    }

    private fun processPolygon(coord: DoubleVector, target: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Target, resultCollector: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>) {
        if (myTargetDetector.checkPolygon(coord, target.polygonProjection, resultCollector.closestPointChecker)) {

            resultCollector.collect(
                    target.prototype.crateGeomTarget(
                            coord,
                            getKeyForSingleObjectGeometry(target.prototype)
                    )
            )
        }
    }

    private fun processPoint(coord: DoubleVector, target: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Target, resultCollector: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>) {
        if (myTargetDetector.checkPoint(coord, target.pointProjection, resultCollector.closestPointChecker)) {

            resultCollector.collect(
                    target.prototype.crateGeomTarget(
                            target.prototype.hitShape.point.center,
                            getKeyForSingleObjectGeometry(target.prototype)
                    )
            )
        }
    }

    private fun processPath(coord: DoubleVector, target: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Target, resultCollector: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector<GeomTarget>) {
        // When searching single point from all targets (REPLACE) - should search nearest projection between every path target.
        // When searching points for every target (APPEND) - should reset nearest point between every path target.
        val pointChecker = if (myCollectingStrategy == jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.APPEND)
            ClosestPointChecker(coord)
        else
            resultCollector.closestPointChecker

        val hitPoint = myTargetDetector.checkPath(coord, target.pathProjection, pointChecker)
        if (hitPoint != null) {
            resultCollector.collect(
                    target.prototype.crateGeomTarget(
                            hitPoint.originalCoord,
                            hitPoint.index
                    )
            )
        }
    }

    private fun getKeyForSingleObjectGeometry(prototype: jetbrains.datalore.plot.builder.interact.loc.TargetPrototype): Int {
        return prototype.indexMapper(0)
    }

    internal class Target(private val targetProjection: jetbrains.datalore.plot.builder.interact.loc.TargetProjection, val prototype: jetbrains.datalore.plot.builder.interact.loc.TargetPrototype) {

        val pointProjection: jetbrains.datalore.plot.builder.interact.loc.PointTargetProjection
            get() = targetProjection as jetbrains.datalore.plot.builder.interact.loc.PointTargetProjection

        val rectProjection: jetbrains.datalore.plot.builder.interact.loc.RectTargetProjection
            get() = targetProjection as jetbrains.datalore.plot.builder.interact.loc.RectTargetProjection

        val polygonProjection: jetbrains.datalore.plot.builder.interact.loc.PolygonTargetProjection
            get() = targetProjection as jetbrains.datalore.plot.builder.interact.loc.PolygonTargetProjection

        val pathProjection: jetbrains.datalore.plot.builder.interact.loc.PathTargetProjection
            get() = targetProjection as jetbrains.datalore.plot.builder.interact.loc.PathTargetProjection
    }

    internal class Collector<T>(cursor: DoubleVector, private val myStrategy: jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy) {
        private val result = ArrayList<T>()
        val closestPointChecker: ClosestPointChecker = ClosestPointChecker(cursor)

        fun collect(data: T) {
            when (myStrategy) {
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.APPEND -> add(data)
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.REPLACE -> replace(data)
                jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator.Collector.CollectingStrategy.IGNORE -> return
            }
        }

        fun collection(): List<T> {
            return result
        }

        fun size(): Int {
            return result.size
        }

        private fun add(data: T) {
            result.add(data)
        }

        private fun replace(locationData: T) {
            result.clear()
            result.add(locationData)
        }

        internal enum class CollectingStrategy {
            APPEND,
            REPLACE,
            IGNORE
        }
    }
}
