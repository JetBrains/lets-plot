/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.splitBy
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.PIXEL_PRECISION
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.isClosed
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.isRingTrimmed
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.trimRing
import org.jetbrains.letsPlot.commons.values.Colors.withOpacity
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier.Companion.DOUGLAS_PEUCKER_PIXEL_THRESHOLD
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier.Companion.douglasPeucker
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.createPathGroups
import org.jetbrains.letsPlot.core.plot.base.render.svg.LinePath
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

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

    // TODO: filled parameter is always false
    fun renderPaths(paths: Map<Int, List<PathData>>, filled: Boolean): List<LinePath> {
        return renderPaths(paths.values.flatten(), filled)
    }

    // TODO: filled parameter is always false
    private fun renderPaths(paths: Collection<PathData>, filled: Boolean): List<LinePath> {
        return paths.map { path ->
            val visualPath = when (myResamplingEnabled) {
                true -> douglasPeucker(path.coordinates, DOUGLAS_PEUCKER_PIXEL_THRESHOLD)
                false -> path.coordinates
            }

            val element = when (filled) {
                true -> LinePath.polygon(visualPath)
                false -> LinePath.line(visualPath)
            }

            decorate(element, path.aes, filled)
            element
        }
    }

    fun createPathData(
        dataPoints: Iterable<DataPointAesthetics>,
        locationTransform: (DataPointAesthetics) -> DoubleVector? = GeomUtil.TO_LOCATION_X_Y,
        closePath: Boolean = false,
    ): Map<Int, List<PathData>> {
        val domainData = preparePathData(dataPoints, locationTransform, closePath)
        return toClient(domainData)
    }

    fun createPolygon(
        dataPoints: Iterable<DataPointAesthetics>,
        locationTransform: (DataPointAesthetics) -> DoubleVector? = GeomUtil.TO_LOCATION_X_Y,
    ): List<Pair<SvgNode, PolygonData>> {
        val domainPathData = createPathGroups(dataPoints, locationTransform, sorted = true, closePath = false).values

        // split in domain space! after resampling coordinates may repeat and splitRings will return wrong results
        val domainPolygonData = domainPathData
            .map { splitRings(it.points, PathPoint.LOC_EQ) }
            .mapNotNull { PolygonData.create(it) }

        val clientPolygonData = domainPolygonData.mapNotNull { polygon ->
            polygon.rings
                .map { resample(it) }
                .let { PolygonData.create(it) }
        }

        val svg = clientPolygonData.map { polygon ->
            val element = polygon.coordinates
                .map { douglasPeucker(it, DOUGLAS_PEUCKER_PIXEL_THRESHOLD) }
                .let(::insertPathSeparators)
                .let { LinePath.polygon(it) }

            decorate(element, polygon.aes, filled = true)
            element.rootGroup
        }

        return svg.zip(clientPolygonData)
    }

    private fun resample(linestring: List<PathPoint>): List<PathPoint> {
        val smoothed = linestring.windowed(size = 2)
            .map { (p1, p2) -> p1.aes to resample(p1.coord, p2.coord, PIXEL_PRECISION) { p -> toClient(p, p1.aes) } }
            .flatMap { (aes, coords) -> coords.map { PathPoint(aes, it) } }

        // smoothed path doesn't contain PathPoint for the last point - append it
        val endPoint = linestring.last()
        return when (val clientCoord = toClient(endPoint.coord, endPoint.aes)) {
            null -> smoothed
            else -> smoothed + PathPoint(endPoint.aes, clientCoord)
        }
    }

    // TODO: return list of PathData for consistency
    fun createPathDataByGroup(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): Map<Int, PathData> {
        return createPathGroups(dataPoints, toClientLocation(toLocation), sorted = true, closePath = false)
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

    // TODO: inline. N.B.: for linear geoms, be careful with the closePath parameter
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
        val domainUpperPathData = preparePathData(dataPoints, toLocationUpper, closePath)
        val domainLowerPathData = preparePathData(dataPoints, toLocationLower, closePath)

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
                .mapNotNull { (upperPath, lowerPath) -> PathData.create(upperPath.points + lowerPath.points.reversed()) }
        }

        val clientBandsPathData = toClient(domainBandsPathData)

        return clientBandsPathData.values.flatten().mapNotNull { pathData ->
            val points = pathData.coordinates

            if (points.isNotEmpty()) {
                val path = LinePath.polygon(
                    when {
                        simplifyBorders -> douglasPeucker(points, DOUGLAS_PEUCKER_PIXEL_THRESHOLD)
                        else -> points
                    }
                )
                decorateFillingPart(path, pathData.aes)
                path
            } else {
                null
            }
        }
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
        path.lineType().set(lineType)
    }

    private fun decorateFillingPart(path: LinePath, p: DataPointAesthetics) {
        val fill = p.fill()
        val fillAlpha = AestheticsUtil.alpha(fill!!, p)
        path.fill().set(withOpacity(fill, fillAlpha))
    }

    private fun preparePathData(
        dataPoints: Iterable<DataPointAesthetics>,
        locationTransform: (DataPointAesthetics) -> DoubleVector?,
        closePath: Boolean
    ): Map<Int, List<PathData>> {
        val domainPathData = createPathGroups(dataPoints, locationTransform, sorted = true, closePath = closePath)
        return domainPathData.mapValues { (_, pathData) -> listOf(pathData) }
    }

    private fun toClient(domainPathData: Map<Int, List<PathData>>): Map<Int, List<PathData>> {
        return when (myResamplingEnabled) {
            true -> {
                domainPathData
                    .mapValues { (_, groupPath) -> groupPath.flatMap(::splitByStyle) }
                    .let { interpolatePathData(it) }
                    .mapValues { (_, paths) -> paths.mapNotNull { PathData.create(resample(it.points)) } }
            }

            false -> {
                val clientPathData = domainPathData.mapValues { (_, groupPath) ->
                    groupPath.mapNotNull { segment ->
                        // Note that PathPoint have to be recreated with the point aes, not with a segment aes
                        val points = segment.points.mapNotNull { p ->
                            toClient(p.coord, p.aes)
                                ?.let { PathPoint(p.aes, coord = it) }
                        }
                        PathData.create(points)
                    }
                }

                val clientVariadicPathData =
                    clientPathData.mapValues { (_, pathData) -> pathData.flatMap(::splitByStyle) }
                interpolatePathData(clientVariadicPathData)
            }
        }
    }

    companion object {
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
                .mapNotNull { PathData.create(it)}
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

            return path.mapIndexedNotNull() { i, subPath ->
                when (i) {
                    0 -> {
                        val rightJointPoint = subPath.points.last().copy(coord = jointPoints[i])
                        PathData.create(subPath.points + rightJointPoint)
                    }

                    path.lastIndex -> {
                        val leftJointPoint = subPath.points.first().copy(coord = jointPoints[i - 1])
                        PathData.create(listOf(leftJointPoint) + subPath.points)
                    }

                    else -> {
                        val leftJointPoint = subPath.points.first().copy(coord = jointPoints[i - 1])
                        val rightJointPoint = subPath.points.last().copy(coord = jointPoints[i])
                        PathData.create(listOf(leftJointPoint) + subPath.points + rightJointPoint)
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

class PathData private constructor(
    val points: List<PathPoint>
) {
    companion object {
        fun create(points: List<PathPoint>): PathData? {
            if (points.isEmpty()) {
                return null
            }

            return PathData(points)
        }
    }

    init {
        require(points.isNotEmpty()) { "PathData should contain at least one point" }
    }

    val aes: DataPointAesthetics by lazy(points.first()::aes) // decoration aes (only for color, fill, size, stroke)
    val aesthetics by lazy { points.map(PathPoint::aes) }
    val coordinates by lazy { points.map(PathPoint::coord) } // may contain duplicates, don't work well for polygon
}

class PolygonData private constructor(
    val rings: List<List<PathPoint>>
) {
    companion object {
        fun create(rings: List<List<PathPoint>>): PolygonData? {
            // Force the invariants
            val processedRings = rings
                .filter { it.isNotEmpty() }
                .map { if (it.isClosed(PathPoint.LOC_EQ)) it else it + it.first() }
                .map { trimRing(it, PathPoint.LOC_EQ) }
                .filter { it.size >= 3 } // 3 points is fine - will draw a line

            if (processedRings.isEmpty()) {
                return null
            }

            return PolygonData(processedRings)
        }
    }

    init {
        require(rings.isNotEmpty()) { "PolygonData should contain at least one ring" }
        require(rings.all { it.size >= 3 }) { "PolygonData ring should contain at least 3 points" }
        require(rings.all { it.first().coord == it.last().coord }) { "PolygonData ring should be closed" }
        require(rings.all { isRingTrimmed(it, PathPoint.LOC_EQ) }) { "PolygonData ring should be trimmed" }
    }

    val aes: DataPointAesthetics by lazy( rings.first().first()::aes ) // decoration aes (only for color, fill, size, stroke)
    val aesthetics by lazy { rings.map { it.map(PathPoint::aes) } }
    val coordinates by lazy { rings.map { it.map(PathPoint::coord) } }
    val flattenCoordinates by lazy { rings.flatten().map(PathPoint::coord) }
}

data class PathPoint(
    val aes: DataPointAesthetics,
    val coord: DoubleVector
) {
    companion object {
        val LOC_EQ = { p1: PathPoint, p2: PathPoint -> p1.coord == p2.coord }
    }
}
