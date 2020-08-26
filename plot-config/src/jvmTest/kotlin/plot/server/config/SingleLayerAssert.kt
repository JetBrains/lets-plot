/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.tooltip.MappedAes
import jetbrains.datalore.plot.builder.tooltip.VariableValue
import jetbrains.datalore.plot.config.LayerConfig
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class SingleLayerAssert private constructor(layers: List<LayerConfig>) :
        AbstractAssert<SingleLayerAssert, List<LayerConfig>>(layers, SingleLayerAssert::class.java) {

    private val myLayer: LayerConfig

    init {
        assertEquals(1, layers.size)
        myLayer = layers[0]
    }

    fun haveBinding(key: Aes<*>, value: String): SingleLayerAssert {
        haveBindings(mapOf(key to value))
        return this
    }

    private fun haveBindings(expectedBindings: Map<Aes<*>, String>): SingleLayerAssert {
        for (aes in expectedBindings.keys) {
            assertBinding(aes, expectedBindings[aes]!!)
        }
        return this
    }

    fun haveDataVector(key: String, value: List<*>): SingleLayerAssert {
        haveDataVectors(mapOf(key to value))
        return this
    }

    private fun haveDataVectors(expectedDataVectors: Map<String, List<*>>): SingleLayerAssert {
        val df = myLayer.combinedData
        val layerData = DataFrameUtil.toMap(df)
        for (`var` in expectedDataVectors.keys) {
            assertTrue(layerData.containsKey(`var`), "No data key '$`var`' found")
            val vector = layerData[`var`]
            val expectedVector = expectedDataVectors[`var`]
            assertEquals(expectedVector, vector)
        }
        return this
    }

    fun haveAesNamesInTooltips(expectedNames: List<String>): SingleLayerAssert {
        return haveSourceNamesInTooltips(expectedNames, getAesNamesInTooltips(), nameType = "aes")
    }

    fun haveVariableNamesInTooltips(expectedNames: List<String>): SingleLayerAssert {
        return haveSourceNamesInTooltips(expectedNames, getVariableNamesInTooltips(), nameType = "variable")
    }

    fun haveNameWithLabelsInTooltips(expectedNameToLabels: Map<String, String>): SingleLayerAssert {
        val actualNameToLabels = getNameToLabelsInTooltips()
        assertEquals(expectedNameToLabels.size, actualNameToLabels.size, "Incorrect number of tooltip lines")
        expectedNameToLabels.forEach { pair ->
            val expectedName = pair.key
            val expectedLabel = pair.value
            val actualLabel = actualNameToLabels[expectedName]
            assertEquals(expectedLabel, actualLabel, "Incorrect label for the source with name '$expectedName'")
        }
        return this
    }

    private fun haveSourceNamesInTooltips(
        expectedNames: List<String>,
        actualNames: List<String>,
        nameType: String = "source"
    ): SingleLayerAssert {
        assertEquals(expectedNames.size, actualNames.size, "Incorrect number of $nameType names used in tooltips")
        expectedNames.forEach { name ->
            assertTrue(name in actualNames, "No tooltip for $nameType with name: '$name'")
        }
        return this
    }

    internal fun haveMapVectors(expectedMapVectors: Map<String, List<*>>): SingleLayerAssert {
        Assertions.assertThat(expectedMapVectors).isEqualTo(myLayer[GEO_POSITIONS])
        return this
    }

    private fun haveMapValues(key: String, expectedMapValues: List<*>): SingleLayerAssert {
        val geoPositions = myLayer[GEO_POSITIONS] as Map<*, *>?
        assertTrue(geoPositions!!.containsKey(key))
        Assertions.assertThat(expectedMapValues).isEqualTo(geoPositions[key])
        return this
    }

    private fun assertBinding(aes: Aes<*>, varName: String) {
        val varBindings = myLayer.varBindings
        for (varBinding in varBindings) {
            if (varBinding.aes == aes) {
                assertEquals(varName, varBinding.variable.name)
                return
            }
        }

        fail("No binding $aes -> $varName")
    }

    private fun getAesNamesInTooltips(): List<String> {
        return myLayer.tooltips
            ?.flatMap { it.data }
            ?.filterIsInstance<MappedAes>()
            ?.map(MappedAes::getAesName)
            ?: emptyList()
    }

    private fun getVariableNamesInTooltips(): List<String> {
        return myLayer.tooltips
            ?.flatMap { it.data }
            ?.filterIsInstance<VariableValue>()
            ?.map(VariableValue::getVariableName)
            ?: emptyList()
    }

    private fun getNameToLabelsInTooltips(): Map<String, String> {
        return myLayer.tooltips
            ?.flatMap { it.data }
            ?.filterIsInstance<MappedAes>()
            ?.associate { it.getAesName() to it.getLabel() }
            ?.plus(
                myLayer.tooltips
                    ?.flatMap { it.data }
                    ?.filterIsInstance<VariableValue>()
                    ?.associate { it.getVariableName() to it.getLabel() }
                    ?: emptyMap()
            )
            ?: emptyMap()
    }

    companion object {
        fun assertThat(layers: List<LayerConfig>): SingleLayerAssert {
            return SingleLayerAssert(layers)
        }
    }
}
