/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.splitBy
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.PIXEL_PRECISION
import org.jetbrains.letsPlot.commons.intern.util.VectorAdapter
import org.jetbrains.letsPlot.commons.values.Colors.withOpacity
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier.Companion.DOUGLAS_PEUCKER_PIXEL_THRESHOLD
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier.Companion.douglasPeucker
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.createPathDataFromRectangle
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.createPaths
import org.jetbrains.letsPlot.core.plot.base.render.svg.LinePath
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

open class LinesHelper(
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext
) : GeomHelper(pos, coord, ctx) {

    private var myAlphaEnabled = true
    protected var myResamplingEnabled = false
    protected var myResamplingPrecision = PIXEL_PRECISION

    // Polar coordinate system with discrete X scale.
    fun meetsRadarPlotReq(): Boolean {
        return coord.isPolar && ctx.plotContext.hasScale(Aes.X) && !ctx.plotContext.getScale(Aes.X).isContinuous
    }

    fun setAlphaEnabled(b: Boolean) {
        this.myAlphaEnabled = b
    }

    fun setResamplingEnabled(resample: Boolean) {
        this.myResamplingEnabled = resample
    }

    // for test only
    internal fun setResamplingPrecision(precision: Double) {
       this.myResamplingPrecision = precision
    }

    fun createLines(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): List<LinePath> {
        val paths = createPaths(dataPoints, toLocation)
        return renderPaths(paths, filled = false)
    }

    // TODO: filled parameter is always false
    fun renderPaths(paths: Collection<PathData>, filled: Boolean): List<LinePath> {
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
    ): List<PathData> {
        val domainData = createPaths(dataPoints, locationTransform, sorted = true, closePath = closePath)
        return toClientPaths(domainData)
    }

    fun createPolygon(
        dataPoints: Iterable<DataPointAesthetics>,
        locationTransform: (DataPointAesthetics) -> DoubleVector? = GeomUtil.TO_LOCATION_X_Y,
    ): List<Pair<SvgNode, PolygonData>> {
        val domainPathData = createPaths(dataPoints, locationTransform, sorted = true, closePath = false)

        return createPolygon(domainPathData)
    }

    fun createRectPolygon(
        dataPoints: Iterable<DataPointAesthetics>,
        locationTransform: (DataPointAesthetics) -> List<DoubleVector>?,
    ): List<Pair<SvgNode, PolygonData>> {
        val domainPathData = createPathDataFromRectangle(dataPoints, locationTransform)

        return createPolygon(domainPathData)
    }

    private fun createPolygon(domainPathData: Collection<PathData>): List<Pair<SvgNode, PolygonData>> {
        // split in domain space! after resampling coordinates may repeat and splitRings will return wrong results
        val domainPolygonData = domainPathData
            .map { splitRings(it.points, PathPoint.LOC_EQ) }
            .mapNotNull { PolygonData.create(it) }

        val clientPolygonData = domainPolygonData.mapNotNull { polygon ->
            polygon.rings
                .map { if (myResamplingEnabled) resample(it) else toClient(it) }
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

        fun resampler(aes: DataPointAesthetics): AdaptiveResampler<PathPoint> {
            val adapter = object : VectorAdapter<PathPoint> {
                override fun x(p: PathPoint) = p.coord.x
                override fun y(p: PathPoint) = p.coord.y
                override fun create(x: Double, y: Double) = PathPoint(aes, DoubleVector(x, y))
            }
            return AdaptiveResampler.generic(myResamplingPrecision, adapter) { p: PathPoint ->
                toClient(p.coord, aes)?.let { PathPoint(aes, it) }
            }
        }
        val smoothed = mutableListOf<PathPoint>()

        linestring.windowed(size = 2).forEach { (p1, p2) ->
            // It is important to use the aes of the first element in each pair,
            // since aes may change depending on the group
            // see https://github.com/JetBrains/lets-plot/issues/1375
            val resampler = resampler(p1.aes)

            val resampledPoints = resampler.resample(p1, p2)

            // Return empty list if one of the points could not be transformed to client coordinates
            if (resampledPoints.isEmpty()) {
                return emptyList()
            }

            smoothed.addAll(resampledPoints.subList(0, resampledPoints.size - 1)) // Do not add the last point to avoid duplicates
        }

        // smoothed path doesn't contain PathPoint for the last point - append it
        val endPoint = linestring.last()
        val endCoord = toClient(endPoint.coord, endPoint.aes)
        if (endCoord != null) {
            smoothed.add(PathPoint(endPoint.aes, endCoord))
        }

        return smoothed
    }

    private fun toClient(linestring: List<PathPoint>): List<PathPoint> {
        return linestring.mapNotNull { p ->
            toClient(p.coord, p.aes)?.let { PathPoint(p.aes, it) }
        }
    }

    fun createPaths(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): List<PathData> {
        return createPaths(dataPoints, toClientLocation(toLocation), sorted = true, closePath = false)
    }

    fun createSteps(paths: Collection<PathData>, horizontalThenVertical: Boolean): List<LinePath> {
        val linePaths = ArrayList<LinePath>()

        // draw step for each group
        paths.forEach { subPath ->
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
        val domainUpperPathData = createPaths(dataPoints, toLocationUpper, sorted = true, closePath)
        val domainLowerPathData = createPaths(dataPoints, toLocationLower, sorted = true, closePath)

        if (domainUpperPathData.isEmpty() || domainLowerPathData.isEmpty()) {
            return emptyList()
        }

        require(domainUpperPathData.size == domainLowerPathData.size) {
            "Upper and lower path data should contain the same number of paths"
        }

        val domainBandsPathData = domainUpperPathData
            .zip(domainLowerPathData)
            .mapNotNull { (upperPath, lowerPath) -> PathData.create(upperPath.points + lowerPath.points.reversed()) }

        val clientBandsPathData: List<PathData> = toClientPaths(domainBandsPathData)

        return clientBandsPathData.mapNotNull { pathData ->
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

    private fun toClientPaths(domainPathData: List<PathData>): List<PathData> {
        return when (myResamplingEnabled) {
            true -> {
                domainPathData
                    .map { path -> splitByStyle(path).let(::midPointsPathInterpolator) }
                    .flatMap { paths -> paths.mapNotNull { PathData.create(resample(it.points)) } }
            }

            false -> {
                val clientPathData = domainPathData.mapNotNull { segment ->
                    // Note that PathPoint have to be recreated with the point aes, not with a segment aes
                    val points = segment.points.mapNotNull { p ->
                        toClient(p.coord, p.aes)
                            ?.let { PathPoint(p.aes, coord = it) }
                    }
                    PathData.create(points)
                }

                clientPathData
                    .map { splitByStyle(it).let(::midPointsPathInterpolator) }
                    .flatten()
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

        fun midPointsPathInterpolator(path: List<PathData>): List<PathData> {
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

            return path.mapIndexedNotNull { i, subPath ->
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
                .map { normalizeRing(it, PathPoint.LOC_EQ) }

            if (processedRings.isEmpty()) {
                return null
            }

            return PolygonData(processedRings)
        }
    }

    init {
        require(rings.isNotEmpty()) { "PolygonData should contain at least one ring" }
        require(rings.all { it.isClosed(PathPoint.LOC_EQ) }) { "PolygonData rings should be closed" }
        require(rings.all {
            isRingNormalized(
                it,
                PathPoint.LOC_EQ
            )
        }) { "PolygonData rings should be normalized" }
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
