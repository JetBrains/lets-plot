package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.MultiPoint
import jetbrains.datalore.base.projectionGeometry.Point
import jetbrains.livemap.core.multitasking.MicroTask

internal class MultiPointTransform(
    multiPoint: MultiPoint,
    private val myTransform: (Point, MutableCollection<Point>) -> Unit
) : MicroTask<MultiPoint> {
    private lateinit var myPointIterator: Iterator<Point>

    private val myNewMultiPoint = ArrayList<Point>()

    private var myHasNext = true
    private lateinit var myResult: MultiPoint

    init {
        try {
            myPointIterator = multiPoint.iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): MultiPoint {
        return myResult
    }

    override fun resume() {
        if (!myPointIterator.hasNext()) {
            myHasNext = false
            myResult = MultiPoint(myNewMultiPoint)
            return
        }

        myTransform(myPointIterator.next(), myNewMultiPoint)
    }

    override fun alive(): Boolean {
        return myHasNext
    }
}
