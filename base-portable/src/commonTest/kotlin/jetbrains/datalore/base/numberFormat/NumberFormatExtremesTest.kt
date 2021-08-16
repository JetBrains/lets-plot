/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatExtremesTest {
    @Test
    fun typeS() {
        val f = NumberFormat(".3s")
        assertEquals("0.00y", f.apply(Double.MIN_VALUE))
        assertEquals("100000000000000Y", f.apply(1E38))
        assertEquals("0.00y", f.apply(-Double.MIN_VALUE))
        assertEquals("-100000000000000Y", f.apply(-1E38))

        assertEquals("100Y", f.apply(NumberFormat.TYPE_S_MAX))
        assertEquals("-100Y", f.apply(-NumberFormat.TYPE_S_MAX))
    }

    @Test
    fun typeE() {
        val f = NumberFormat(".2e")

        assertEquals("1.00e-323", f.apply(NumberFormat.TYPE_E_MIN))
        assertEquals("-1.00e-323", f.apply(-NumberFormat.TYPE_E_MIN))

        assertEquals("2.00e-323", f.apply(1.9999999E-323))
        assertEquals("-2.00e-323", f.apply(-1.9999999E-323))

        assertEquals("1.80e+308", f.apply(Double.MAX_VALUE))
        assertEquals("-1.80e+308", f.apply(-Double.MAX_VALUE))

        assertEquals("0.00", f.apply(Double.MIN_VALUE))
        assertEquals("0.00", f.apply(-Double.MIN_VALUE))
    }
}