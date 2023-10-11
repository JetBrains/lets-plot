/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MonolithicCommonTest {
    @Test
    fun checkSpecStandardizing() {
        val plotSpec = MonolithicCommon.processRawSpecs(
            mutableMapOf(
                "kind" to "plot",
                "data" to mapOf(
                    1 to listOf(1, 2, 3),
                    "b" to listOf("5", "6", "7")
                ),
                "layers" to listOf(
                    mapOf(
                        "geom" to "line",
                        "map_join" to null,
                        null to null,
                        "mapping" to mapOf(
                            "x" to "1",
                            "y" to "b"
                        )
                    )
                ),
            ),
            frontendOnly = false
        )

        assertFalse(PlotConfig.isFailure(plotSpec))

        assertTrue("1" in plotSpec.getMap("data")!!, "Int key should be converted to String")

        plotSpec.getMaps("layers")!!.single().let {
            assertTrue("map_join" !in it, "Key with null value should be removed")
            assertTrue(null !in it, "Null key should be removed")
        }
    }

    @Test
    fun ifPlotSpecIsInvalidThenUsefulErrorMessageShouldBeProvided() {
        val plotSpec = MonolithicCommon.processRawSpecs(
            mutableMapOf(
                "kind" to "plot",
                "data" to mapOf(
                    "a" to listOf(1, 2, 3),
                    "b" to listOf(null, null, listOf("300"))
                ),
                "layers" to listOf(
                    mapOf(
                        "geom" to "line",
                        "mapping" to mapOf(
                            "x" to "a",
                            "y" to "b"
                        )
                    )
                )
            ),
            frontendOnly = false
        )

        assertTrue(PlotConfig.isFailure(plotSpec))
        assertContentEquals(
            expected = listOf(
                "All data series in data frame must have equal size",
                "a : 3",
                "b : 1"
            ),
            // Sorted list to make test stable (order of variables in data frame is not guaranteed)
            actual = PlotConfig.getErrorMessage(plotSpec)
                .split("\n")
                .filter(String::isNotEmpty)
                .sorted()
        )
    }
}
