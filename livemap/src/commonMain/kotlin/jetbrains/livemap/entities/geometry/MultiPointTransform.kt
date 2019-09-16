package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.livemap.core.multitasking.MicroTask

internal class MultiPointTransform<InT, OutT>(
    multiPoint: Typed.MultiPoint<InT>,
    private val myTransform: (Typed.Vec<InT>, MutableCollection<Typed.Vec<OutT>>) -> Unit
) : MicroTask<Typed.MultiPoint<OutT>> {
    private lateinit var myPointIterator: Iterator<Typed.Vec<InT>>

    private val myNewMultiPoint = ArrayList<Typed.Vec<OutT>>()

    private var myHasNext = true
    private lateinit var myResult: Typed.MultiPoint<OutT>

    init {
        try {
            myPointIterator = multiPoint.iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): Typed.MultiPoint<OutT> {
        return myResult
    }

    override fun resume() {
        if (!myPointIterator.hasNext()) {
            myHasNext = false
            myResult = Typed.MultiPoint(myNewMultiPoint)
            return
        }

        myTransform(myPointIterator.next(), myNewMultiPoint)
    }

    override fun alive(): Boolean {
        return myHasNext
    }
}
