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
            .put(variable,        listOf("B", "A", "B", "C", "A", "A"))
            .put(orderByVariable, listOf(1.0, 0.0, 2.0, 0.0, 1.0, 0.0))
            .build()
        assertDistinctValues(
            df,
            expectedDistinctValues = mapOf(
                variable to listOf("B", "A", "C"),
                orderByVariable to listOf(1.0, 0.0, 2.0)
            )
        )
    }

    @Test
    fun `check ordered distinct values`() {
        fun builder() = DataFrame.Builder()
            .put(variable,        listOf("B", "A", "B", "C", "A", "A"))
            .put(orderByVariable, listOf(1.0, 0.0, 2.0, 0.0, 1.0, 0.0))

        run {
            // Ascending
            val orderSpecs = listOf(
                OrderingSpec(variable, orderBy = null, direction = 1),
                OrderingSpec(orderByVariable, orderBy = null, direction = 1)
            )
            val df = builder()
                .addOrderSpecs(orderSpecs)
                .build()

            val expectedDistinctValues = mapOf(
                variable to listOf("A", "B", "C"),
                orderByVariable to listOf(0.0, 1.0, 2.0)
            )
            assertDistinctValues(df, expectedDistinctValues)
        }
        run {
            // Descending
            val orderSpecs = listOf(
                OrderingSpec(variable, orderBy = null, direction = -1),
                OrderingSpec(orderByVariable, orderBy = null, direction = -1)
            )
            val df = builder()
                .addOrderSpecs(orderSpecs)
                .build()

            val expectedDistinctValues = mapOf(
                variable to listOf("C", "B", "A"),
                orderByVariable to listOf(2.0, 1.0, 0.0)
            )
            assertDistinctValues(df, expectedDistinctValues)
        }
        run {
            // order by ascending orderByVariable
            val df = builder()
                .addOrderSpec(OrderingSpec(variable, orderByVariable, direction = 1))
                .build()

            assertDistinctValues(df, mapOf(
                variable to listOf("A", "C", "B")
            ))
        }
        run {
            // order by descending orderByVariable
            val df = builder()
                .addOrderSpec(OrderingSpec(variable, orderByVariable, direction = -1))
                .build()

            assertDistinctValues(df, mapOf(
                variable to listOf("B", "A", "C")
            ))
        }
    }

    @Test
    fun `correct ordering should be kept after dataframe rebuilding`() {
        val orderSpecs = listOf(
            OrderingSpec(variable, null, direction = 1),
            OrderingSpec(orderByVariable, null, direction = -1)
        )
        // Build dataFrame with ordering specifications
        val df = DataFrame.Builder()
            .put(variable, listOf("B", "A", "B", "C", "A", "A"))
            .put(orderByVariable, listOf(1.0, 0.0, 2.0, 0.0, 1.0, 0.0))
            .addOrderSpecs(orderSpecs)
            .build()

        val expectedDistinctValues = mapOf(
            variable to listOf("A", "B", "C"),
            orderByVariable to listOf(2.0, 1.0, 0.0)
        )
        assertDistinctValues(df, expectedDistinctValues)

        // Rebuild and check ordering
        assertDistinctValues(df.builder().build(), expectedDistinctValues)
    }

    @Test
    fun `data variable has null values (which should be skipped)`() {
        val builder = DataFrame.Builder()
            .put(variable, listOf("B", "A", null, "C", null, "A"))

        run {
            // Default distinct function will be used
            val df = builder.build()
            assertDistinctValues(df, mapOf(variable to listOf("B", "A", "C")))
        }
        run {
            // Add ordering specs
            val df = builder
                .addOrderSpec(OrderingSpec(variable, orderBy = null, direction = 1))
                .build()
            assertDistinctValues(df, mapOf(variable to listOf("A", "B", "C")))
        }
    }

    @Test
    fun `all data values are null`() {
        val builder = DataFrame.Builder()
            .put(variable, listOf(null, null, null))

        run {
            // Default
            val df = builder.build()
            assertDistinctValues(df, mapOf(variable to emptyList()))
        }
        run {
            // Add ordering specs
            val df = builder
                .addOrderSpec(OrderingSpec(variable, orderBy = null, direction = 1))
                .build()
            assertDistinctValues(df, mapOf(variable to emptyList()))
        }
    }

    @Test
    fun `variable 'orderBy' has null values (corresponding values will be at the end)`() {
        fun builder() = DataFrame.Builder()
            .put(variable,        listOf("B",  "A", "B",  "D", "A", "A",  "C"))
            .put(orderByVariable, listOf(1.0, null, 2.0, null, 2.0, null, null))

        run {
            // Ascending
            val df = builder()
                .addOrderSpec(OrderingSpec(variable, orderByVariable, direction = 1))
                .build()
            assertDistinctValues(df, mapOf(variable to listOf("B", "A", "D", "C")))
        }
        run {
            // Descending
            val df = builder()
                .addOrderSpec(OrderingSpec(variable, orderByVariable, direction = -1))
                .build()
            assertDistinctValues(df, mapOf(variable to listOf("A", "B", "C", "D")))
        }
    }

    @Test
    fun `all variable 'orderBy' values are null`() {
        fun builder(): DataFrame.Builder {
            val data = listOf("B", "A", "B", "C", "A", "A")
            return DataFrame.Builder()
                .put(variable, data)
                .put(orderByVariable, List(data.size) { null })
        }

        run {
            // Ascending
            val df = builder()
                .addOrderSpec(OrderingSpec(variable, orderByVariable, direction = 1))
                .build()
            assertDistinctValues(df, mapOf(variable to listOf("B", "A", "C")))
        }
        run {
            // Descending
            val df = builder()
                .addOrderSpec(OrderingSpec(variable, orderByVariable, direction = -1))
                .build()
            assertDistinctValues(df, mapOf(variable to listOf("A", "C", "B")))
        }
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
                .addOrderSpec(OrderingSpec(variable, orderByVariable, direction = 1))
                .build()
            assertDistinctValues(df, mapOf(variable to emptyList()))
        }
    }

    @Test
    fun `order by stat count variable`() {
        val df = DataFrame.Builder()
            .put(variable,    listOf("B", "A", "B", "C", "A", "A"))
            .put(Stats.COUNT, listOf(0.0, 1.0, 2.0, 1.0, 2.0, 0.0))
            .addOrderSpec(OrderingSpec(variable, Stats.COUNT, direction = 1))
            .build()
        assertDistinctValues(df, mapOf(variable to listOf("C", "B", "A")))
    }

    @Test
    fun `few ordering specifications for the variable - choose a more specific`() {
        fun builder() = DataFrame.Builder()
            .put(variable,        listOf("B", "A", "B", "C", "A", "A"))
            .put(orderByVariable, listOf(1.0, 0.0, 2.0, 0.0, 1.0, 0.0))

        val spec1 = OrderingSpec(variable, orderBy = null, direction = 1)
        val spec2 = OrderingSpec(variable, orderByVariable, direction = 1)  // orderBy is specified => more specific

        val expectedDistinctValues = mapOf(variable to listOf("A", "C", "B"))

        run {
            val df = builder()
                .addOrderSpecs(listOf(spec1, spec2))
                .build()
            assertDistinctValues(df, expectedDistinctValues)
        }
        run {
            val df = builder()
                .addOrderSpecs(listOf(spec2, spec1))
                .build()
            assertDistinctValues(df, expectedDistinctValues)
        }
    }

    private fun assertDistinctValues(df: DataFrame, expectedDistinctValues: Map<DataFrame.Variable, List<Any>>) {
        expectedDistinctValues.forEach { (variable, expected) ->
            assertEquals(expected, df.distinctValues(variable).toList())
        }
    }
}