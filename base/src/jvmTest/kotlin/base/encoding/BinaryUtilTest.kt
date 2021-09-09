/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.encoding

import jetbrains.datalore.base.assertion.assertArrayEquals
import kotlin.test.Test
import kotlin.test.assertTrue

class BinaryUtilTest {
    private fun toExpected(l: List<Double?>): List<Double> {
        val result = ArrayList<Double>()
        for (d in l) {
            result.add(d ?: Double.NaN)
        }
        return result
    }

    @Test
    fun encodeEmpty() {
        val s = BinaryUtil.encodeList(emptyList())
        val l1 = BinaryUtil.decodeList(s)
        assertTrue(l1.isEmpty())
    }

    @Test
    fun encodeList() {
        val l = listOf(
                777.77,
                -777.77,
                0.0,
                Double.NaN, null,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.MAX_VALUE,
                Double.MIN_VALUE
        )

        val s = BinaryUtil.encodeList(l)
        val l1 = BinaryUtil.decodeList(s)
        assertArrayEquals(toExpected(l).toTypedArray(), l1.toTypedArray())
    }
}