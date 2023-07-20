/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.spec.conversion.TypedContinuousIdentityMappers
import kotlin.test.Test
import kotlin.test.assertEquals

class TypedContinuousIdentityMappersTest {
    private fun assertColor(expected: Color, value: Double?) {
        assertEquals(expected, TypedContinuousIdentityMappers.COLOR(value))
    }

    @Test
    fun colorValues() {
        assertColor(Color.RED, 0xff0000.toDouble())
        assertColor(Color.GREEN, 0x00ff00.toDouble())
        assertColor(Color.BLUE, 0x0000ff.toDouble())
        assertColor(Color.BLACK, 0.0)
        assertColor(Color.WHITE, 0xffffff.toDouble())
    }

    @Test
    fun colorValuesNeg() {
        assertColor(Color.RED, (-0xff0000).toDouble())
        assertColor(Color.GREEN, (-0x00ff00).toDouble())
        assertColor(Color.BLUE, (-0x0000ff).toDouble())
        assertColor(Color.BLACK, -0.0)
        assertColor(Color.WHITE, (-0xffffff).toDouble())
    }
}