/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

object ContourStatUtil {
    private val xLoc = doubleArrayOf(0.0, 1.0, 1.0, 0.0, .5)
    private val yLoc = doubleArrayOf(0.0, 0.0, 1.0, 1.0, .5)

    /**
     * @param xs - series defining x-coordinates of data-points
     * @return Pair(col, row) or fail if not a regular grid
     */
    internal fun estimateRegularGridShape(xs: List<Double?>): Pair<Int, Int> {
        // num of columns
        // regular X/Y grid is expected
        var colCount = 0
        var x0: Double? = null
        for (x in xs) {
            if (x0 == null) {
                x0 = x
            } else if (x == x0) {
                break
            }
            colCount++
        }

        if (colCount <= 1) {
            throw IllegalArgumentException("Data grid must be at least 2 columns wide (was $colCount)")
        }
        val rowCount = xs.size / colCount
        if (rowCount <= 1) {
            throw IllegalArgumentException("Data grid must be at least 2 rows tall (was $rowCount)")
        }

        return Pair(colCount, rowCount)
    }

    fun computeLevels(data: DataFrame, binOptions: BinStatUtil.BinOptions): List<Double>? {
        if (!(data.has(TransformVar.X) && data.has(TransformVar.Y) && data.has(
                TransformVar.Z
            ))
        ) {
            return null
        }
        val zRange = data.range(TransformVar.Z)
        return computeLevels(zRange, binOptions)
    }

    fun computeLevels(zRange: DoubleSpan?, binOptions: BinStatUtil.BinOptions): List<Double>? {
        if (zRange == null || SeriesUtil.isBeyondPrecision(zRange)) return null

        val b = BinStatUtil.binCountAndWidth(zRange.length, binOptions)
        val levels = ArrayList<Double>()
        for (i in 0 until b.count) {
            var level = i * b.width + zRange.lowerEnd
            level += b.width / 2   // shift all levels half-step to make 1-st level contour visible
            levels.add(level)
        }
        return levels
    }

    internal fun computeContours(data: DataFrame, levels: List<Double>): Map<Double, List<List<DoubleVector>>> {
        val xVector = data.getNumeric(TransformVar.X)
        val zVector = data.getNumeric(TransformVar.Z)

        val shape = estimateRegularGridShape(xVector)
        val colCount = shape.first
        val rowCount = shape.second

        val xRange = data.range(TransformVar.X)!!
        val yRange = data.range(TransformVar.Y)!!

        return computeContours(
            xRange,
            yRange,
            colCount,
            rowCount,
            zVector,
            levels
        )
    }

    fun computeContours(
        xRange: DoubleSpan,
        yRange: DoubleSpan,
        colCount: Int,
        rowCount: Int,
        data: List<Double?>,
        levels: List<Double>
    ): Map<Double, List<List<DoubleVector>>> {

        val xStep = xRange.length / (colCount - 1)
        val yStep = yRange.length / (rowCount - 1)
        val origin = DoubleVector(xRange.lowerEnd, yRange.lowerEnd)

        val pathListByLevel = HashMap<Double, List<List<DoubleVector>>>()
        for (level in levels) {
            val levelSegments =
                computeSegments(colCount, rowCount, data, level)
            val paths = joinSegments(levelSegments)
            // checkEdges(paths, colCount, rowCount); // debug use
            var pathsReal =
                convertPaths(paths, xStep, yStep, origin, level)
            pathsReal = confirmPaths(pathsReal)
            pathListByLevel[level] = pathsReal
        }

        return pathListByLevel
    }

    private fun joinSegments(segments: List<Pair<Edge, Edge>>): List<List<Edge>> {
        val pathList = ArrayList<List<Edge>>()

        val pathByEndPoint = HashMap<Edge, MutableList<Edge>>()

        for (segment in segments) {
            val p0 = segment.first
            val p1 = segment.second

            if (pathByEndPoint.containsKey(p0) || pathByEndPoint.containsKey(p1)) {
                //remove need to be modified paths
                val path0 = pathByEndPoint[p0]
                val path1 = pathByEndPoint[p1]

                if (path0 != null) {
                    pathByEndPoint.remove(path0[0])
                    pathByEndPoint.remove(path0[path0.size - 1])
                }
                if (path1 != null) {
                    pathByEndPoint.remove(path1[0])
                    pathByEndPoint.remove(path1[path1.size - 1])
                }

                val longPath: MutableList<Edge>
                if (path0 === path1) {
                    //a cycle is formed
                    path0!!.add(segment.second)
                    pathList.add(path0)
                    continue
                } else if (path0 != null && path1 != null) {
                    longPath = path0
                    longPath.addAll(path1)
                } else if (path0 == null) { //path1!=null
                    longPath = path1!!
                    longPath.add(0, segment.first)
                } else { // path0!=null
                    longPath = path0
                    longPath.add(segment.second)
                }

                pathByEndPoint[longPath[0]] = longPath
                pathByEndPoint[longPath[longPath.size - 1]] = longPath
            } else {
                val path = ArrayList<Edge>()
                path.add(segment.first)
                path.add(segment.second)
                pathByEndPoint[segment.first] = path
                pathByEndPoint[segment.second] = path
            }
        }

        // collect paths (don't duplicate)
        val pathSet = HashSet(pathByEndPoint.values)
        for (path in pathSet) {
            pathList.add(path)
        }
        return pathList
    }

    private fun convertPaths(
        paths: List<List<Edge>>,
        xStep: Double,
        yStep: Double,
        origin: DoubleVector,
        level: Double?
    ): List<List<DoubleVector>> {
        val result = ArrayList<List<DoubleVector>>()

        for (path in paths) {
            val temp = ArrayList<DoubleVector>()
            var lastPoint: DoubleVector? = null
            for (edge in path) {
                var intersect = edge.intersect(level!!)
                intersect = DoubleVector(xStep * intersect.x, yStep * intersect.y).add(origin)
                // in case two adjacent double vectors are the same
                if (intersect == lastPoint) {
                    continue
                }
                temp.add(intersect)
                lastPoint = intersect
            }
            // in case of slight computational error in loops
            if (path[0] == path[path.size - 1] && temp[0] != temp[temp.size - 1]) {
                temp[temp.size - 1] = temp[0]
            }
            if (temp.size > 1) {
                result.add(temp)
            }
        }

        return result
    }

    private fun confirmPaths(paths: List<List<DoubleVector>>): List<List<DoubleVector>> {
        // join paths that share the same end points
        val newPaths = ArrayList<List<DoubleVector>>()

        val pathByEndPoint = HashMap<DoubleVector, List<DoubleVector>>()

        for (path in paths) {
            val p0 = path[0]
            val p1 = path[path.size - 1]
            //if cycle
            if (p0 == p1) {
                newPaths.add(path)
                continue
            }

            if (pathByEndPoint.containsKey(p0) || pathByEndPoint.containsKey(p1)) {
                //remove need to be modified paths
                val path0 = pathByEndPoint[p0]
                val path1 = pathByEndPoint[p1]
                removePathByEndpoints(path0, pathByEndPoint)
                removePathByEndpoints(path1, pathByEndPoint)

                val longPath = ArrayList<DoubleVector>()
                if (path0 === path1) {
                    //a cycle is formed
                    longPath.addAll(path0!!)
                    longPath.addAll(path.subList(1, path.size))
                    newPaths.add(longPath)
                    continue
                } else if (path0 != null && path1 != null) {
                    longPath.addAll(path0)
                    longPath.addAll(path.subList(1, path.size - 1))
                    longPath.addAll(path1)
                } else if (path0 == null) { //path1!=null
                    longPath.addAll(path1!!)
                    longPath.addAll(0, path.subList(0, path.size - 1))
                } else { //path0!=null
                    longPath.addAll(path0)
                    longPath.addAll(path.subList(1, path.size))
                }

                pathByEndPoint[longPath[0]] = longPath
                pathByEndPoint[longPath[longPath.size - 1]] = longPath
            } else {
                pathByEndPoint[path[0]] = path
                pathByEndPoint[path[path.size - 1]] = path
            }
        }

        val pathSet = HashSet(pathByEndPoint.values)
        for (path in pathSet) {
            newPaths.add(path)
        }

        // prevent the degenerated polygons from providing weird outputs
        val result = ArrayList<List<DoubleVector>>()
        for (path in newPaths) {
            result.addAll(pathSeparator(path))
        }
        return result
    }

    internal fun <T : List<DoubleVector>> removePathByEndpoints(
        path: List<DoubleVector>?,
        pathByEndPoint: MutableMap<DoubleVector, T>
    ) {
        if (path != null) {
            pathByEndPoint.remove(path[0])
            pathByEndPoint.remove(path[path.size - 1])
        }
    }

    private fun pathSeparator(path: List<DoubleVector>): List<List<DoubleVector>> {
        val result = ArrayList<List<DoubleVector>>()
        var startIndex = 0
        for (nextIndex in 1 until path.size - 1) {
            if (path[startIndex] == path[nextIndex]) {
                result.add(path.subList(startIndex, nextIndex + 1))
                startIndex = nextIndex
            }
        }
        if (startIndex == 0) {
            return listOf(path)
        } else {
            result.add(path.subList(startIndex, path.size))
            return result
        }
    }

    private fun computeSegments(
        colCount: Int,
        rowCount: Int,
        data: List<Double?>,
        level: Double
    ): List<Pair<Edge, Edge>> {

        val segments = ArrayList<Pair<Edge, Edge>>()
        for (row in 0 until rowCount - 1) {
            for (col in 0 until colCount - 1) {
                val i0 = row * colCount + col
                val i1 = row * colCount + col + 1 // next right
                val i2 = (row + 1) * colCount + col + 1 // next right & up
                val i3 = (row + 1) * colCount + col // next up

                val v = DoubleArray(5)
                v[0] = data[i0]!!
                v[1] = data[i1]!!
                v[2] = data[i2]!!
                v[3] = data[i3]!!

                var min = v[0]
                var max = v[0]
                for (i in 1..3) {
                    min = min(min, v[i])
                    max = max(max, v[i])
                }

                if (min == max) {
                    // special case: all points are in the same plane - drop
                    continue
                }

                // v[4] = (v[0] + v[1] + v[2] + v[3]) / 4;   // center  (average of corners)
                v[4] = (v[0] + v[1] + v[2] + v[3] - min - max) / 2   // center  (average of corners)

                if (level > min && level <= max) {

                    val cellSegments =
                        computeSegmentsForGridCell(level, v, col, row)
                    segments.addAll(cellSegments)
                }
            }
        }

        return segments
    }

    private fun computeSegmentsForGridCell(
        level: Double?,
        value5: DoubleArray,
        col: Int,
        row: Int
    ): List<Pair<Edge, Edge>> {
        // triangles:
        // [0] 0-1-4
        // [1] 1-2-4
        // [2] 2-3-4
        // [3] 3-0-4
        // check each edge of each triangle - find edges that cross the level 'plane'

        val result = ArrayList<Pair<Edge, Edge>>()
        val points = ArrayList<TripleVector>()
        var tempVector: MutableList<TripleVector>
        for (i in 0..4) {
            points.add(
                TripleVector(
                    col + xLoc[i],
                    row + yLoc[i],
                    value5[i]
                )
            )
        }

        // All triangles are counterclockwise
        for (i in 0..3) {
            val i1 = (i + 1) % 4
            tempVector = ArrayList()
            tempVector.add(points[i])
            tempVector.add(points[i1])
            tempVector.add(points[4])
            val temp = intersectionSegment(tempVector, level)
            if (temp != null) {
                result.add(temp)
            }
        }
        return result
    }

    private fun intersectionSegment(vectors: List<TripleVector>, level: Double?): Pair<Edge, Edge>? {
        // input: a counterclockwise triangle
        // output: a segment, such that the higher part is always to the right
        val type = vectors[0].getType(level!!) * 100 + vectors[1].getType(level) * 10 + vectors[2].getType(level)
        val start: Edge
        val end: Edge
        when (type) {
            100 -> {
                start = Edge(vectors[2], vectors[0])
                end = Edge(vectors[0], vectors[1])
            }
            10 -> {
                start = Edge(vectors[0], vectors[1])
                end = Edge(vectors[1], vectors[2])
            }
            1 -> {
                start = Edge(vectors[1], vectors[2])
                end = Edge(vectors[2], vectors[0])
            }
            110 -> {
                start = Edge(vectors[0], vectors[2])
                end = Edge(vectors[2], vectors[1])
            }
            101 -> {
                start = Edge(vectors[2], vectors[1])
                end = Edge(vectors[1], vectors[0])
            }
            11 -> {
                start = Edge(vectors[1], vectors[0])
                end = Edge(vectors[0], vectors[2])
            }
            else -> return null
        }
        return Pair(start, end)
    }

    // to check if all the side edges are on the boarder **Debug Use**
    private fun checkEdges(paths: List<List<Edge>>, colCount: Int, rowCount: Int) {
        for (path in paths) {
            if (path[0] != path[path.size - 1]) {
                checkEdge(path[0], colCount, rowCount)
                checkEdge(path[path.size - 1], colCount, rowCount)
            }
        }
    }

    private fun checkEdge(edge: Edge, colCount: Int, rowCount: Int) {
        val a = edge.myA
        val b = edge.myB
        if (a.myX == 0 && b.myX == 0) {
            return
        }
        if (a.myY == 0 && b.myY == 0) {
            return
        }
        if (a.myX == colCount - 1 && b.myX == colCount - 1) {
            return
        }
        if (a.myY == rowCount - 1 && b.myY == rowCount - 1) {
            return
        }
        throw IllegalArgumentException("Check Edge Failed")
    }

    private class TripleVector internal constructor(x: Double, y: Double, val z: Double) {
        val myX: Int
        val myY: Int
        private val myIsCenter: Int

        val coord: DoubleVector
            get() = DoubleVector(x, y)

        val x: Double
            get() = this.myX + 0.5 * myIsCenter

        val y: Double
            get() = this.myY + 0.5 * myIsCenter

        init {
            this.myX = x.toInt()
            this.myY = y.toInt()
            this.myIsCenter = if (x % 1 == 0.0) 0 else 1
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            val that = other as TripleVector?
            return myX == that!!.myX && myY == that.myY && myIsCenter == that.myIsCenter
        }

        override fun hashCode(): Int {
            return arrayOf(myX, myY, myIsCenter).hashCode()
        }

        fun getType(level: Double): Int {
            return if (z >= level) {
                1
            } else {
                0
            }
        }
    }

    private class Edge internal constructor(val myA: TripleVector, val myB: TripleVector) {

        override fun equals(other: Any?): Boolean {
            if (other !is Edge) {
                return false
            }
            val obj = other as Edge?
            return this.myA == obj!!.myA && this.myB == obj.myB || this.myA == obj.myB && this.myB == obj.myA
        }

        override fun hashCode(): Int {
            return myA.coord.hashCode() + myB.coord.hashCode()
        }

        fun intersect(level: Double): DoubleVector {
            val z0 = myA.z
            val z1 = myB.z

            if (level == z0) {
                return myA.coord
            }
            if (level == z1) {
                return myB.coord
            }

            val ratio = (z1 - z0) / (level - z0)
            val x0 = myA.x
            val y0 = myA.y
            val x1 = myB.x
            val y1 = myB.y
            val x = x0 + (x1 - x0) / ratio
            val y = y0 + (y1 - y0) / ratio
            return DoubleVector(x, y)
        }
    }
}// **Debug Use**
