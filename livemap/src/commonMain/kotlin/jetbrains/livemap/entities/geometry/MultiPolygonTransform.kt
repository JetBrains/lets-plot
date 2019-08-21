package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.Ring
import jetbrains.livemap.core.multitasking.MicroTask

internal class MultiPolygonTransform(
    multiPolygon: MultiPolygon,
    private val myTransform: (DoubleVector, MutableCollection<DoubleVector>) -> Unit
) : MicroTask<MultiPolygon> {
    private lateinit var myPolygonsIterator: Iterator<Polygon>
    private lateinit var myRingIterator: Iterator<Ring>
    private lateinit var myPointIterator: Iterator<DoubleVector>

    private var myNewRing: MutableList<DoubleVector> = ArrayList()
    private var myNewPolygon: MutableList<Ring> = ArrayList()
    private val myNewMultiPolygon = ArrayList<Polygon>()

    private var myHasNext = true
    private lateinit var myResult: MultiPolygon

    init {
        try {
            myPolygonsIterator = multiPolygon.iterator()
            myRingIterator = myPolygonsIterator.next().iterator()
            myPointIterator = myRingIterator.next().iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): MultiPolygon {
        return myResult
    }

    override fun resume() {
        if (!myPointIterator.hasNext()) {
            myNewPolygon.add(Ring(myNewRing))
            if (!myRingIterator.hasNext()) {
                myNewMultiPolygon.add(Polygon(myNewPolygon))
                if (!myPolygonsIterator.hasNext()) {
                    myHasNext = false
                    myResult = MultiPolygon(myNewMultiPolygon)
                    return
                } else {
                    myRingIterator = myPolygonsIterator.next().iterator()
                    myPointIterator = myRingIterator.next().iterator()
                    myNewRing = ArrayList()
                    myNewPolygon = ArrayList()
                }
            } else {
                myPointIterator = myRingIterator.next().iterator()
                myNewRing = ArrayList()
            }
        }

        myTransform(myPointIterator.next(), myNewRing)
    }

    override fun alive(): Boolean {
        return myHasNext
    }
}