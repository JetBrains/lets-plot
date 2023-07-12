/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.interact.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.interact.HitShape.Kind.*
import org.jetbrains.letsPlot.core.plot.base.interact.TipLayoutHint.Kind.CURSOR_TOOLTIP
import jetbrains.datalore.plot.builder.interact.MathUtil.ClosestPointChecker
import kotlin.math.max

internal class LayerTargetLocator(
    private val geomKind: GeomKind,
    private val lookupSpec: GeomTargetLocator.LookupSpec,
    private val contextualMapping: ContextualMapping,
    targetPrototypes: List<TargetPrototype>
) :
    GeomTargetLocator {

    private val myTargets = ArrayList<Target>()
    private val myTargetDetector: TargetDetector =
        TargetDetector(lookupSpec.lookupSpace, lookupSpec.lookupStrategy)

    private val mySimpleGeometry = setOf(GeomKind.RECT, GeomKind.POLYGON)

    private val myCollectingStrategy: Collector.CollectingStrategy =
        when {
            geomKind in mySimpleGeometry -> {
                // fix overlapping tooltips under cursor
                Collector.CollectingStrategy.REPLACE
            }
            lookupSpec.lookupSpace.isUnivariate() && lookupSpec.lookupStrategy === GeomTargetLocator.LookupStrategy.NEAREST -> {
                // collect all with a minimum distance from cursor
                Collector.CollectingStrategy.APPEND_IF_EQUAL
            }
            lookupSpec.lookupSpace.isUnivariate() -> {
                Collector.CollectingStrategy.APPEND
            }
            lookupSpec.lookupStrategy === GeomTargetLocator.LookupStrategy.HOVER -> {
                Collector.CollectingStrategy.APPEND
            }
            lookupSpec.lookupStrategy === GeomTargetLocator.LookupStrategy.NONE || lookupSpec.lookupSpace === GeomTargetLocator.LookupSpace.NONE -> {
                Collector.CollectingStrategy.IGNORE
            }
            else -> {
                Collector.CollectingStrategy.REPLACE
            }
        }

    init {
        fun toProjection(prototype: TargetPrototype): TargetProjection {
            return when (prototype.hitShape.kind) {
                POINT -> PointTargetProjection.create(
                    prototype.hitShape.point.center,
                    lookupSpec.lookupSpace
                )

                RECT -> RectTargetProjection.create(
                    prototype.hitShape.rect,
                    lookupSpec.lookupSpace
                )

                POLYGON -> PolygonTargetProjection.create(
                    prototype.hitShape.points,
                    lookupSpec.lookupSpace
                )

                PATH -> PathTargetProjection.create(
                    prototype.hitShape.points,
                    prototype.indexMapper,
                    lookupSpec.lookupSpace
                )
            }
        }

        for (prototype in targetPrototypes) {
            myTargets.add(
                Target(
                    toProjection(prototype),
                    prototype
                )
            )
        }
    }

    private fun addLookupResults(
        collector: Collector<GeomTarget>,
        targets: MutableList<GeomTargetLocator.LookupResult>
    ) {
        if (collector.size() == 0) {
            return
        }

        targets.add(
            GeomTargetLocator.LookupResult(
                collector.collection(),
                // Distance can be negative when lookup space is X or Y
                // In this case use 0.0 as a distance - we have a direct hit.
                max(0.0, collector.closestPointChecker.distance),
                geomKind,
                contextualMapping,
                contextualMapping.isCrosshairEnabled
            )
        )
    }

    override fun search(coord: DoubleVector): GeomTargetLocator.LookupResult? {
        if (myTargets.isEmpty()) {
            return null
        }

        val rectCollector = Collector<GeomTarget>(
            coord,
            myCollectingStrategy,
            lookupSpec.lookupSpace
        )
        val pointCollector = Collector<GeomTarget>(
            coord,
            myCollectingStrategy,
            lookupSpec.lookupSpace
        )
        val pathCollector = Collector<GeomTarget>(
            coord,
            myCollectingStrategy,
            lookupSpec.lookupSpace
        )

        // Should always replace because of polygon with holes - only top should have tooltip.
        val polygonCollector = Collector<GeomTarget>(
            coord,
            Collector.CollectingStrategy.REPLACE,
            lookupSpec.lookupSpace
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
        require(closestTargets.distance >= 0)

        for (lookupResult in lookupResults) {
            if (lookupResult.distance < closestTargets.distance) {
                closestTargets = lookupResult
            }
        }
        return closestTargets
    }

    private fun processRect(coord: DoubleVector, target: Target, resultCollector: Collector<GeomTarget>) {
        if (myTargetDetector.checkRect(coord, target.rectProjection, resultCollector.closestPointChecker)) {

            val rect = target.prototype.hitShape.rect
            val yOffset = when {
                target.prototype.tooltipKind == CURSOR_TOOLTIP -> rect.height / 2.0
                lookupSpec.lookupSpace == GeomTargetLocator.LookupSpace.Y -> rect.height / 2.0
                else -> 0.0
            }
            val hintOffset = when (lookupSpec.lookupSpace) {
                GeomTargetLocator.LookupSpace.X -> rect.width / 2
                GeomTargetLocator.LookupSpace.Y -> rect.height / 2
                else -> 0.0
            }

            resultCollector.collect(
                target.prototype.createGeomTarget(
                    rect.origin.add(DoubleVector(rect.width / 2, yOffset)),
                    getKeyForSingleObjectGeometry(target.prototype),
                    objectRadius = hintOffset
                )
            )
        }
    }

    private fun processPolygon(coord: DoubleVector, target: Target, resultCollector: Collector<GeomTarget>) {
        if (myTargetDetector.checkPolygon(coord, target.polygonProjection, resultCollector.closestPointChecker)) {

            resultCollector.collect(
                target.prototype.createGeomTarget(
                    coord,
                    getKeyForSingleObjectGeometry(target.prototype)
                )
            )
        }
    }

    private fun processPoint(coord: DoubleVector, target: Target, resultCollector: Collector<GeomTarget>) {
        if (myTargetDetector.checkPoint(coord, target.pointProjection, resultCollector.closestPointChecker)) {

            resultCollector.collect(
                target.prototype.createGeomTarget(
                    target.prototype.hitShape.point.center,
                    getKeyForSingleObjectGeometry(target.prototype),
                    objectRadius = target.prototype.hitShape.point.radius
                )
            )
        }
    }

    private fun processPath(coord: DoubleVector, target: Target, resultCollector: Collector<GeomTarget>) {
        // When searching single point from all targets (REPLACE) - should search nearest projection between every path target.
        // When searching points for every target (APPEND) - should reset nearest point between every path target.
        val pointChecker = if (myCollectingStrategy == Collector.CollectingStrategy.APPEND)
            ClosestPointChecker(coord)
        else
            resultCollector.closestPointChecker

        val hitPoint = myTargetDetector.checkPath(coord, target.pathProjection, pointChecker)
        if (hitPoint != null) {
            resultCollector.collect(
                target.prototype.createGeomTarget(
                    hitPoint.originalCoord,
                    hitPoint.index
                )
            )
        }
    }

    private fun getKeyForSingleObjectGeometry(prototype: TargetPrototype): Int {
        return prototype.indexMapper(0)
    }

    internal class Target(private val targetProjection: TargetProjection, val prototype: TargetPrototype) {

        val pointProjection: PointTargetProjection
            get() = targetProjection as PointTargetProjection

        val rectProjection: RectTargetProjection
            get() = targetProjection as RectTargetProjection

        val polygonProjection: PolygonTargetProjection
            get() = targetProjection as PolygonTargetProjection

        val pathProjection: PathTargetProjection
            get() = targetProjection as PathTargetProjection
    }

    internal class Collector<T>(
        cursor: DoubleVector,
        private val myStrategy: CollectingStrategy,
        lookupSpace: GeomTargetLocator.LookupSpace
    ) {
        private val result = ArrayList<T>()
        val closestPointChecker: ClosestPointChecker = when (lookupSpace) {
            GeomTargetLocator.LookupSpace.X -> ClosestPointChecker(DoubleVector(cursor.x, 0.0))
            GeomTargetLocator.LookupSpace.Y -> ClosestPointChecker(DoubleVector(0.0, cursor.y))
            else -> ClosestPointChecker(cursor)
        }
        private var myLastAddedDistance: Double = -1.0

        fun collect(data: T) {
            when (myStrategy) {
                CollectingStrategy.APPEND -> add(data)
                CollectingStrategy.REPLACE -> replace(data)
                CollectingStrategy.APPEND_IF_EQUAL -> {
                    if (myLastAddedDistance == closestPointChecker.distance) {
                        add(data)
                    } else {
                        replace(data)
                    }
                }
                CollectingStrategy.IGNORE -> return
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
            myLastAddedDistance = closestPointChecker.distance
        }

        private fun replace(locationData: T) {
            result.clear()
            result.add(locationData)
            myLastAddedDistance = closestPointChecker.distance
        }

        internal enum class CollectingStrategy {
            APPEND,
            REPLACE,
            APPEND_IF_EQUAL,
            IGNORE
        }
    }
}
