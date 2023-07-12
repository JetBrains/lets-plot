/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.config.DataMetaUtil
import jetbrains.datalore.plot.config.LayerConfig
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import kotlin.test.*

class SingleLayerAssert private constructor(layers: List<LayerConfig>) :
    AbstractAssert<SingleLayerAssert, List<LayerConfig>>(layers, SingleLayerAssert::class.java) {

    private val myLayer: LayerConfig

    init {
        assertEquals(1, layers.size, "No plot layers!")
        myLayer = layers[0]
    }

    fun haveBinding(key: org.jetbrains.letsPlot.core.plot.base.Aes<*>, value: String): SingleLayerAssert {
        haveBindings(mapOf(key to value))
        return this
    }

    private fun haveBindings(expectedBindings: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, String>): SingleLayerAssert {
        for (aes in expectedBindings.keys) {
            assertBinding(aes, expectedBindings[aes]!!)
        }
        return this
    }

    private fun assertBinding(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>, varName: String) {
        val varBindings = myLayer.varBindings
        for (varBinding in varBindings) {
            if (varBinding.aes == aes) {
                assertEquals(varName, varBinding.variable.name)
                return
            }
        }

        fail("No binding $aes -> $varName")
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

    fun hasDataMetaFacetLevels(variable: String, levels: List<Any>): SingleLayerAssert {
        val layerDataMeta = myLayer.getMap(DATA_META)
        val factorLevelsByVariable = DataMetaUtil.getFactorLevelsByVariable(layerDataMeta)
        assertTrue(factorLevelsByVariable.containsKey(variable), "'$variable' - 'factor levels' annotation not found.")

        val factorLevels = factorLevelsByVariable.getValue(variable)
        assertEquals(
            expected = levels,
            actual = factorLevels
        )
        return this
    }

    fun noDataMetaFacetLevels(variable: String): SingleLayerAssert {
        val layerDataMeta = myLayer.getMap(DATA_META)
        val factorLevelsByVariable = DataMetaUtil.getFactorLevelsByVariable(layerDataMeta)
        assertFalse(
            factorLevelsByVariable.containsKey(variable),
            "'$variable' - unexpected 'factor levels' annotation found."
        )
        return this
    }

    fun hasDataMetaDateTime(variable: String): SingleLayerAssert {
        val layerDataMeta = myLayer.getMap(DATA_META)
        val dateTimeVariables = DataMetaUtil.getDateTimeColumns(layerDataMeta)
        assertTrue(dateTimeVariables.contains(variable), "'$variable' - 'date-time' annotation not found.")
        return this
    }

    fun hasDataMetaAesAsDiscrete(aes: String): SingleLayerAssert {
        val layerDataMeta = myLayer.getMap(DATA_META)
        val asDiscreteAesSet = DataMetaUtil.getAsDiscreteAesSet(layerDataMeta)
        assertTrue(asDiscreteAesSet.contains(aes), "'aes $aes' - 'as_discrete' annotation not found.")
        return this
    }

    fun hasDataMetaAesOrderOption(aes: String): SingleLayerAssert {
        val commonMappings = org.jetbrains.letsPlot.core.plot.base.Aes.values().associate {
            it.name to "${it.name} dummy var"
        }

        val orderOptionsList = DataMetaUtil.getOrderOptions(
            myLayer.toMap(),
            commonMappings,
            isClientSide = false
        )

        // Extra checks just to make sure that our assumptions about 'as discrete' annotation didn't change:
        //      If aes is 'as discrete' then the ordering is guaranteed (the mapped var is ordered)
        val orderOption = orderOptionsList.find {
            it.variableName == commonMappings.getValue(aes)
        }
        assertNotNull(orderOption)
        assertTrue(orderOption.getOrderDir() != 0)
        return this
    }

    companion object {
        fun assertThat(layers: List<LayerConfig>): SingleLayerAssert {
            return SingleLayerAssert(layers)
        }
    }
}
