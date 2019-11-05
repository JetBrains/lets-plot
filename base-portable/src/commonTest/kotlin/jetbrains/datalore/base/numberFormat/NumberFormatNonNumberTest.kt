/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatNonNumberTest {
    @Test
    fun nonNumber() {
        val f = NumberFormat("d")
        assertEquals("NaN", f.apply(Double.NaN))
        assertEquals("+Infinity", f.apply(Double.POSITIVE_INFINITY))
        assertEquals("-Infinity", f.apply(Double.NEGATIVE_INFINITY))
    }
}