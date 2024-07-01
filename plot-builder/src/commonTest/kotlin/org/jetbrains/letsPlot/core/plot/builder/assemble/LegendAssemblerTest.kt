/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import kotlin.test.Test
import kotlin.test.assertEquals
import org.jetbrains.letsPlot.core.plot.base.Aes


internal class LegendLayerTest {
    @Test
    fun testProcessOverrideAesValuesWithNoOverrideValues() {
        val labelsValuesByAes = mutableMapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.WIDTH to (listOf("label1", "label2", "label3") to listOf(1, 2, 3)),
            Aes.HEIGHT to (listOf("label1", "label2", "label3") to listOf(4, 5, 6)),
        )
        val overrideAesValues = emptyMap<Aes<*>, Any>()

        val expectedLabels = listOf("label1", "label2", "label3")
        val expectedMapsByLabel = listOf(
            mapOf(Aes.WIDTH to 1, Aes.HEIGHT to 4),
            mapOf(Aes.WIDTH to 2, Aes.HEIGHT to 5),
            mapOf(Aes.WIDTH to 3, Aes.HEIGHT to 6)
        )

        val result = processOverrideAesValues(labelsValuesByAes, overrideAesValues)

        assertEquals(expectedLabels, result.first)
        assertEquals<List<Map<out Aes<*>, Any>>>(expectedMapsByLabel, result.second)
    }

    @Test
    fun testProcessOverrideAesValuesWithEmptyOverrideValues() {
        val labelsValuesByAes = mutableMapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.WIDTH to (listOf("label1", "label2", "label3") to listOf(1, 2, 3)),
            Aes.HEIGHT to (listOf("label1", "label2", "label3") to listOf(4, 5, 6)),
        )
        val overrideAesValues = mapOf<Aes<*>, Any>(
            Aes.HEIGHT to emptyList<Int>(),
            Aes.COLOR to emptyList<String>()
        )

        val expectedLabels = listOf("label1", "label2", "label3")
        val expectedMapsByLabel = listOf(
            mapOf(Aes.WIDTH to 1, Aes.HEIGHT to 4),
            mapOf(Aes.WIDTH to 2, Aes.HEIGHT to 5),
            mapOf(Aes.WIDTH to 3, Aes.HEIGHT to 6)
        )

        val result = processOverrideAesValues(labelsValuesByAes, overrideAesValues)

        assertEquals(expectedLabels, result.first)
        assertEquals<List<Map<out Aes<*>, Any>>>(expectedMapsByLabel, result.second)
    }

    @Test
    fun testProcessOverrideAesValuesWithASingleValue() {
        val labelsValuesByAes = mutableMapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.WIDTH to (listOf("label1", "label2", "label3") to listOf(1, 2, 3)),
            Aes.HEIGHT to (listOf("label1", "label2", "label3") to listOf(4, 5, 6)),
        )
        val overrideAesValues = mapOf<Aes<*>, Any>(
            Aes.WIDTH to 7,
            Aes.COLOR to "red"
        )

        val expectedLabels = listOf("label1", "label2", "label3")
        val expectedMapsByLabel = listOf(
            mapOf(Aes.WIDTH to 7, Aes.HEIGHT to 4, Aes.COLOR to "red"),
            mapOf(Aes.WIDTH to 7, Aes.HEIGHT to 5, Aes.COLOR to "red"),
            mapOf(Aes.WIDTH to 7, Aes.HEIGHT to 6, Aes.COLOR to "red")
        )

        val result = processOverrideAesValues(labelsValuesByAes, overrideAesValues)

        assertEquals(expectedLabels, result.first)
        assertEquals<List<Map<out Aes<*>, Any>>>(expectedMapsByLabel, result.second)
    }


    @Test
    fun testProcessOverrideAesValuesWithShortValueList() {
        val labelsValuesByAes = mutableMapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.WIDTH to (listOf("label1", "label2", "label3") to listOf(1, 2, 3)),
            Aes.HEIGHT to (listOf("label1", "label2", "label3") to listOf(4, 5, 6)),
        )
        val overrideAesValues = mapOf<Aes<*>, Any>(
            Aes.WIDTH to listOf(7, 8),
            Aes.HEIGHT to listOf(null),
            Aes.COLOR to listOf(null)
        )

        val expectedLabels = listOf("label1", "label2", "label3")
        val expectedMapsByLabel = listOf(
            mapOf(Aes.WIDTH to 7, Aes.HEIGHT to 4),
            mapOf(Aes.WIDTH to 8, Aes.HEIGHT to 5),
            mapOf(Aes.WIDTH to 8, Aes.HEIGHT to 6)
        )

        val result = processOverrideAesValues(labelsValuesByAes, overrideAesValues)

        assertEquals(expectedLabels, result.first)
        assertEquals<List<Map<out Aes<*>, Any>>>(expectedMapsByLabel, result.second)
    }

    @Test
    fun testProcessOverrideAesValuesWithNulls() {
        val labelsValuesByAes = mutableMapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.WIDTH to (listOf("label1", "label2", "label3") to listOf(1, 2, 3)),
            Aes.HEIGHT to (listOf("label1", "label2", "label3") to listOf(4, 5, 6)),
        )
        val overrideAesValues = mapOf<Aes<*>, Any>(
            Aes.WIDTH to listOf(7, null, 8),
            Aes.HEIGHT to listOf(9, null)
        )

        val expectedLabels = listOf("label1", "label2", "label3")
        val expectedMapsByLabel = listOf(
            mapOf(Aes.WIDTH to 7, Aes.HEIGHT to 9),
            mapOf(Aes.WIDTH to 2, Aes.HEIGHT to 5),
            mapOf(Aes.WIDTH to 8, Aes.HEIGHT to 6)
        )

        val result = processOverrideAesValues(labelsValuesByAes, overrideAesValues)

        assertEquals(expectedLabels, result.first)
        assertEquals<List<Map<out Aes<*>, Any>>>(expectedMapsByLabel, result.second)
    }

    @Test
    fun testProcessOverrideAesValuesWithMixedLabels() {
        val labelsValuesByAes = mutableMapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.WIDTH to (listOf("label1", "label2", "label3") to listOf(1, 2, 3)),
            Aes.HEIGHT to (listOf("label2", "label1", "label3", "label4") to listOf(4, 5, 6, 7)),
        )
        val overrideAesValues = mapOf<Aes<*>, Any>(
            Aes.HEIGHT to listOf(8, 9, 10, 11),
        )

        val expectedLabels = listOf("label1", "label2", "label3", "label4")
        val expectedMapsByLabel = listOf(
            mapOf(Aes.WIDTH to 1, Aes.HEIGHT to 9),
            mapOf(Aes.WIDTH to 2, Aes.HEIGHT to 8),
            mapOf(Aes.WIDTH to 3, Aes.HEIGHT to 10),
            mapOf(Aes.HEIGHT to 11)
        )

        val result = processOverrideAesValues(labelsValuesByAes, overrideAesValues)

        assertEquals(expectedLabels, result.first)
        assertEquals<List<Map<out Aes<*>, Any>>>(expectedMapsByLabel, result.second)
    }
}
