package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.livemap.core.multitasking.MicroTask

internal class MultiPolygonTransform<InT, OutT>(
    multiPolygon: Typed.MultiPolygon<InT>,
    private val myTransform: (Typed.Vec<InT>, MutableCollection<Typed.Vec<OutT>>) -> Unit
) : MicroTask<Typed.MultiPolygon<OutT>> {
    private lateinit var myPolygonsIterator: Iterator<Typed.Polygon<InT>>
    private lateinit var myRingIterator: Iterator<Typed.Ring<InT>>
    private lateinit var myPointIterator: Iterator<Typed.Vec<InT>>

    private var myNewRing: MutableList<Typed.Vec<OutT>> = ArrayList()
    private var myNewPolygon: MutableList<Typed.Ring<OutT>> = ArrayList()
    private val myNewMultiPolygon = ArrayList<Typed.Polygon<OutT>>()

    private var myHasNext = true
    private lateinit var myResult: Typed.MultiPolygon<OutT>

    init {
        try {
            myPolygonsIterator = multiPolygon.iterator()
            myRingIterator = myPolygonsIterator.next().iterator()
            myPointIterator = myRingIterator.next().iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): Typed.MultiPolygon<OutT> {
        return myResult
    }

    override fun resume() {
        if (!myPointIterator.hasNext()) {
            myNewPolygon.add(Typed.Ring(myNewRing))
            if (!myRingIterator.hasNext()) {
                myNewMultiPolygon.add(Typed.Polygon(myNewPolygon))
                if (!myPolygonsIterator.hasNext()) {
                    myHasNext = false
                    myResult = Typed.MultiPolygon(myNewMultiPolygon)
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