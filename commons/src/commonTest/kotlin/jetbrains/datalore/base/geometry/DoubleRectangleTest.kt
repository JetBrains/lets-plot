/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.geometry

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
