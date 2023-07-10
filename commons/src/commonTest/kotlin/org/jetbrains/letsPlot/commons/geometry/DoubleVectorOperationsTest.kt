/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertTrue

class DoubleVectorOperationsTest {

    @Test
    fun simpleRotation() {
        val v = DoubleVector(1.0, 0.0)

        val delta = DoubleVector(0.0, 1.0).subtract(v.rotate(PI / 2))
        assertTrue(delta.length() < EPS)
    }

    companion object {
        private val EPS = 0.0001
    }
}