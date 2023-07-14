/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.data

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.calculateArea

class RingAssertion internal constructor(ring: List<DoubleVector>) :
    AbstractAssert<RingAssertion, List<DoubleVector>>(ring, RingAssertion::class.java) {

    val isClosed: RingAssertion
        get() {
            Assertions.assertThat(actual[0]).isEqualTo(actual[actual.size - 1])
            return this
        }

    fun hasSize(expected: Int): RingAssertion {
        Assertions.assertThat(actual).hasSize(expected)
        return this
    }

    fun hasArea(expected: Double): RingAssertion {
        return hasArea(expected, 0.001)
    }

    private fun hasArea(expected: Double, epsilon: Double): RingAssertion {
        Assertions.assertThat(calculateArea(actual)).isEqualTo(expected, Assertions.offset(epsilon))
        return this
    }

    companion object {

        fun assertThatRing(ring: List<DoubleVector>): RingAssertion {
            return RingAssertion(ring)
        }
    }
}
