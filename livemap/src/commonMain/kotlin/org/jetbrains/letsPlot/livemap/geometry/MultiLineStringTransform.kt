/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiLineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask

internal class MultiLineStringTransform<InT, OutT> (
    multiLineString: MultiLineString<InT>,
    private val myTransform: (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit
) : MicroTask<MultiLineString<OutT>> {
    private lateinit var myLineStringIterator: Iterator<LineString<InT>>
    private lateinit var myPointIterator: Iterator<Vec<InT>>

    private var myNewLineString: MutableList<Vec<OutT>> = ArrayList()
    private val myNewMultiLineString: MutableList<LineString<OutT>> = ArrayList()

    private var myHasNext = true
    private lateinit var myResult: MultiLineString<OutT>

    init {
        try {
            myLineStringIterator = multiLineString.iterator()
            myPointIterator = myLineStringIterator.next().iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): MultiLineString<OutT> {
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