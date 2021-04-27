/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.config.DataReorderingUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class DataReorderingUtilTest {

    private val data = mapOf(
        "x" to listOf("A", "B", "A", "B", "A", "B"),
        "fill" to listOf(1, 2, 2, 2, 1, 1),
        "color" to listOf("f", "e", "d", "c", "b", "a")
    )


    @Test
    fun `order by x (ascending)`() {
        val orderedDataFrame = reorderDataFrame(
            DataReorderingUtil.OrderOption("x", byVariable = null, orderDir = 1)
        )
        val expected = mapOf(
            "x" to listOf("A", "A", "A", "B", "B", "B"),
            "fill" to listOf(1, 2, 1, 2, 2, 1),
            "color" to listOf("f", "d", "b", "e", "c", "a")
        )
        assertEquals(
            expected,
            DataFrameUtil.toMap(orderedDataFrame)
        )
    }

    @Test
    fun `order by x (descending)`() {
        val orderedDataFrame = reorderDataFrame(
            DataReorderingUtil.OrderOption("x", byVariable = null, orderDir = -1)
        )
        val expected = mapOf(
            "x" to listOf("B", "B", "B", "A", "A", "A"),
            "fill" to listOf(2, 2, 1, 1, 2, 1),
            "color" to listOf("e", "c", "a", "f", "d", "b")
        )
        assertEquals(
            expected,
            DataFrameUtil.toMap(orderedDataFrame)
        )
    }

    @Test
    fun `order x by ascending fill`() {
        val orderedDataFrame = reorderDataFrame(
            DataReorderingUtil.OrderOption("x", byVariable = "fill", orderDir = 1)
        )

        val expected = mapOf(
            "x" to listOf("A", "A", "B", "B", "A", "B"),
            "fill" to listOf(1, 1, 1, 2, 2, 2),
            "color" to listOf("f", "b", "a", "e", "d", "c")
        )
        assertEquals(
            expected,
            DataFrameUtil.toMap(orderedDataFrame)
        )
    }

    @Test
    fun `order x by descending fill`() {
        val orderedDataFrame = reorderDataFrame(
            DataReorderingUtil.OrderOption("x", byVariable = "fill", orderDir = -1)
        )

        val expected = mapOf(
            "x" to listOf("B", "A", "B", "A", "A", "B"),
            "fill" to listOf(2, 2, 2, 1, 1, 1),
            "color" to listOf("e", "d", "c", "f", "b", "a")
        )
        assertEquals(
            expected,
            DataFrameUtil.toMap(orderedDataFrame)
        )
    }

    @Test
    fun `by two fields - ascending x, then ascending fill`() {
        val orderedDataFrame = reorderDataFrame(
            DataReorderingUtil.OrderOption("x", byVariable = "x", orderDir = 1),
            DataReorderingUtil.OrderOption("fill", byVariable = "fill", orderDir = 1)
        )

        val expected = mapOf(
            "x" to listOf("A", "A", "A", "B", "B", "B"),
            "fill" to listOf(1, 1, 2, 1, 2, 2),
            "color" to listOf("f", "b", "d", "a", "e", "c")
        )
        assertEquals(
            expected,
            DataFrameUtil.toMap(orderedDataFrame)
        )
    }

    @Test
    fun `by two fields - ascending x, then descending fill`() {
        val orderedDataFrame = reorderDataFrame(
            DataReorderingUtil.OrderOption("x", byVariable = "x", orderDir = 1),
            DataReorderingUtil.OrderOption("fill", byVariable = "fill", orderDir = -1)
        )

        val expected = mapOf(
            "x" to listOf("A", "A", "A", "B", "B", "B"),
            "fill" to listOf(2, 1, 1, 2, 2, 1),
            "color" to listOf("d", "f", "b", "e", "c", "a")
        )
        assertEquals(
            expected,
            DataFrameUtil.toMap(orderedDataFrame)
        )
    }

    @Test
    fun `by two fields - descending x, then descending fill`() {
        val orderedDataFrame = reorderDataFrame(
            DataReorderingUtil.OrderOption("x", byVariable = "x", orderDir = -1),
            DataReorderingUtil.OrderOption("fill", byVariable = "fill", orderDir = -1)
        )

        val expected = mapOf(
            "x" to listOf("B", "B", "B", "A", "A", "A"),
            "fill" to listOf(2, 2, 1, 2, 1, 1),
            "color" to listOf("e", "c", "a", "d", "f", "b")
        )
        assertEquals(
            expected,
            DataFrameUtil.toMap(orderedDataFrame)
        )
    }

    @Test
    fun `by three fields - x, then fill, then color`() {
        val orderedDataFrame = reorderDataFrame(
            DataReorderingUtil.OrderOption("x", byVariable = "x", orderDir = 1),
            DataReorderingUtil.OrderOption("fill", byVariable = "fill", orderDir = 1),
            DataReorderingUtil.OrderOption("color", byVariable = "color", orderDir = 1)
        )

        val expected = mapOf(
            "x" to listOf("A", "A", "A", "B", "B", "B"),
            "fill" to listOf(1, 1, 2, 1, 2, 2),
            "color" to listOf("b", "f", "d", "a", "c", "e")
        )
        assertEquals(
            expected,
            DataFrameUtil.toMap(orderedDataFrame)
        )
    }

    private val dataFrame = DataFrameUtil.fromMap(data)
    private val varBindings = dataFrame.variables().map { variable ->
        val aes = Aes.values().find { it.name == variable.name }!!
        VarBinding(variable, aes)
    }

    private fun reorderDataFrame(vararg options: DataReorderingUtil.OrderOption): DataFrame {
        return DataReorderingUtil.reorderDataFrame(
            dataFrame,
            varBindings,
            options.asList()
        )
    }
}