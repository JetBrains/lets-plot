/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.plot.base.DataFrame.OrderingSpec
import jetbrains.datalore.plot.base.stat.Stats
import kotlin.test.Test
import kotlin.test.assertEquals

class DataFrameDistinctValuesTest {
    private val variable = DataFrame.Variable("foo")
    private val orderByVariable = DataFrame.Variable("bar")

    @Test
    fun `check default distinct values`() {
        val df = DataFrame.Builder()
            .put(variable, listOf("B", "A", "C"))
            .put(orderByVariable, listOf(1.0, 2.0, 0.0))
            .build()

        assertDistinctValues(
            df,
            expectedDistinctValues = mapOf(
                variable to listOf("B", "A", "C"),
                orderByVariable to listOf(1.0, 2.0, 0.0)
            )
        )
    }

    @Test
    fun `check ordered distinct values`() {
        val orderSpecs = listOf(
            OrderingSpec(variable, orderByVariable, direction = -1),  // order A descending orderByVariable
            OrderingSpec(orderByVariable, null, direction = 1)        // order B
        )
        val expectedDistinctValues = mapOf(
            variable to listOf("A", "B", "C"),
            orderByVariable to listOf(0.0, 1.0, 2.0)
        )

        // Build dataFrame with ordering specifications
        val df = DataFrame.Builder()
            .put(variable, listOf("B", "A", "C"))
            .put(orderByVariable, listOf(1.0, 2.0, 0.0))
            .addOrderSpecs(orderSpecs)
            .build()

        assertDistinctValues(df, expectedDistinctValues)

        // The correct ordering should be kept after dataframe rebuilding
        assertDistinctValues(df.builder().build(), expectedDistinctValues)
    }

    @Test
    fun `data variable has a null value (which should be skipped)`() {
        val values = listOf("B", null, "A")
        run {
            // Default distinct function will be used
            val df = DataFrame.Builder()
                .put(variable, values)
                .build()
            assertDistinctValues(df, mapOf(variable to listOf("B", "A")))
        }
        run {
            // Add ordering specs
            val df = DataFrame.Builder()
                .put(variable, values)
                .addOrderSpecs(listOf(OrderingSpec(variable, orderBy = null, direction = 1)))
                .build()
            assertDistinctValues(df, mapOf(variable to listOf("A", "B")))
        }
    }

    @Test
    fun `all data values are null`() {
        run {
            // Default
            val df = DataFrame.Builder()
                .put(variable, listOf(null, null))
                .build()
            assertDistinctValues(df, mapOf(variable to emptyList()))
        }
        run {
            // Add ordering specs
            val df = DataFrame.Builder()
                .put(variable, listOf(null, null))
                .addOrderSpecs(listOf(OrderingSpec(variable, orderBy = null, direction = 1)))
                .build()
            assertDistinctValues(df, mapOf(variable to emptyList()))
        }
    }

    @Test
    fun `variable 'orderBy' has a null value - null to the end`() {
        val df = DataFrame.Builder()
            .put(variable, listOf("B", "A", "C", "D"))
            .put(orderByVariable, listOf(1.0, null, 0.0, null))
            .addOrderSpecs(listOf(OrderingSpec(variable, orderByVariable, direction = 1)))
            .build()
        assertDistinctValues(df, mapOf(variable to listOf("C", "B", "A", "D")))
    }

    @Test
    fun `variable 'orderBy' has all null values - default order will be used`() {
        val df = DataFrame.Builder()
            .put(variable, listOf("B", "A", "C"))
            .put(orderByVariable, listOf(null, null, null))
            .addOrderSpecs(listOf(OrderingSpec(variable, orderByVariable, direction = 1)))
            .build()
        assertDistinctValues(df, mapOf(variable to listOf("B", "A", "C")))
    }

    @Test
    fun `empty DataFrame`() {
        run {
            // Default
            val df = DataFrame.Builder()
                .put(variable, listOf<Any>())
                .build()
            assertDistinctValues(df, mapOf(variable to emptyList()))
        }
        run {
            // Add ordering specs
            val df = DataFrame.Builder()
                .put(variable, listOf<Any>())
                .put(orderByVariable, listOf<Any>())
                .addOrderSpecs(listOf(OrderingSpec(variable, orderByVariable, direction = 1)))
                .build()
            assertDistinctValues(df, mapOf(variable to emptyList()))
        }
    }

    @Test
    fun `order by stat count variable`() {
        val df = DataFrame.Builder()
            .put(variable, listOf("B", "A", "B", "C", "A", "A"))
            .put(Stats.COUNT, listOf(0.0, 1.0, 2.0, 1.0, 2.0, 0.0))
            .addOrderSpecs(listOf(OrderingSpec(variable, Stats.COUNT, direction = 1)))
            .build()
        assertDistinctValues(df, mapOf(variable to listOf("C", "B", "A")))
    }

    private fun assertDistinctValues(df: DataFrame, expectedDistinctValues: Map<DataFrame.Variable, List<Any>>) {
        expectedDistinctValues.forEach { (variable, expected) ->
            assertEquals(expected, df.distinctValues(variable).toList())
        }
    }
}