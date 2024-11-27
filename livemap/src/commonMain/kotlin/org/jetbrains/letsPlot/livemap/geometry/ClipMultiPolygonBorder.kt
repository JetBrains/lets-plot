/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.isOnBorder
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask

class ClipMultiPolygonBorder<T>(
    multiPolygon: MultiPolygon<T>,
    private val clipRect: Rect<T>
) : MicroTask<MultiLineString<T>> {
    private lateinit var polygonIterator: Iterator<Polygon<T>>
    private lateinit var ringIterator: Iterator<Ring<T>>
    private lateinit var pointIterator: Iterator<Vec<T>>

    private var newLineString: MutableList<Vec<T>> = ArrayList()
    private val newMultiLineString: MutableList<LineString<T>> = ArrayList()

    private var prevVisible = false
    private var prev: Vec<T>? = null

    private var hasNext = true
    private lateinit var result: MultiLineString<T>

    init {
        try {
            polygonIterator = multiPolygon.iterator()
            ringIterator = polygonIterator.next().iterator()
            pointIterator = ringIterator.next().iterator()
        } catch (e: RuntimeException) {
            println(e)
        }
    }

    override fun resume() {
        if (!pointIterator.hasNext()) {
            if (newLineString.isNotEmpty()) {
                newMultiLineString.add(LineString(newLineString))
            }
            newLineString = mutableListOf()
            prev = null
            prevVisible = false

            if (!ringIterator.hasNext()) {
                if (!polygonIterator.hasNext()) {
                    hasNext = false
                    result = MultiLineString(newMultiLineString)
                    return
                } else {
                    ringIterator = polygonIterator.next().iterator()
                    pointIterator = ringIterator.next().iterator()
                    newLineString = ArrayList()
                }
            } else {
                pointIterator = ringIterator.next().iterator()
                newLineString = ArrayList()
            }
        }
        val currentPoint = pointIterator.next()

        if (currentPoint.isOnBorder(clipRect)) {
            if (prevVisible) {
                newLineString.add(currentPoint)

                newMultiLineString.add(LineString(newLineString))
                newLineString = mutableListOf()
            } else if (prev != null && !onSameEdge(prev!!, currentPoint)) {
                newLineString.add(prev!!)
                newLineString.add(currentPoint)

                newMultiLineString.add(LineString(newLineString))
                newLineString = mutableListOf()
            }

            prev = currentPoint
            prevVisible = false
        } else {
            if (!prevVisible && prev != null) {
                newLineString.add(prev!!)
            }
            newLineString.add(currentPoint)
            prevVisible = true
        }
    }

    private fun onSameEdge(prev: Vec<T>, current: Vec<T>): Boolean {
        return prev.x == current.x || prev.y == current.y
    }

    override fun alive(): Boolean {
        return hasNext
    }

    override fun getResult(): MultiLineString<T> {
        return result
    }
}