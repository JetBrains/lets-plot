/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Ordering
import org.jetbrains.letsPlot.commons.intern.splitBy
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.reduce
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.commons.values.Colors.withOpacity
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.render.svg.LinePath
import kotlin.math.abs

open class LinesHelper(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) :
    GeomHelper(pos, coord, ctx) {

    private var myAlphaEnabled = true
    private var myResamplingEnabled = false

    fun setAlphaEnabled(b: Boolean) {
        this.myAlphaEnabled = b
    }

    fun setResamplingEnabled(resample: Boolean) {
        this.myResamplingEnabled = resample
    }

    fun createLines(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): List<LinePath> {
        // draw line for each group
        val pathDataByGroup = createPathDataByGroup(dataPoints, toLocation)
        return renderPaths(pathDataByGroup.values, closePath = false)
    }

    fun renderPaths(paths: Map<Int, List<PathData>>, closePath: Boolean): List<LinePath> {
        return renderPaths(paths.values.flatten(), closePath)
    }

    fun renderPaths(paths: Collection<PathData>, closePath: Boolean): List<LinePath> {
        return paths.map { path -> renderPaths(path.aes, path.coordinates, closePath) }
    }

    private fun renderPaths(
        aes: DataPointAesthetics,
        points: List<DoubleVector>,
        closePath: Boolean
    ): LinePath {
        val element = when (closePath) {
            true -> LinePath.polygon(splitRings(points).map(::reduce).let(::insertPathSeparators))
            false -> LinePath.line(reduce(points))
        }
        decorate(element, aes, closePath)
        return element
    }

    fun createPathData(
        dataPoints: Iterable<DataPointAesthetics>,
        locationTransform: (DataPointAesthetics) -> DoubleVector? = GeomUtil.TO_LOCATION_X_Y
    ): Map<Int, List<PathData>> {
        return if (myResamplingEnabled) {
            // coords are in data space as they have to be resampled
            val pathData = GeomUtil.createPathGroups(dataPoints, locationTransform, sorted = true)
            val variadicPathData = variadicPathByStyle(pathData)
            val interpolatedData = interpolatePathData(variadicPathData)
            resamplePath(interpolatedData)
        } else {
            // coords are already in client space
            val pathData = createPathDataByGroup(dataPoints, locationTransform)
            val variadicPath = variadicPathByStyle(pathData)
            interpolatePathData(variadicPath)
        }
    }

    // TODO: refactor - inconsistent and implicit usage of the toClient method in a whole LinesHelper class
    private fun resamplePath(groups: Map<Int, List<PathData>>): Map<Int, List<PathData>> {
        return groups.mapValues { (_, path) ->
            path.map { segment ->
                val smoothed = segment.points
                    .windowed(size = 2)
                    .map { (p1, p2) -> p1.aes to resample(p1.coord, p2.coord, 0.5) { toClient(it, p1.aes) } }
                    .flatMap { (aes, points) -> points.map { PathPoint(aes, it) } }
                PathData(smoothed)
            }
        }
    }

    internal fun createPathDataByGroup(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): Map<Int, PathData> {
        return GeomUtil.createPathGroups(dataPoints, toClientLocation(toLocation), sorted = true)
    }


    internal fun createSteps(paths: Map<Int, PathData>, horizontalThenVertical: Boolean): List<LinePath> {
        val linePaths = ArrayList<LinePath>()

        // draw step for each group
        paths.values.forEach { subPath ->
            val points = subPath.coordinates
            if (points.isNotEmpty()) {
                val newPoints = ArrayList<DoubleVector>()
                var prev: DoubleVector? = null
                for (point in points) {
                    if (prev != null) {
                        val x = if (horizontalThenVertical) point.x else prev.x
                        val y = if (horizontalThenVertical) prev.y else point.y
                        newPoints.add(DoubleVector(x, y))
                    }
                    newPoints.add(point)
                    prev = point
                }

                val line = LinePath.line(newPoints)
                decorate(line, subPath.aes, filled = false)
                linePaths.add(line)
            }
        }

        return linePaths
    }

    fun renderBands(
        upperPoints: Map<Int, List<PathData>>,
        lowerPoints: Map<Int, List<PathData>>,
        simplifyBorders: Boolean = false
    ): List<LinePath> {
        return upperPoints.mapNotNull { (group, upperPathData) ->
            val lowerPathData = lowerPoints[group] ?: return@mapNotNull null

            require(upperPathData.size == 1) { "Upper path data should contain only one path" }
            require(lowerPathData.size == 1) { "Lower path data should contain only one path" }

            val upperCoordinates = upperPathData.single().coordinates.let {
                if (simplifyBorders) simplify(it) else it
            }

            val lowerCoordinates = lowerPathData.single().coordinates.let {
                if (simplifyBorders) simplify(it) else it
            }

            val points = upperCoordinates + lowerCoordinates.reversed()

            if (points.isEmpty()) {
                return@mapNotNull null
            }

            val path = LinePath.polygon(points)
            decorateFillingPart(path, upperPathData.single().aes)

            path
        }
    }

    // TODO: replace with PathData version
    fun createBands(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocationUpper: (DataPointAesthetics) -> DoubleVector?,
        toLocationLower: (DataPointAesthetics) -> DoubleVector?,
        simplifyBorders: Boolean = false
    ): MutableList<LinePath> {

        val lines = ArrayList<LinePath>()
        val pointsByGroup = GeomUtil.createGroups(dataPoints)

        // draw line for each group
        for (group in Ordering.natural<Int>().sortedCopy(pointsByGroup.keys)) {
            val groupDataPoints = pointsByGroup[group]!!

            // upper margin points
            val upperPoints = project(groupDataPoints, toLocationUpper)
            val points = ArrayList(if (simplifyBorders) simplify(upperPoints) else upperPoints)

            // lower margin point in reversed order
            val lowerPoints = ArrayList(project(groupDataPoints.reversed(), toLocationLower))
            points.addAll(if (simplifyBorders) simplify(lowerPoints) else lowerPoints)

            if (!points.isEmpty()) {
                val path = LinePath.polygon(points)
                // to retain the side edges of area:
                // decorate(path, groupDataPoints.get(0), true);
                decorateFillingPart(path, groupDataPoints[0])
                lines.add(path)
            }
        }
        return lines
    }

    protected fun project(
        dataPoints: Iterable<DataPointAesthetics>,
        projection: (DataPointAesthetics) -> DoubleVector?
    ): List<DoubleVector> {
        val points = ArrayList<DoubleVector>()
        for (p in dataPoints) {
            val location = projection(p)
            if (location != null) {
                val pp = toClient(location, p)
                if (pp != null) {
                    points.add(pp)
                }
            }
        }
        return points
    }

    private fun simplify(points: List<DoubleVector>): List<DoubleVector> {
        val weightLimit = 0.25 // in px for Douglas–Peucker algorithm
        return PolylineSimplifier.douglasPeucker(points).setWeightLimit(weightLimit).points
    }

    protected fun decorate(
        path: LinePath,
        p: DataPointAesthetics,
        filled: Boolean,
        strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth
    ) {
        val stroke = p.color()
        val strokeAlpha = AestheticsUtil.alpha(stroke!!, p)
        path.color().set(withOpacity(stroke, strokeAlpha))
        if (!AestheticsUtil.ALPHA_CONTROLS_BOTH && (filled || !myAlphaEnabled)) {
            path.color().set(stroke)
        }

        if (filled) {
            decorateFillingPart(path, p)
        }

        val size = strokeScaler(p)
        path.width().set(size)

        val lineType = p.lineType()
        if (!(lineType.isBlank || lineType.isSolid)) {
            path.dashArray().set(lineType.dashArray)
        }
    }

    private fun decorateFillingPart(path: LinePath, p: DataPointAesthetics) {
        val fill = p.fill()
        val fillAlpha = AestheticsUtil.alpha(fill!!, p)
        path.fill().set(withOpacity(fill, fillAlpha))
    }

    fun toClient(pathData: Map<Int, List<PathData>>): Map<Int, List<PathData>> {
        return pathData.mapValues { (_, groupPath) ->
            groupPath.map { segment ->
                // Note that PathPoint have to be recreated with the point aes, not with a segment aes
                val points = segment.points.mapNotNull { p -> toClient(p.coord, segment.aes)?.let { PathPoint(p.aes, coord = it) } }
                PathData(points)
            }
        }
    }

    companion object {
        private fun reduce(points: List<DoubleVector>): List<DoubleVector> {
            return reduce(points, 0.999) { e1, e2 -> maxOf(abs(e1.x - e2.x), abs(e1.y - e2.y)) }
        }

        private fun insertPathSeparators(rings: Iterable<List<DoubleVector>>): List<DoubleVector?> {
            val result = ArrayList<DoubleVector?>()
            for (ring in rings) {
                if (!result.isEmpty()) {
                    result.add(LinePath.END_OF_SUBPATH) // this is polygon's path separator understood by PathLine component
                }

                result.addAll(ring)
            }

            return result
        }

        fun variadicPathByStyle(paths: Map<Int, PathData>): Map<Int, List<PathData>> {
            return paths.mapValues { (_, pathData) ->
                pathData.points
                    .splitBy(
                        compareBy(
                            { it.aes.size() },
                            { it.aes.color()?.red },
                            { it.aes.color()?.green },
                            { it.aes.color()?.blue },
                            { it.aes.color()?.alpha }
                        )
                    )
                    .map(::PathData)
            }
        }

        private fun midPointsPathInterpolator(path: List<PathData>): List<PathData> {
            if (path.size == 1) {
                return path
            }

            val jointPoints = path
                .windowed(size = 2, step = 1)
                .map { (prevSubPath, nextSubPath) ->
                    val prevSubPathEnd = prevSubPath.coordinates.last()
                    val nextSubPathStart = nextSubPath.coordinates.first()
                    val midPoint = lerp(prevSubPathEnd, nextSubPathStart, 0.5)

                    midPoint
                }

            return path.mapIndexed { i, subPath ->
                when (i) {
                    0 -> {
                        val rightJointPoint = subPath.points.last().copy(coord = jointPoints[i])
                        PathData(subPath.points + rightJointPoint)
                    }

                    path.lastIndex -> {
                        val leftJointPoint = subPath.points.first().copy(coord = jointPoints[i - 1])
                        PathData(listOf(leftJointPoint) + subPath.points)
                    }

                    else -> {
                        val leftJointPoint = subPath.points.first().copy(coord = jointPoints[i - 1])
                        val rightJointPoint = subPath.points.last().copy(coord = jointPoints[i])
                        PathData(listOf(leftJointPoint) + subPath.points + rightJointPoint)
                    }
                }
            }
        }

        private fun lerp(p1: DoubleVector, p2: DoubleVector, progress: Double): DoubleVector {
            return p1.add(p2.subtract(p1).mul(progress))
        }

        fun interpolatePathData(variadicPath: Map<Int, List<PathData>>): Map<Int, List<PathData>> {
            return variadicPath.mapValues { (_, pathSegments) -> midPointsPathInterpolator(pathSegments) }
        }
    }
}

data class PathData(
    val points: List<PathPoint>
) {
    val aes: DataPointAesthetics by lazy(points.first()::aes) // decoration aes (only for color, fill, size, stroke)
    val aesthetics by lazy { points.map(PathPoint::aes) }
    val coordinates by lazy { points.map(PathPoint::coord) }
}

data class PathPoint(
    val aes: DataPointAesthetics,
    val coord: DoubleVector
)
