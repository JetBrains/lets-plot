package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.gcommon.collect.ImmutableList
import jetbrains.datalore.base.gcommon.collect.Ordering
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.render.AestheticsUtil
import jetbrains.datalore.visualization.plot.gog.core.render.CoordinateSystem
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext
import jetbrains.datalore.visualization.plot.gog.core.render.PositionAdjustment
import jetbrains.datalore.visualization.plot.gog.core.render.geom.StepGeom
import jetbrains.datalore.visualization.plot.gog.core.render.linetype.LineType
import jetbrains.datalore.visualization.plot.gog.core.render.svg.LinePath

import java.util.ArrayList
import java.util.function.Function

import jetbrains.datalore.base.values.Colors.withOpacity
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.MultiPointDataConstructor.reducer
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.MultiPointDataConstructor.singlePointAppender

class LinesHelper(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) : GeomHelper(pos, coord, ctx) {

    private var myAlphaFilter = Function.identity<Double>()
    private var myWidthFilter = Function.identity<Double>()
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

    fun createLines(dataPoints: Iterable<DataPointAesthetics>,
                    toLocation: Function<DataPointAesthetics, DoubleVector>): List<LinePath> {
        return createPaths(dataPoints, toLocation, false)
    }

    private fun createPaths(dataPoints: Iterable<DataPointAesthetics>,
                            toLocation: Function<DataPointAesthetics, DoubleVector>, closePath: Boolean): List<LinePath> {
        val paths = ArrayList<LinePath>()
        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
                dataPoints,
                singlePointAppender(toClientLocation(toLocation)),
                reducer(0.999, closePath)
        )

        // draw line for each group
        for (multiPointData in multiPointDataList) {
            paths.addAll(createPaths(multiPointData.aes, multiPointData.points, closePath))
        }

        return paths
    }

    internal fun createPaths(aes: DataPointAesthetics, points: List<DoubleVector>, closePath: Boolean): List<LinePath> {
        val paths = ArrayList<LinePath>()
        if (closePath) {
            paths.add(LinePath.polygon(insertPathSeparators(GeomUtil.createRingsFromPoints(points))))
        } else {
            paths.add(LinePath.line(points))
        }
        paths.forEach { path -> decorate(path, aes, closePath) }
        return paths
    }

    fun createSteps(dataPoints: Iterable<DataPointAesthetics>, dir: StepGeom.Direction): List<PathInfo> {
        val pathInfos = ArrayList<PathInfo>()
        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
                dataPoints,
                singlePointAppender(toClientLocation(GeomUtil.TO_LOCATION_X_Y)),
                reducer(0.999, false)
        )

        // draw step for each group
        for (multiPointData in multiPointDataList) {
            val points = multiPointData.getPoints()
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
                decorate(path, multiPointData.getAes(), false)
                pathInfos.add(PathInfo(path, multiPointData.getAes(), multiPointData.getGroup()))
            }
        }

        return pathInfos
    }

    fun createBands(dataPoints: Iterable<DataPointAesthetics>,
                    toLocationUpper: Function<DataPointAesthetics, DoubleVector>,
                    toLocationLower: Function<DataPointAesthetics, DoubleVector>): List<LinePath> {

        val lines = ArrayList<LinePath>()
        val pointsByGroup = GeomUtil.createGroups(dataPoints)

        // draw line for each group
        for (group in Ordering.natural().sortedCopy(pointsByGroup.keys)) {
            val groupDataPoints = pointsByGroup[group]
            // upper margin points
            val points = ArrayList(project(groupDataPoints, toLocationUpper))

            // lower margin point in reversed order
            val lowerPoints = ImmutableList.reverse(groupDataPoints)
            points.addAll(project(lowerPoints, toLocationLower))

            if (!points.isEmpty()) {
                val path = LinePath.polygon(points)
                //decorate(path, groupDataPoints.get(0), true);
                decorateFillingPart(path, groupDataPoints.get(0))
                lines.add(path)
            }
        }
        return lines
    }

    protected fun decorate(path: LinePath, p: DataPointAesthetics, filled: Boolean) {

        val stroke = p.color()
        val strokeAlpha = myAlphaFilter.apply(AestheticsUtil.alpha(stroke!!, p))
        path.color().set(withOpacity(stroke, strokeAlpha))
        if (!AestheticsUtil.ALPHA_CONTROLS_BOTH && (filled || !myAlphaEnabled)) {
            path.color().set(stroke)
        }

        if (filled) {
            decorateFillingPart(path, p)
        }

        val size = myWidthFilter.apply(AestheticsUtil.strokeWidth(p))
        path.width().set(size)

        val lineType = p.lineType()
        if (!(lineType.isBlank || lineType.isSolid)) {
            path.dashArray().set(lineType.dashArray)
        }
    }

    private fun decorateFillingPart(path: LinePath, p: DataPointAesthetics) {
        val fill = p.fill()
        val fillAlpha = myAlphaFilter.apply(AestheticsUtil.alpha(fill!!, p))
        path.fill().set(withOpacity(fill, fillAlpha))
    }

    fun setAlphaFilter(alphaFilter: Function<Double, Double>) {
        myAlphaFilter = alphaFilter
    }

    fun setWidthFilter(widthFilter: Function<Double, Double>) {
        myWidthFilter = widthFilter
    }

    // ToDo: get rid of PathInfo class
    class PathInfo private constructor(val path: LinePath, aes: DataPointAesthetics, group: Int)
}
