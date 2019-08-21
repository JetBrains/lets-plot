package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.LineString
import jetbrains.datalore.base.projectionGeometry.MultiLineString
import jetbrains.livemap.core.multitasking.MicroTask

internal class MultiLineStringTransform(
    multiLineString: MultiLineString,
    private val myTransform: (DoubleVector, MutableCollection<DoubleVector>) -> Unit
) : MicroTask<MultiLineString> {
    private lateinit var myLineStringIterator: Iterator<LineString>
    private lateinit var myPointIterator: Iterator<DoubleVector>

    private var myNewLineString: MutableList<DoubleVector> = ArrayList()
    private val myNewMultiLineString: MutableList<LineString> = ArrayList()

    private var myHasNext = true
    private lateinit var myResult: MultiLineString

    init {
        try {
            myLineStringIterator = multiLineString.iterator()
            myPointIterator = myLineStringIterator.next().iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): MultiLineString {
        return myResult
    }

    override fun resume() {
        if (!myPointIterator.hasNext()) {
            myNewMultiLineString.add(LineString(myNewLineString))
            if (!myLineStringIterator.hasNext()) {
                myHasNext = false
                myResult = MultiLineString(myNewMultiLineString)
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