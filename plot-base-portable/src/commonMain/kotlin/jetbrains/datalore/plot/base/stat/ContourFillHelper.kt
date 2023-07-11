/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.stat.ContourStatUtil.removePathByEndpoints

class ContourFillHelper(xRange: DoubleSpan, yRange: DoubleSpan) {

    private val myLowLeft: DoubleVector
    private val myLowRight: DoubleVector
    private val myUpLeft: DoubleVector
    private val myUpRight: DoubleVector

    init {
        val xMin = xRange.lowerEnd
        val xMax = xRange.upperEnd
        val yMin = yRange.lowerEnd
        val yMax = yRange.upperEnd
        myLowLeft = DoubleVector(xMin, yMin)
        myLowRight = DoubleVector(xMax, yMin)
        myUpLeft = DoubleVector(xMin, yMax)
        myUpRight = DoubleVector(xMax, yMax)
    }

    fun createPolygons(
        pathByLevels: Map<Double, List<List<DoubleVector>>>, levels: List<Double>, fillLevels: List<Double>): Map<Double, List<DoubleVector>> {

        val result = HashMap<Double, List<DoubleVector>>()

        //getBoundary
        val allPaths = ArrayList<List<DoubleVector>>()
        for (level in pathByLevels.values) {
            allPaths.addAll(level)
        }

        val cornerPoints = ArrayList<DoubleVector>()
        val outerMap = createOuterMap(allPaths, cornerPoints)


        val n = pathByLevels.keys.size

        // The designated area is always to the left
        for (i in 0 until n + 1) {
            var polygonPieces: MutableList<List<DoubleVector>> = ArrayList()
            val value = ArrayList<DoubleVector>()
            if (i > 0) {
                polygonPieces.addAll(
                    reverseAll(
                        pathByLevels[levels[i - 1]]!!
                    )
                )
            }
            if (i < n) {
                polygonPieces.addAll(pathByLevels[levels[i]]!!)
            }
            polygonPieces = createClosedPolygonLevels(polygonPieces, outerMap, cornerPoints)
            for (path in polygonPieces) {
                value.addAll(path)
            }
            result[fillLevels[i]] = value
        }
        return result
    }

    private fun createClosedPolygonLevels(
        openLevels: List<List<DoubleVector>>, outerMap: Map<DoubleVector, DoubleVector>, cornerPoints: List<DoubleVector>): MutableList<List<DoubleVector>> {
        val result = ArrayList<List<DoubleVector>>()

        val donePath = HashSet<MutableList<DoubleVector>>()
        //Add boundary part to each openPath
        for (level in openLevels) {
            val levelCopy = ArrayList(level)
            val p0 = levelCopy[0]
            var p1 = levelCopy[levelCopy.size - 1]
            //if cycle
            if (p0 == p1) {
                result.add(levelCopy)
                continue
            }
            //p1 remains to be the end of levelCopy
            var pi2 = outerMap[p1]!!
            levelCopy.add(pi2)
            p1 = pi2
            while (cornerPoints.contains(p1)) {
                pi2 = outerMap[p1]!!
                levelCopy.add(pi2)
                p1 = pi2
            }
            donePath.add(levelCopy)
        }

        val pathByEndPoint = HashMap<DoubleVector, MutableList<DoubleVector>>()

        for (path in donePath) {
            val p0 = path[0]
            val p1 = path[path.size - 1]
            //if cycle
            if (p0 == p1) {
                result.add(path)
                continue
            }

            if (pathByEndPoint.containsKey(p0) || pathByEndPoint.containsKey(p1)) {
                //remove need to be modified paths
                val path0 = pathByEndPoint[p0]
                val path1 = pathByEndPoint[p1]
                removePathByEndpoints(path0, pathByEndPoint)
                removePathByEndpoints(path1, pathByEndPoint)

                val longPath: MutableList<DoubleVector>
                if (path0 === path1) {
                    //a cycle is formed
                    path0!!.addAll(path.subList(1, path.size))
                    result.add(path0)
                    continue
                } else if (path0 != null && path1 != null) {
                    longPath = path0
                    longPath.addAll(path.subList(1, path.size - 1))
                    longPath.addAll(path1)
                } else if (path0 == null) { //path1!=null
                    longPath = path1!!
                    longPath.addAll(0, path.subList(0, path.size - 1))
                } else { //path0!=null
                    longPath = path0
                    longPath.addAll(path.subList(1, path.size))
                }

                pathByEndPoint[longPath[0]] = longPath
                pathByEndPoint[longPath[longPath.size - 1]] = longPath
            } else {
                pathByEndPoint[path[0]] = path
                pathByEndPoint[path[path.size - 1]] = path
            }
        }

        if (!pathByEndPoint.keys.isEmpty()) {
            throw IllegalArgumentException("Some paths are not cleared yet there is something wrong!" + pathByEndPoint.keys.size)
        }

        // check
        for (path in result) {
            if (path[0] != path[path.size - 1]) {
                throw IllegalArgumentException("The polygons are not entirely closed!")
            }
        }
        return result
    }

    //counterclockwise
    //update corner points for record
    private fun createOuterMap(paths: List<List<DoubleVector>>, cornerPoints: MutableList<DoubleVector>): Map<DoubleVector, DoubleVector> {
        val points = ArrayList<DoubleVector>()
        for (path in paths) {
            if (path[0] != path[path.size - 1]) {
                points.add(path[0])
                points.add(path[path.size - 1])
            }
        }

        val result = HashMap<DoubleVector, DoubleVector>()

        val list = ArrayList<MutableList<DoubleVector>>()
        for (i in 0..3) {
            list.add(ArrayList())
        }

        for (point in points) {
            val kind = getKind(point)
            when (kind) {
                BorderKind.DOWN -> list[0].add(point)
                BorderKind.RIGHT -> list[1].add(point)
                BorderKind.UP -> list[2].add(point)
                BorderKind.LEFT -> list[3].add(point)
            }
        }

        list[0].sortWith(Comparator { o1, o2 -> o1.x.compareTo(o2.x) })
        list[1].sortWith(Comparator { o1, o2 -> o1.y.compareTo(o2.y) })
        list[2].sortWith(Comparator { o1, o2 -> o2.x.compareTo(o1.x) })
        list[3].sortWith(Comparator { o1, o2 -> o2.y.compareTo(o1.y) })

        // According to getKind: myLowLeft -> DOWN; myLowRight -> DOWN; myUpRight -> RIGHT; myUpLeft -> LEFT
        val prepareMap = ArrayList<DoubleVector>()
        if (!list[0].contains(myLowLeft)) {
            prepareMap.add(myLowLeft)
            cornerPoints.add(myLowLeft)
        }
        prepareMap.addAll(list[0])
        if (!list[0].contains(myLowRight)) {
            prepareMap.add(myLowRight)
            cornerPoints.add(myLowRight)
        }
        prepareMap.addAll(list[1])
        if (!list[1].contains(myUpRight)) {
            prepareMap.add(myUpRight)
            cornerPoints.add(myUpRight)
        }
        prepareMap.addAll(list[2])
        if (!list[3].contains(myUpLeft)) {
            prepareMap.add(myUpLeft)
            cornerPoints.add(myUpLeft)
        }
        prepareMap.addAll(list[3])
        prepareMap.add(prepareMap[0])
        for (i in 0 until prepareMap.size - 1) {
            result[prepareMap[i]] = prepareMap[i + 1]
        }

        return result
    }

    private fun getKind(point: DoubleVector): BorderKind {
        return if (belowOrOnLine(
                myLowLeft,
                myUpRight,
                point
            ) && belowOrOnLine(
                myUpLeft,
                myLowRight,
                point
            )
        ) {
            BorderKind.DOWN
        } else if (belowOrOnLine(
                myLowLeft,
                myUpRight,
                point
            ) && !belowOrOnLine(
                myUpLeft,
                myLowRight,
                point
            )
        ) {
            BorderKind.RIGHT
        } else if (!belowOrOnLine(
                myLowLeft,
                myUpRight,
                point
            ) && !belowOrOnLine(
                myUpLeft,
                myLowRight,
                point
            )
        ) {
            BorderKind.UP
        } else if (!belowOrOnLine(
                myLowLeft,
                myUpRight,
                point
            ) && belowOrOnLine(
                myUpLeft,
                myLowRight,
                point
            )
        ) {
            BorderKind.LEFT
        } else {
            throw IllegalArgumentException("The Contour Point is not on the border $point")
        }
    }

    private enum class BorderKind {
        DOWN, RIGHT, UP, LEFT
    }

    companion object {

        fun computeFillLevels(zRange: DoubleSpan, levels: List<Double>): List<Double> {
            val fillLevels = ArrayList<Double>()
            fillLevels.add(zRange.lowerEnd)
            for (i in 0 until levels.size - 1) {
                fillLevels.add((levels[i] + levels[i + 1]) / 2.0)
            }
            fillLevels.add(zRange.upperEnd)
            return fillLevels
        }

        private fun <E> reverseAll(list: List<List<E>>): List<List<E>> {
            val result = ArrayList<List<E>>()
            for (item in list) {
                val copy = ArrayList(item)
                copy.reverse()
                result.add(copy)
            }
            return result
        }

        // a.x<b.x
        private fun belowOrOnLine(a: DoubleVector, b: DoubleVector, test: DoubleVector): Boolean {
            val value = test.subtract(a)
            val standard = b.subtract(a)

            return standard.y * value.x - value.y * standard.x >= 0
        }
    }
}
