/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transform

import demoAndTestShared.assertDoesNotFail
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlotSpecTransformToMutableTest {
    @Test
    fun toMutable() {
        val im = mutableMapOf(
                "a" to 0,
                "b" to listOf(0, 0),
                "c" to mapOf("c_" to 0),
                "d" to listOf(mapOf("d0_" to 0))
        )


        assertEquals(0, ((im["d"] as List<*>)[0] as Map<*, *>)["d0_"])

        val mm = PlotSpecTransform.builderForRawSpec().build().apply(im)

        assertEquals(0, mm["a"])

        assertDoesNotFail { mm["a"] = 1 }

        assertEquals(2, (mm["b"] as List<*>).size)

        run {
            @Suppress("UNCHECKED_CAST")
            val list = mm["b"] as MutableList<Any>
            // maps become mutable but lists do not!
            assertFailsWith(UnsupportedOperationException::class) {
                list.add(1)
            }
        }

        assertEquals(0, (mm["c"] as Map<*, *>)["c_"])
        assertDoesNotFail {
            @Suppress("UNCHECKED_CAST")
            val map = mm["c"] as MutableMap<Any, Any>
            map["c_"] = 1
        }

        assertEquals(0, ((mm["d"] as List<*>)[0] as Map<*, *>)["d0_"])
        assertDoesNotFail {
            @Suppress("UNCHECKED_CAST")
            val map = (mm["d"] as List<*>)[0] as MutableMap<Any, Any>
            map["do_"] = 1
        }
    }
}