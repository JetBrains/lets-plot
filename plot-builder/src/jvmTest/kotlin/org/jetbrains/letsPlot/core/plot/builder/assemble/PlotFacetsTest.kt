/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets.Companion.varNameAndLevelPairsByTile
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlotFacetsTest {
    @Test
    fun dataByLevelTuple() {
        val b = DataFrame.Builder()
        b.putNumeric(
            DataFrame.Variable("A"),
            listOf(0.0, 0.0, 1.0, 1.0, 2.0, 2.0)
        )
        b.put(
            DataFrame.Variable("B"),
            listOf("one", "one", "one", "two", "two", "two")
        )

        val data = b.build()

        // "A" levels as column labels
        run {
            val varNameAndLevelPairsByTile: List<List<Pair<String, Any>>> = varNameAndLevelPairsByTile(
                varNames = listOf("A"),
                varLevels = listOf(listOf(0.0, 1.0, 2.0))
            )
            val dataByLevelA = PlotFacets.levelTupleAndDataPairs(data, varNameAndLevelPairsByTile)
                .toMap()

            assertEquals(3, dataByLevelA.size)

            val extectedKeys = listOf(
                listOf(0.0),
                listOf(1.0),
                listOf(2.0),
            )
            for (extectedKey in extectedKeys) {
                assert(dataByLevelA.containsKey(extectedKey)) { "Key missing: $extectedKey" }
            }

            val expectedData = listOf(
                mapOf(
                    "A" to listOf(0.0, 0.0),
                    "B" to listOf("one", "one"),
                ),
                mapOf(
                    "A" to listOf(1.0, 1.0),
                    "B" to listOf("one", "two"),
                ),
                mapOf(
                    "A" to listOf(2.0, 2.0),
                    "B" to listOf("two", "two"),
                ),
            )

            for ((i, extectedKey) in extectedKeys.withIndex()) {
                assertEquals(expectedData[i], DataFrameUtil.toMap(dataByLevelA.getValue(extectedKey)))
            }
        }

        // "A" levels as column labels
        // "B" levels as row labels
        run {
            val varNameAndLevelPairsByTile: List<List<Pair<String, Any>>> = varNameAndLevelPairsByTile(
                varNames = listOf("A", "B"),
                varLevels = listOf(
                    listOf(0.0, 1.0, 2.0),
                    listOf("one", "two"),
                )
            )
            val dataByLevelAB = PlotFacets.levelTupleAndDataPairs(
                data,
                varNameAndLevelPairsByTile,
            ).toMap()

            assertEquals(6, dataByLevelAB.size)

            val extectedKeys = listOf(
                listOf(0.0, "one"),
                listOf(0.0, "two"),
                listOf(1.0, "one"),
                listOf(1.0, "two"),
                listOf(2.0, "one"),
                listOf(2.0, "two"),
            )
            for (extectedKey in extectedKeys) {
                assert(dataByLevelAB.containsKey(extectedKey)) { "Key: $extectedKey" }
            }


//            listOf(0.0,   0.0,    1.0,   1.0,   2.0,   2.0)
//            listOf("one", "one", "one", "two", "two", "two")

            val expectedData = listOf(
                // (0.0, "one")
                mapOf(
                    "A" to listOf(0.0, 0.0),
                    "B" to listOf("one", "one"),
                ),
                // (0.0, "two")
                mapOf(
                    "A" to listOf(),
                    "B" to listOf(),
                ),
                //(1.0, "one")
                mapOf(
                    "A" to listOf(1.0),
                    "B" to listOf("one"),
                ),
                //(1.0, "two")
                mapOf(
                    "A" to listOf(1.0),
                    "B" to listOf("two"),
                ),
                //(2.0, "one")
                mapOf(
                    "A" to listOf(),
                    "B" to listOf(),
                ),
                //(2.0, "two")
                mapOf(
                    "A" to listOf(2.0, 2.0),
                    "B" to listOf("two", "two"),
                ),
            )

            for ((i, extectedKey) in extectedKeys.withIndex()) {
                assertEquals(expectedData[i], DataFrameUtil.toMap(dataByLevelAB.getValue(extectedKey)))
            }
        }


    }

}