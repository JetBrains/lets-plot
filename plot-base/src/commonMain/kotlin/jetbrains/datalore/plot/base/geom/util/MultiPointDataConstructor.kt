package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.gcommon.collect.Ordering.Companion.natural
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.DataPointAesthetics
import kotlin.math.abs

object MultiPointDataConstructor {
    fun collector(): () -> PointCollector {
        return { SimplePointCollector() }
    }

    fun reducer(dropPointDistance: Double, isPolygon: Boolean): () -> PointCollector {
        return {
            PointReducer(
                dropPointDistance,
                isPolygon
            )
        }
    }

    fun singlePointAppender(
            toPoint: (DataPointAesthetics) -> DoubleVector?):
            (DataPointAesthetics, (DoubleVector?) -> Unit) -> Unit {
        return { aes, coordinateConsumer -> coordinateConsumer(toPoint(aes)) }
    }

    fun multiPointAppender(
            toPath: (DataPointAesthetics) -> List<DoubleVector>): (DataPointAesthetics, (DoubleVector) -> Unit) -> Unit {
        return { aes, coordinateConsumer -> toPath(aes).forEach(coordinateConsumer) }
    }

    fun createMultiPointDataByGroup(
        dataPoints: Iterable<DataPointAesthetics>,
        coordinateAppender: (DataPointAesthetics, (DoubleVector?) -> Unit) -> Unit,
        pointCollectorSupplier: () -> PointCollector
    ): List<MultiPointData> {
        val multiPointDataCombiners = HashMap<Int, MultiPointDataCombiner>()

        for (p in dataPoints) {
            val group = p.group()
            if (!multiPointDataCombiners.containsKey(group)) {
                multiPointDataCombiners[group!!] =
                    MultiPointDataCombiner(
                        coordinateAppender,
                        pointCollectorSupplier()
                    )
            }

            multiPointDataCombiners[group]!!.add(p)
        }

        val result = ArrayList<MultiPointData>()

        val sortedGroup = natural<Int>().sortedCopy(multiPointDataCombiners.keys)
        for (group in sortedGroup) {
            val multiPointData = multiPointDataCombiners[group]!!.create(group)

            if (!multiPointData.points.isEmpty()) {
                result.add(multiPointData)
            }
        }

        return result
    }

    interface PointCollector {

        val points: Pair<List<DoubleVector>, List<Int>>
        fun add(coord: DoubleVector?, index: Int)
    }

    private class MultiPointDataCombiner internal constructor(
        private val myCoordinateAppender: (DataPointAesthetics, (DoubleVector?) -> Unit) -> Unit,
        private val myPointCollector: PointCollector
    ) {

        private var myFirstAes: DataPointAesthetics? = null

        internal fun add(aes: DataPointAesthetics) {
            if (myFirstAes == null) {
                myFirstAes = aes
            }
            myCoordinateAppender(aes) { myPointCollector.add(it, aes.index()) }
        }

        internal fun create(group: Int): MultiPointData {
            val points = myPointCollector.points
            return MultiPointData(
                myFirstAes!!,
                points.first,
                { points.second[it] },
                group
            )
        }
    }

    private class SimplePointCollector internal constructor() :
        PointCollector {
        private val myPoints = ArrayList<DoubleVector>()
        private val myIndexes = ArrayList<Int>()

        override val points: Pair<List<DoubleVector>, List<Int>>
            get() = Pair(myPoints, myIndexes)

        override fun add(coord: DoubleVector?, index: Int) {
            myPoints.add(coord!!)
            myIndexes.add(index)
        }
    }

    private class PointReducer internal constructor(private val myDropPointDistance: Double, private val myPolygon: Boolean) :
        PointCollector {

        private val myReducedPoints = ArrayList<DoubleVector>()
        private val myReducedIndexes = ArrayList<Int>()
        private var myLastAdded: DoubleVector? = null
        private var myLastPostponed: Pair<DoubleVector, Int>? = null
        private var myRegionStart: Pair<DoubleVector?, Int?>? = null

        override// Add last postponed to avoid disconnected contours
        val points: Pair<List<DoubleVector>, List<Int>>
            get() {
                if (myLastPostponed != null) {
                    addPoint(myLastPostponed!!.first, myLastPostponed!!.second)
                    myLastPostponed = null
                }

                return Pair(myReducedPoints, myReducedIndexes)
            }

        private fun isCloserThan(p0: DoubleVector, p1: DoubleVector, distance: Double): Boolean {
            return abs(p0.x - p1.x) < distance && abs(p0.y - p1.y) < distance
        }

        override fun add(coord: DoubleVector?, index: Int) {
            if (coord == null) {
                return
            }

            if (myLastAdded == null) {
                storePoint(coord, index)
                return
            }

            if (needPostpone(coord)) {
                postponePoint(coord, index)
            } else {
                // add all
                if (myLastPostponed != null) {
                    addPoint(myLastPostponed!!.first, myLastPostponed!!.second)
                    myLastPostponed = null
                }
                storePoint(coord, index)
            }
        }

        private fun needPostpone(loc: DoubleVector): Boolean {
            val closeToAdded = areTooClose(myLastAdded, loc)
            val closeToPostponed = myLastPostponed == null || areTooClose(myLastPostponed!!.first, loc)
            val regionClosePoint = isRegionStart(loc)

            return closeToAdded && closeToPostponed && !regionClosePoint
        }

        private fun postponePoint(loc: DoubleVector, index: Int) {
            myLastPostponed = Pair(loc, index)
        }

        private fun storePoint(loc: DoubleVector, index: Int) {
            addPoint(loc, index)
            myLastAdded = loc

            processRegionStart(loc, index)
        }

        private fun processRegionStart(loc: DoubleVector?, index: Int?) {
            if (!myPolygon) {
                return
            }

            if (myRegionStart == null) {
                myRegionStart = Pair(loc, index)
                return
            }

            if (isRegionStart(loc)) {
                // Region is closed. Wait next start point.
                myRegionStart = null
                myLastAdded = null
            }
        }

        private fun isRegionStart(loc: DoubleVector?): Boolean {
            if (!myPolygon) {
                return false
            }

            return if (myRegionStart == null) {
                false
            } else myRegionStart!!.first == loc

        }

        private fun addPoint(loc: DoubleVector, index: Int) {
            myReducedPoints.add(loc)
            myReducedIndexes.add(index)
        }

        private fun areTooClose(p1: DoubleVector?, p2: DoubleVector): Boolean {
            return isCloserThan(p1!!, p2, myDropPointDistance)
        }
    }
}
