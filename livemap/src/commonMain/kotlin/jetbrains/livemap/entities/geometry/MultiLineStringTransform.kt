package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.livemap.core.multitasking.MicroTask

internal class MultiLineStringTransform<InT, OutT> (
    multiLineString: Typed.MultiLineString<InT>,
    private val myTransform: (Typed.Point<InT>, MutableCollection<Typed.Point<OutT>>) -> Unit
) : MicroTask<Typed.MultiLineString<OutT>> {
    private lateinit var myLineStringIterator: Iterator<Typed.LineString<InT>>
    private lateinit var myPointIterator: Iterator<Typed.Point<InT>>

    private var myNewLineString: MutableList<Typed.Point<OutT>> = ArrayList()
    private val myNewMultiLineString: MutableList<Typed.LineString<OutT>> = ArrayList()

    private var myHasNext = true
    private lateinit var myResult: Typed.MultiLineString<OutT>

    init {
        try {
            myLineStringIterator = multiLineString.iterator()
            myPointIterator = myLineStringIterator.next().iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): Typed.MultiLineString<OutT> {
        return myResult
    }

    override fun resume() {
        if (!myPointIterator.hasNext()) {
            myNewMultiLineString.add(Typed.LineString(myNewLineString))
            if (!myLineStringIterator.hasNext()) {
                myHasNext = false
                myResult = Typed.MultiLineString(myNewMultiLineString)
                return
            } else {
                myPointIterator = myLineStringIterator.next().iterator()
                myNewLineString = ArrayList()
            }
        }

        myTransform(myPointIterator.next(), myNewLineString)
    }

    override fun alive(): Boolean {
        return myHasNext
    }
}