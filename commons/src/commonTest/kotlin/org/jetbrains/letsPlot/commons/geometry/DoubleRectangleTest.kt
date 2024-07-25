/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import kotlin.test.Test
import kotlin.test.assertEquals


class DoubleRectangleTest {
    @Test
    fun hashCodeWorks() {
        assertEquals(
            DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO).hashCode(),
            DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO).hashCode()
        )
    }

    @Test
    fun partsOrderIsTopLeftRightBottom() {
        val rect = DoubleRectangle(DoubleVector(1.0, 2.0), DoubleVector(3.0, 4.0))

        val (t, l, r, b) = rect.parts.toList()
        assertEquals(DoubleVector(1.0, 2.0), t.start)
        assertEquals(DoubleVector(4.0, 2.0), t.end)
        assertEquals(DoubleVector(1.0, 2.0), l.start)
        assertEquals(DoubleVector(1.0, 6.0), l.end)
        assertEquals(DoubleVector(4.0, 6.0), r.start)
        assertEquals(DoubleVector(4.0, 2.0), r.end)
        assertEquals(DoubleVector(4.0, 6.0), b.start)
        assertEquals(DoubleVector(1.0, 6.0), b.end)
    }

}
