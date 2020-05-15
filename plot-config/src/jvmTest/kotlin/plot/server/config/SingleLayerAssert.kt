/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_OSM_ID_COLUMN
import jetbrains.datalore.plot.config.LayerConfig
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GEOMETRIES
import jetbrains.datalore.plot.config.Option.Meta.MapJoin.ID
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import kotlin.test.assertEquals
import kotlin.test.assertNull
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

    fun haveBindings(expectedBindings: Map<Aes<*>, String>): SingleLayerAssert {
        for (aes in expectedBindings.keys) {
            assertBinding(aes, expectedBindings[aes]!!)
        }
        return this
    }

    fun haveDataVector(key: String, value: List<*>): SingleLayerAssert {
        haveDataVectors(mapOf(key to value))
        return this
    }

    fun haveDataVectors(expectedDataVectors: Map<String, List<*>>): SingleLayerAssert {
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

    fun haveTooltipAesList(expectedAesList : List<Aes<*>>?): SingleLayerAssert {
        if (expectedAesList != null) {
            assertTooltipAesListCount(expectedAesList.size)
            for (aes in expectedAesList)
                assertAesTooltip(aes)
        }
        else {
            assertNull(myLayer.tooltipAes)
        }
        return this
    }

    internal fun haveMapVectors(expectedMapVectors: Map<String, List<*>>): SingleLayerAssert {
        Assertions.assertThat(expectedMapVectors).isEqualTo(myLayer[GEO_POSITIONS])
        return this
    }

    internal fun haveMapIds(expectedIds: List<*>): SingleLayerAssert {
        return haveMapValues(ID, expectedIds)
    }

    internal fun haveMapGeometries(expectedGeometries: List<*>): SingleLayerAssert {
        return haveMapValues(GEOMETRIES, expectedGeometries)
    }

    internal fun haveMapGeocode(expectedGeocode: List<*>): SingleLayerAssert {
        return haveMapValues(MAP_OSM_ID_COLUMN, expectedGeocode)
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

    private fun assertAesTooltip(aes: Aes<*>) {
        myLayer.tooltipAes?.contains(aes)?.let { assertTrue(it, "No tooltip for '${aes.name}' aes") }
    }

    private fun assertTooltipAesListCount(expectedCount: Int) {
        assertEquals(expectedCount, myLayer.tooltipAes?.size, "Wrong size of tooltip aes list")
    }

    companion object {
        fun assertThat(layers: List<LayerConfig>): SingleLayerAssert {
            return SingleLayerAssert(layers)
        }
    }
}
