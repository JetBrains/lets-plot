/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.random

import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

class RandomGaussian(val random: Random) {
    private var nextNextGaussian: Double = 0.0
    private var haveNextNextGaussian = false

    // From JDK Random (but not as good)
    fun nextGaussian(): Double {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false
            return nextNextGaussian
        } else {
            var v1: Double
            var v2: Double
            var s: Double
            do {
                v1 = 2 * random.nextDouble() - 1 // between -1 and 1
                v2 = 2 * random.nextDouble() - 1 // between -1 and 1
                s = v1 * v1 + v2 * v2
            } while (s >= 1 || s == 0.0)
            val multiplier = sqrt(-2 * ln(s) / s)
            nextNextGaussian = v2 * multiplier
            haveNextNextGaussian = true
            return v1 * multiplier
        }
    }

    companion object {
        fun normal(count: Int, seed: Long, mean: Double, stdDeviance: Double): List<Double> {
            val r = RandomGaussian(Random(seed))
            return List(count) { r.nextGaussian() * stdDeviance + mean }
        }
    }
}
