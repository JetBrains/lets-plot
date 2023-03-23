/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.algorithms.splitRings
import jetbrains.datalore.base.gcommon.collect.Ordering
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Colors.withOpacity
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.StepGeom
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.reducer
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.singlePointAppender
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.plot.common.geometry.PolylineSimplifier

open class LinesHelper(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) :
    GeomHelper(pos, coord, ctx) {

    private var myAlphaFilter = { v: Double? -> v }
    private var myWidthFilter = { v: Double? -> v }
    private var myAlphaEnabled = true

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

    fun setAlphaEnabled(b: Boolean) {
        this.myAlphaEnabled = b
    }

    fun createLines(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): MutableList<LinePath> {
        return createPaths(dataPoints, toLocation, false)
    }

    private fun createPaths(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?,
        closePath: Boolean
    ): MutableList<LinePath> {
        val paths = ArrayList<LinePath>()
        val multiPointDataList =
            MultiPointDataConstructor.createMultiPointDataByGroup(
                dataPoints,
                singlePointAppender(toClientLocation { toLocation(it) }),
                reducer(0.999, closePath)
            )

        // draw line for each group
        for (multiPointData in multiPointDataList) {
            paths.addAll(createPaths(multiPointData.aes, multiPointData.points, closePath))
        }

        return paths
    }

    internal fun createPaths(
        aes: DataPointAesthetics,
        points: List<DoubleVector>,
        closePath: Boolean
    ): List<LinePath> {
        val paths = ArrayList<LinePath>()
        if (closePath) {
            paths.add(LinePath.polygon(insertPathSeparators(splitRings(points))))
        } else {
            paths.add(LinePath.line(points))
        }
        paths.forEach { path -> decorate(path, aes, closePath) }
        return paths
    }

    internal fun createSteps(dataPoints: Iterable<DataPointAesthetics>, dir: StepGeom.Direction): List<PathInfo> {
        val pathInfos = ArrayList<PathInfo>()
        val multiPointDataList =
            MultiPointDataConstructor.createMultiPointDataByGroup(
                dataPoints,
                singlePointAppender(toClientLocation(GeomUtil.TO_LOCATION_X_Y)),
                reducer(0.999, false)
            )

        // draw step for each group
        for (multiPointData in multiPointDataList) {
            val points = multiPointData.points
            if (!points.isEmpty()) {
                val newPoints = ArrayList<DoubleVector>()
                var prev: DoubleVector? = null
                for (point in points) {
                    if (prev != null) {
                        val x = if (dir === StepGeom.Direction.HV) point.x else prev.x
                        val y = if (dir === StepGeom.Direction.HV) prev.y else point.y
                        newPoints.add(DoubleVector(x, y))
                    }
                    newPoints.add(point)
                    prev = point
                }

                val path = LinePath.line(newPoints)
                decorate(path, multiPointData.aes, false)
                pathInfos.add(
                    PathInfo(
                        path
                    )
                )
            }
        }

        return pathInfos
    }

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
            val upperPoints = project(groupDataPoints) { toLocationUpper(it) }
            val points = ArrayList(if (simplifyBorders) simplify(upperPoints) else upperPoints)

            // lower margin point in reversed order
            val lowerPoints = ArrayList(project(groupDataPoints.reversed()) { toLocationLower(it) })
            points.addAll(if (simplifyBorders) simplify(lowerPoints) else lowerPoints)

            if (!points.isEmpty()) {
                val path = LinePath.polygon(points)
                //decorate(path, groupDataPoints.get(0), true);
                decorateFillingPart(path, groupDataPoints[0])
                lines.add(path)
            }
        }
        return lines
    }

    private fun simplify(points: List<DoubleVector>): List<DoubleVector> {
        val weightLimit = 0.25 // in px for Douglas–Peucker algorithm
        return PolylineSimplifier.douglasPeucker(points).setWeightLimit(weightLimit).points
    }

    protected fun decorate(path: LinePath, p: DataPointAesthetics, filled: Boolean, isLine: Boolean = true) {

        val stroke = p.color()
        val strokeAlpha = myAlphaFilter(AestheticsUtil.alpha(stroke!!, p))!!
        path.color().set(withOpacity(stroke, strokeAlpha))
        if (!AestheticsUtil.ALPHA_CONTROLS_BOTH && (filled || !myAlphaEnabled)) {
            path.color().set(stroke)
        }

        if (filled) {
            decorateFillingPart(path, p)
        }

        val size = if (isLine) {
            myWidthFilter(AesScaling.strokeWidth(p))!!
        } else {
            myWidthFilter(AesScaling.pointStrokeWidth(p))!!
        }
        path.width().set(size)

        val lineType = p.lineType()
        if (!(lineType.isBlank || lineType.isSolid)) {
            path.dashArray().set(lineType.dashArray)
        }
    }

    private fun decorateFillingPart(path: LinePath, p: DataPointAesthetics) {
        val fill = p.fill()
        val fillAlpha = myAlphaFilter(AestheticsUtil.alpha(fill!!, p))!!
        path.fill().set(withOpacity(fill, fillAlpha))
    }

    fun setAlphaFilter(alphaFilter: (Double?) -> Double?) {
        myAlphaFilter = alphaFilter
    }

    fun setWidthFilter(widthFilter: (Double?) -> Double?) {
        myWidthFilter = widthFilter
    }

    // ToDo: get rid of PathInfo class
    class PathInfo internal constructor(val path: LinePath)
}
