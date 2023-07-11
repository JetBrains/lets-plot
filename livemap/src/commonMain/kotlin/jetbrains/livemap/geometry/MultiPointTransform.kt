/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPoint
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import jetbrains.livemap.core.multitasking.MicroTask

internal class MultiPointTransform<InT, OutT>(
    multiPoint: MultiPoint<InT>,
    private val myTransform: (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit
) : MicroTask<MultiPoint<OutT>> {
    private lateinit var myPointIterator: Iterator<Vec<InT>>

    private val myNewMultiPoint = ArrayList<Vec<OutT>>()

    private var myHasNext = true
    private lateinit var myResult: MultiPoint<OutT>

    init {
        try {
            myPointIterator = multiPoint.iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): MultiPoint<OutT> {
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
