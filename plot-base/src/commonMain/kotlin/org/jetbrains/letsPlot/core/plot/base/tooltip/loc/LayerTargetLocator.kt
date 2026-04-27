/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.util.ClosestPointChecker
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.tooltip.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace.X
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace.Y
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy.HOVER
import org.jetbrains.letsPlot.core.plot.base.tooltip.HitShape.Kind.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.CURSOR
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.LayerTargetLocator.Collector.CollectingStrategy
import kotlin.math.max

internal class LayerTargetLocator(
    private val geomKind: GeomKind,
    private val lookupSpec: GeomTargetLocator.LookupSpec,
    private val contextualMapping: ContextualMapping,
    targetPrototypes: List<TargetPrototype>
) : GeomTargetLocator {

    private val myTargets = ArrayList<Target>()

    private val mySimpleGeometry = setOf(GeomKind.RECT, GeomKind.POLYGON)

    // Geoms that never have more than one target per X coord
    private val unstackableGeoms = setOf(GeomKind.LINE_RANGE, GeomKind.POINT_RANGE, GeomKind.ERROR_BAR, GeomKind.BOX_PLOT)

    private val myCollectingStrategy: CollectingStrategy =
        when {
            geomKind in unstackableGeoms && lookupSpec.lookupStrategy == LookupStrategy.NEAREST -> CollectingStrategy.REPLACE
            geomKind in mySimpleGeometry -> CollectingStrategy.REPLACE // fix overlapping tooltips under cursor

            lookupSpec.lookupSpace.isUnivariate() && lookupSpec.lookupStrategy === LookupStrategy.NEAREST -> {
                // collect all with a minimum distance from cursor
                CollectingStrategy.APPEND_IF_EQUAL
            }

            lookupSpec.lookupSpace.isUnivariate() -> CollectingStrategy.APPEND

            lookupSpec.lookupStrategy == HOVER && (lookupSpec.lookupSpace in setOf(X, Y)) -> CollectingStrategy.APPEND

            lookupSpec.lookupStrategy === LookupStrategy.NONE || lookupSpec.lookupSpace === LookupSpace.NONE -> {
                CollectingStrategy.IGNORE
            }

            else -> CollectingStrategy.REPLACE
        }

    init {
        fun toProjection(prototype: TargetPrototype): TargetProjection {
            return when (prototype.hitShape.kind) {
                POINT -> PointTargetProjection(prototype.hitShape.point, lookupSpec.lookupSpace)
                RECT -> RectTargetProjection(prototype.hitShape.rect, lookupSpec.lookupSpace)
                POLYGON -> PolygonTargetProjection(prototype.hitShape.points, lookupSpec.lookupSpace)
                PATH -> PathTargetProjection(prototype.hitShape.points, prototype.indexMapper, lookupSpec.lookupSpace)
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
        targets: MutableList<LookupResult>,
        hitShapeKind: HitShape.Kind
    ) {
        if (collector.size() == 0) {
            return
        }

        targets.add(
            LookupResult(
                collector.collection(),
                // Distance can be negative when lookup space is X or Y
                // In this case use 0.0 as a distance - we have a direct hit.
                max(0.0, collector.closestPointChecker.distance),
                ownerDistance(collector.cursor, collector.collection(), lookupSpec.lookupSpace),
                lookupSpec,
                geomKind,
                contextualMapping,
                hitShapeKind
            )
        )
    }

    override fun search(coord: DoubleVector): LookupResult? {
        if (myTargets.isEmpty()) {
            return null
        }

        // Should always replace because of polygon with holes - only top should have tooltip.
        val polygonCollector = Collector<GeomTarget>(coord, CollectingStrategy.REPLACE, lookupSpec.lookupSpace)
        val rectCollector = Collector<GeomTarget>(coord, myCollectingStrategy, lookupSpec.lookupSpace)
        val pointCollector = Collector<GeomTarget>(coord, myCollectingStrategy, lookupSpec.lookupSpace)
        val pathCollector = Collector<GeomTarget>(coord, myCollectingStrategy, lookupSpec.lookupSpace)

        for (target in myTargets) {
            when (target.prototype.hitShape.kind) {
                RECT -> processRect(coord, target, rectCollector)
                POINT -> processPoint(coord, target, pointCollector)
                PATH -> processPath(coord, target, pathCollector)
                POLYGON -> processPolygon(coord, target, polygonCollector)
            }
        }

        val lookupResults = ArrayList<LookupResult>()

        addLookupResults(pathCollector, lookupResults, PATH)
        addLookupResults(rectCollector, lookupResults, RECT)
        addLookupResults(pointCollector, lookupResults, POINT)
        addLookupResults(polygonCollector, lookupResults, POLYGON)

        return getClosestTarget(lookupResults)
    }

    private fun getClosestTarget(lookupResults: List<LookupResult>): LookupResult? {
        if (lookupResults.isEmpty()) {
            return null
        }

        var closestTargets: LookupResult = lookupResults[0]
        require(closestTargets.lookupDistance >= 0)

        for (lookupResult in lookupResults) {
            if (lookupResult.lookupDistance < closestTargets.lookupDistance) {
                closestTargets = lookupResult
            }
        }
        return closestTargets
    }

    private fun processRect(coord: DoubleVector, target: Target, resultCollector: Collector<GeomTarget>) {
        if (target.rectProjection.check(coord, lookupSpec.lookupStrategy, resultCollector.closestPointChecker)) {

            val rect = target.prototype.hitShape.rect
            val yOffset = when {
                target.prototype.tooltipPlacement == CURSOR -> rect.height / 2.0
                lookupSpec.lookupSpace == Y -> rect.height / 2.0
                else -> 0.0
            }
            val hintOffset = when (lookupSpec.lookupSpace) {
                X -> rect.width / 2
                Y -> rect.height / 2
                else -> 0.0
            }

            val hitCoord = target.prototype.tooltipAnchor
                ?: rect.origin.add(DoubleVector(rect.width / 2, yOffset))
            resultCollector.collect(
                target.prototype.createGeomTarget(
                    hitCoord,
                    getKeyForSingleObjectGeometry(target.prototype),
                    objectRadius = hintOffset
                )
            )
        }
    }

    private fun processPolygon(coord: DoubleVector, target: Target, resultCollector: Collector<GeomTarget>) {
        if (target.polygonProjection.check(coord, lookupSpec.lookupStrategy, resultCollector.closestPointChecker)) {

            resultCollector.collect(
                target.prototype.createGeomTarget(
                    coord,
                    getKeyForSingleObjectGeometry(target.prototype)
                )
            )
        }
    }

    private fun processPoint(coord: DoubleVector, target: Target, resultCollector: Collector<GeomTarget>) {
        if (target.pointProjection.check(coord, lookupSpec.lookupStrategy, resultCollector.closestPointChecker)) {

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
        val lookupResult = target.pathProjection.check(
            cursorCoord = coord,
            lookupStrategy = lookupSpec.lookupStrategy,
            closestPointChecker = resultCollector.closestPointChecker
        )
        if (lookupResult != null) {
            val hitPoint = lookupResult.first
            val hitCoord = lookupResult.second ?: hitPoint.originalCoord

            resultCollector.collect(
                target.prototype.createGeomTarget(
                    hitCoord,
                    hitPoint.index
                )
            )
        }
    }

    private fun getKeyForSingleObjectGeometry(prototype: TargetPrototype): Int {
        return prototype.indexMapper(0)
    }

    private fun ownerDistance(
        cursor: DoubleVector,
        targets: List<GeomTarget>,
        lookupSpace: LookupSpace
    ): Double {
        return targets.minOf { target ->
            val offset = target.tooltipHint.coord.subtract(cursor)
            when (lookupSpace) {
                X -> kotlin.math.abs(offset.x)
                Y -> kotlin.math.abs(offset.y)
                LookupSpace.XY -> offset.length()
                LookupSpace.NONE -> error("Distance calculation is not supported for NONE lookup space")
            }
        }
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
        val cursor: DoubleVector,
        private val myStrategy: CollectingStrategy,
        lookupSpace: LookupSpace
    ) {
        private val result = ArrayList<T>()

        val closestPointChecker: ClosestPointChecker = when (lookupSpace) {
            X -> ClosestPointChecker(cursor)
            Y -> ClosestPointChecker(cursor)
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
