/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import kotlin.test.*

class IsAesDiscreteTest {
    private val  data = mapOf(
        "code" to listOf("a", "b", "c"),
        "code_num" to listOf(4.0, 5.0, 6.0),
        "value" to listOf(1.0, -5.0, 6.0)
    )

    private val dataInv = mapOf(
        "code" to listOf(2.0, 3.0, 7.0),
        "value" to listOf("f", "g", "h")
    )

    @Test
    fun `data and mapping in plot`() {
        val plotData = DataFrameUtil.fromMap(data)
        val layerData = DataFrameUtil.fromMap(emptyMap<String, String>())
        val plotMapping = mapOf(
            "x" to "code",
            "y" to "value"
        )
        val layerMapping = emptyMap<String, String>()
        val asDiscreteMapping = emptyMap<String, String>()

        val isXDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.X,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )
        val isYDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.Y,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )

        assertTrue(isXDiscrete)
        assertFalse(isYDiscrete)
    }

    @Test
    fun `data and mapping in layer`(){
        val plotData = DataFrameUtil.fromMap(emptyMap<String, String>())
        val layerData = DataFrameUtil.fromMap(data)
        val plotMapping = emptyMap<String, String>()
        val layerMapping = mapOf(
            "x" to "code",
            "y" to "value"
        )
        val asDiscreteMapping = emptyMap<String, String>()

        val isXDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.X,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )
        val isYDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.Y,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )

        assertTrue(isXDiscrete)
        assertFalse(isYDiscrete)
    }

    @Test
    fun `data in plot but mapping in layer`(){
        val plotData = DataFrameUtil.fromMap(data)
        val layerData = DataFrameUtil.fromMap(emptyMap<String, String>())
        val plotMapping = emptyMap<String, String>()
        val layerMapping = mapOf(
            "x" to "code",
            "y" to "value"
        )
        val asDiscreteMapping = emptyMap<String, String>()

        val isXDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.X,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )
        val isYDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.Y,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )

        assertTrue(isXDiscrete)
        assertFalse(isYDiscrete)
    }


     @Test
     fun `aes use as_discrete()`() {
         val plotData = DataFrameUtil.fromMap(data)
         val layerData = DataFrameUtil.fromMap(emptyMap<String, String>())
         val plotMapping = mapOf(
             "x" to "code",
             "y" to "value"
         )
         val layerMapping = emptyMap<String, String>()
         val asDiscreteMapping = mapOf(
             "x" to "code",
             "y" to "value"
         )

         val isXDiscrete = DataConfigUtil.isAesDiscrete(
             Aes.X,
             plotData,
             layerData,
             plotMapping,
             layerMapping,
             asDiscreteMapping
         )
         val isYDiscrete = DataConfigUtil.isAesDiscrete(
             Aes.Y,
             plotData,
             layerData,
             plotMapping,
             layerMapping,
             asDiscreteMapping
         )

         assertTrue(isXDiscrete)
         assertTrue(isYDiscrete)
     }

    @Test
    fun `data and mapping in plot with layer`() {
        val plotData = DataFrameUtil.fromMap(data)
        val layerData = DataFrameUtil.fromMap(emptyMap<String, String>())
        val plotMapping = mapOf(
            "x" to "code",
            "y" to "value"
        )
        val layerMapping = mapOf(
            "x" to "code_num"
        )
        val asDiscreteMapping = emptyMap<String, String>()

        val isXDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.X,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )
        assertFalse(isXDiscrete)
    }

    @Test
    fun `different data in plot and layer`() {
        val plotData = DataFrameUtil.fromMap(data)
        val layerData = DataFrameUtil.fromMap(dataInv)
        val plotMapping = mapOf(
            "x" to "code",
            "y" to "value"
        )
        val layerMapping = mapOf(
            "x" to "code",
            "y" to "value"
        )
        val asDiscreteMapping = emptyMap<String, String>()

        val isXDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.X,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )
        val isYDiscrete = DataConfigUtil.isAesDiscrete(
            Aes.Y,
            plotData,
            layerData,
            plotMapping,
            layerMapping,
            asDiscreteMapping
        )

        assertFalse(isXDiscrete)
        assertTrue(isYDiscrete)
    }


}