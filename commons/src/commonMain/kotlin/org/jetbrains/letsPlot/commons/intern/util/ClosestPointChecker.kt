/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance

class ClosestPointChecker(val target: DoubleVector) {
    var distance = -1.0
        private set
    var coord: DoubleVector? = null
        private set

    constructor(x: Double, y: Double) : this(DoubleVector(x, y))

    fun check(coord: DoubleVector, objectRadius: Double = 0.0): Boolean {
        val cmpResult = compareObject(coord, objectRadius)
        return cmpResult == COMPARISON_RESULT.NEW_CLOSER || cmpResult == COMPARISON_RESULT.EQUAL
    }

    fun compareObject(coord: DoubleVector, objectRadius: Double = 0.0): COMPARISON_RESULT {
        val newDistance = distance(target, coord).takeIf { it > objectRadius } ?: 0.0
        return compare(coord, newDistance)
    }

    private fun compare(coord: DoubleVector, newDistance: Double): COMPARISON_RESULT {
        if (distance < 0) {
            setNewClosestCoord(coord, newDistance)
            return COMPARISON_RESULT.NEW_CLOSER
        }

        if (distance < newDistance) {
            return COMPARISON_RESULT.NEW_FARTHER
        }

        if (distance == newDistance) {
            return COMPARISON_RESULT.EQUAL
        }

        setNewClosestCoord(coord, newDistance)
        return COMPARISON_RESULT.NEW_CLOSER
    }

    private fun setNewClosestCoord(coord: DoubleVector, distance: Double) {
        this.distance = distance
        this.coord = coord
    }

    enum class COMPARISON_RESULT {
        NEW_CLOSER,
        NEW_FARTHER,
        EQUAL
    }
}