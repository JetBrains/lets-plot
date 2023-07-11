/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
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

}
