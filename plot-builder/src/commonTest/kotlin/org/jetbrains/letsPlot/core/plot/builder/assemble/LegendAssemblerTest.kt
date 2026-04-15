/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import kotlin.test.Test
import kotlin.test.assertEquals
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.builder.guide.LegendBreak
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement


class LegendAssemblerTest {
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

    @Test
    fun keySizeMultiplierUsesOnlyOverrideAesValues() {
        val labelsValuesByAes = mapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.WIDTH to (listOf("label1", "label2") to listOf(10, 20)),
            Aes.HEIGHT to (listOf("label1", "label2") to listOf(30, 40))
        )

        val result = createKeySizeMultipliers(
            labelsValuesByAes,
            keyLabels = listOf("label1", "label2"),
            overrideAesValues = emptyMap()
        )

        assertEquals(listOf(1.0 to 1.0, 1.0 to 1.0), result.map { it.x to it.y })
    }

    @Test
    fun keySizeMultiplierSupportsOverrideLists() {
        val labelsValuesByAes = mutableMapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.COLOR to (listOf("label1", "label2", "label3") to listOf("red", "green", "blue"))
        )
        val labelValues = processOverrideAesValues(
            labelsValuesByAes,
            mapOf<Aes<*>, Any>(
                Aes.WIDTH to listOf(0.5, 2.0),
                Aes.HEIGHT to 3.0
            )
        )

        val result = createKeySizeMultipliers(
            labelsValuesByAes,
            labelValues.first,
            mapOf<Aes<*>, Any>(
                Aes.WIDTH to listOf(0.5, 2.0),
                Aes.HEIGHT to 3.0
            )
        )

        assertEquals(listOf(0.5 to 3.0, 2.0 to 3.0, 2.0 to 3.0), result.map { it.x to it.y })
    }

    @Test
    fun keySizeMultiplierTreatsNullOverrideAsNoResize() {
        val labelsValuesByAes = mutableMapOf<Aes<*>, Pair<List<String>, List<Any?>>>(
            Aes.WIDTH to (listOf("label1", "label2", "label3") to listOf(10.0, 20.0, 30.0))
        )
        val labelValues = processOverrideAesValues(
            labelsValuesByAes,
            mapOf<Aes<*>, Any>(Aes.WIDTH to listOf(0.5, null, 2.0))
        )

        val result = createKeySizeMultipliers(
            labelsValuesByAes,
            labelValues.first,
            mapOf<Aes<*>, Any>(Aes.WIDTH to listOf(0.5, null, 2.0))
        )

        assertEquals(listOf(0.5 to 1.0, 1.0 to 1.0, 2.0 to 1.0), result.map { it.x to it.y })
    }

    @Test
    fun customKeySizeMultiplierUsesOnlyOverrideAesValues() {
        val result = keySizeMultiplier(
            mapOf<Aes<*>, Any>(
                Aes.WIDTH to 0.25,
                Aes.HEIGHT to 4.0,
                Aes.SIZE to 5.0
            )
        )

        assertEquals(0.25 to 4.0, result.x to result.y)
    }

    @Test
    fun preferredKeySizeCanShrinkBelowMinimumKeySize() {
        val legendBreak = LegendBreak("label")
        legendBreak.addLayer(
            FAKE_DATA_POINT,
            legendKeyElementFactory(
                minimumKeySize = DoubleVector(100.0, 100.0),
                supportsKeySizeMultiplier = true
            ),
            keySizeMultiplier = DoubleVector(0.5, 0.25)
        )

        assertEquals(DoubleVector(10.0, 5.0), legendBreak.preferredKeySize(DoubleVector(20.0, 20.0)))
    }

    @Test
    fun preferredKeySizeKeepsMinimumKeySizeWhenNotShrunk() {
        val legendBreak = LegendBreak("label")
        legendBreak.addLayer(
            FAKE_DATA_POINT,
            legendKeyElementFactory(
                minimumKeySize = DoubleVector(31.0, 31.0),
                supportsKeySizeMultiplier = true
            ),
            keySizeMultiplier = DoubleVector(2.0, 1.0)
        )

        assertEquals(DoubleVector(40.0, 32.0), legendBreak.preferredKeySize(DoubleVector(20.0, 20.0)))
    }

    @Test
    fun preferredKeySizeIgnoresMultiplierForUnsupportedKeyFactory() {
        val legendBreak = LegendBreak("label")
        legendBreak.addLayer(
            FAKE_DATA_POINT,
            legendKeyElementFactory(
                minimumKeySize = DoubleVector(31.0, 31.0),
                supportsKeySizeMultiplier = false
            ),
            keySizeMultiplier = DoubleVector(3.0, 0.25)
        )

        assertEquals(DoubleVector(32.0, 32.0), legendBreak.preferredKeySize(DoubleVector(20.0, 20.0)))
    }

    private fun legendKeyElementFactory(
        minimumKeySize: DoubleVector,
        supportsKeySizeMultiplier: Boolean
    ): LegendKeyElementFactory {
        return object : LegendKeyElementFactory {
            override val supportsKeySizeMultiplier: Boolean
                get() = supportsKeySizeMultiplier

            override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
                return SvgGElement()
            }

            override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
                return minimumKeySize
            }
        }
    }

    private companion object {
        val FAKE_DATA_POINT = object : DataPointAesthetics() {
            override fun index(): Int = 0

            override fun group(): Int? = null

            override fun <T> get(aes: Aes<T>): T? = null

            override val colorAes: Aes<Color> = Aes.COLOR

            override val fillAes: Aes<Color> = Aes.FILL
        }
    }
}
