/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.splitBy
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.reduce
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.commons.values.Colors.withOpacity
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.render.svg.LinePath
import kotlin.math.abs

open class LinesHelper(
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext
) : GeomHelper(pos, coord, ctx) {

    private var myAlphaEnabled = true
    private var myResamplingEnabled = false

    // Polar coordinate system with discrete X scale.
    fun meetsRadarPlotReq(): Boolean {
        return coord.isPolar && ctx.plotContext?.getScale(Aes.X)?.isContinuous != true
    }

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
        return renderPaths(pathDataByGroup.values, filled = false)
    }

    fun renderPaths(paths: Map<Int, List<PathData>>, filled: Boolean): List<LinePath> {
        return renderPaths(paths.values.flatten(), filled)
    }

    fun renderPaths(paths: Collection<PathData>, filled: Boolean): List<LinePath> {
        return paths.map { path -> renderPaths(path.aes, path.coordinates, filled) }
    }

    fun createPathData(
        dataPoints: Iterable<DataPointAesthetics>,
        locationTransform: (DataPointAesthetics) -> DoubleVector? = GeomUtil.TO_LOCATION_X_Y,
        closePath: Boolean = false,
    ): Map<Int, List<PathData>> {
        @Suppress("NAME_SHADOWING")
        val dataPoints = prepareDataPoints(dataPoints, closePath)
        val domainInterpolatedData = preparePathData(dataPoints, locationTransform)
        return toClient(domainInterpolatedData)
    }

    // TODO: return list of PathData for consistency
    fun createPathDataByGroup(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): Map<Int, PathData> {
        return GeomUtil.createPathGroups(dataPoints, toClientLocation(toLocation), sorted = true)
    }


    fun createSteps(paths: Map<Int, PathData>, horizontalThenVertical: Boolean): List<LinePath> {
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

    // TODO: inline
    fun createBands(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocationUpper: (DataPointAesthetics) -> DoubleVector?,
        toLocationLower: (DataPointAesthetics) -> DoubleVector?,
        simplifyBorders: Boolean = false
    ): List<LinePath> {
        return renderBands(dataPoints, toLocationUpper, toLocationLower, simplifyBorders, closePath = false)
    }

    fun renderBands(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocationUpper: (DataPointAesthetics) -> DoubleVector?,
        toLocationLower: (DataPointAesthetics) -> DoubleVector?,
        simplifyBorders: Boolean,
        closePath: Boolean
    ): List<LinePath> {
        @Suppress("NAME_SHADOWING")
        val dataPoints = prepareDataPoints(dataPoints, closePath)

        val domainUpperPathData = preparePathData(dataPoints, toLocationUpper)
        val domainLowerPathData = preparePathData(dataPoints, toLocationLower)

        val domainBandsPathData = domainUpperPathData.mapValues { (group, upperPathData) ->
            val lowerPathData = domainLowerPathData[group] ?: return@mapValues emptyList<PathData>()

            if (upperPathData.isEmpty() || lowerPathData.isEmpty()) {
                return@mapValues emptyList<PathData>()
            }

            require(upperPathData.size == lowerPathData.size) {
                "Upper and lower path data should contain the same number of paths"
            }

            upperPathData
                .zip(lowerPathData)
                .map { (upperPath, lowerPath) -> PathData(upperPath.points + lowerPath.points.reversed()) }
        }

        val clientBandsPathData = toClient(domainBandsPathData)

        return clientBandsPathData.values.flatten().mapNotNull { pathData ->
            val points = pathData.coordinates

            if (points.isNotEmpty()) {
                val path = LinePath.polygon(if (simplifyBorders) simplify(points) else points)
                decorateFillingPart(path, pathData.aes)
                path
            } else {
                null
            }
        }
    }

    private fun simplify(points: List<DoubleVector>): List<DoubleVector> {
        val weightLimit = 0.25 // in px for Douglas–Peucker algorithm
        return PolylineSimplifier.douglasPeucker(points).setWeightLimit(weightLimit).points
    }

    fun decorate(
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

    private fun renderPaths(
        aes: DataPointAesthetics,
        points: List<DoubleVector>,
        filled: Boolean
    ): LinePath {
        val element = when (filled) {
            true -> LinePath.polygon(splitRings(points).map(::reduce).let(::insertPathSeparators))
            false -> LinePath.line(reduce(points))
        }
        decorate(element, aes, filled)
        return element
    }

    private fun preparePathData(
        dataPoints: Iterable<DataPointAesthetics>,
        locationTransform: (DataPointAesthetics) -> DoubleVector? = GeomUtil.TO_LOCATION_X_Y
    ): Map<Int, List<PathData>> {
        val domainPathData = GeomUtil.createPathGroups(dataPoints, locationTransform, sorted = true)
        return domainPathData.mapValues { (_, pathData) -> listOf(pathData) }
    }

    private fun prepareDataPoints(
        dataPoints: Iterable<DataPointAesthetics>,
        closePath: Boolean
    ) = when {
        closePath -> dataPoints + dataPoints.first()
        else -> dataPoints
    }

    private fun toClient(domainPathData: Map<Int, List<PathData>>): Map<Int, List<PathData>> {
        return when (myResamplingEnabled) {
            true -> {
                val domainVariadicPathData = domainPathData.mapValues { (_, groupPath) -> groupPath.flatMap(::splitByStyle) }
                val domainInterpolatedPathData = interpolatePathData(domainVariadicPathData)
                resamplePathData(domainInterpolatedPathData)
            }
            false -> {
                val clientPathData = domainPathData.mapValues { (_, groupPath) ->
                    groupPath.map { segment ->
                        // Note that PathPoint have to be recreated with the point aes, not with a segment aes
                        val points = segment.points.mapNotNull { p ->
                            toClient(p.coord, segment.aes)
                                ?.let { PathPoint(p.aes, coord = it) }
                        }
                        PathData(points)
                    }
                }

                val clientVariadicPathData = clientPathData.mapValues { (_, pathData) -> pathData.flatMap(::splitByStyle) }
                interpolatePathData(clientVariadicPathData)
            }
        }
    }

    // TODO: refactor - inconsistent and implicit usage of the toClient method in a whole LinesHelper class
    private fun resamplePathData(pathData: Map<Int, List<PathData>>): Map<Int, List<PathData>> {
        return pathData.mapValues { (_, path) ->
            path.map { segment ->
                val smoothed = segment.points
                    .windowed(size = 2)
                    .map { (p1, p2) -> p1.aes to resample(p1.coord, p2.coord, 0.5) { toClient(it, p1.aes) } }
                    .flatMap { (aes, points) -> points.map { PathPoint(aes, it) } }
                PathData(smoothed)
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

        fun splitByStyle(pathData: PathData): List<PathData> {
            return pathData.points
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
