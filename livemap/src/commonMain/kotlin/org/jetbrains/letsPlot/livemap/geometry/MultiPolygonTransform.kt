/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Ring
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask

internal class MultiPolygonTransform<InT, OutT>(
    multiPolygon: MultiPolygon<InT>,
    private val myTransform: (Vec<InT>, MutableCollection<Vec<OutT>>) -> Unit
) : MicroTask<MultiPolygon<OutT>> {
    private lateinit var myPolygonsIterator: Iterator<Polygon<InT>>
    private lateinit var myRingIterator: Iterator<Ring<InT>>
    private lateinit var myPointIterator: Iterator<Vec<InT>>

    private var myNewRing: MutableList<Vec<OutT>> = ArrayList()
    private var myNewPolygon: MutableList<Ring<OutT>> = ArrayList()
    private val myNewMultiPolygon = ArrayList<Polygon<OutT>>()

    private var myHasNext = true
    private lateinit var myResult: MultiPolygon<OutT>

    init {
        try {
            myPolygonsIterator = multiPolygon.iterator()
            myRingIterator = myPolygonsIterator.next().iterator()
            myPointIterator = myRingIterator.next().iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun getResult(): MultiPolygon<OutT> {
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