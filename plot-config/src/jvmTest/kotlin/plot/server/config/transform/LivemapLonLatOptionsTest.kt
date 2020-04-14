/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import jetbrains.datalore.plot.server.config.SingleLayerAssert.Companion.assertThat
import jetbrains.datalore.plot.server.config.transform.LivemapLonLatOptionsTest.PlotWithLonLatData.Companion.LAT_DATA_KEY
import jetbrains.datalore.plot.server.config.transform.LivemapLonLatOptionsTest.PlotWithLonLatData.Companion.LON_DATA_KEY
import jetbrains.datalore.plot.server.config.transform.LonLatSpecInMappingSpecChange.Companion.GENERATED_LONLAT_COLUMN_NAME
import jetbrains.datalore.plot.server.config.transform.LonLatSpecInMappingSpecChange.Companion.LAT_KEY
import jetbrains.datalore.plot.server.config.transform.LonLatSpecInMappingSpecChange.Companion.LONLAT_SPEC_KEY
import jetbrains.datalore.plot.server.config.transform.LonLatSpecInMappingSpecChange.Companion.LONLAT_SPEC_VALUE
import jetbrains.datalore.plot.server.config.transform.LonLatSpecInMappingSpecChange.Companion.LON_KEY
import kotlin.test.Test
import kotlin.test.assertEquals


class LivemapLonLatOptionsTest {
    @Test
    fun livemapGeoCoordProcessorIntegrationTest() {
        livemapGeoCoordProcessorIntegrationTest(PlotWithLonLatData())
        livemapGeoCoordProcessorIntegrationTest(PlotWithLonLatData.withDataInPlot())
    }

    private fun livemapGeoCoordProcessorIntegrationTest(plotWithLonLatData: PlotWithLonLatData) {
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotWithLonLatData.plotOpts)

        assertThat(layerConfigs)
                .haveBinding(Aes.MAP_ID, GENERATED_LONLAT_COLUMN_NAME)
                .haveDataVector(
                        GENERATED_LONLAT_COLUMN_NAME, plotWithLonLatData.formattedLonLatData
                )
    }

    @Test
    fun shouldDropLonLatDataWithoutMapping() {
        val plotWithLonLatData = PlotWithLonLatData.withoutMapping()
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotWithLonLatData.plotOpts)

        assertEquals(1, layerConfigs[0].getMap(DATA).size.toLong())
        assertThat(layerConfigs)
                .haveBinding(Aes.MAP_ID, GENERATED_LONLAT_COLUMN_NAME)
                .haveDataVector(
                        GENERATED_LONLAT_COLUMN_NAME, plotWithLonLatData.formattedLonLatData
                )
    }


    @Test
    fun shouldNotDropLonLatDataWithMapping() {
        val plotWithLonLatData = PlotWithLonLatData.withMapping(true, true)
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotWithLonLatData.plotOpts)

        assertEquals(3, layerConfigs[0].getMap(DATA).size.toLong())
        assertThat(layerConfigs)
                .haveBindings(mapOf(
                        Aes.MAP_ID to GENERATED_LONLAT_COLUMN_NAME,
                        Aes.FILL to LON_DATA_KEY,
                        Aes.COLOR to LAT_DATA_KEY
                ))
                .haveDataVectors(mapOf(
                        GENERATED_LONLAT_COLUMN_NAME to plotWithLonLatData.formattedLonLatData,
                        LON_DATA_KEY to plotWithLonLatData.lonDataVector,
                        LAT_DATA_KEY to plotWithLonLatData.latDataVector))
    }

    internal class PlotWithLonLatData @JvmOverloads constructor(dataInLayer: Boolean = true) {

        internal val lonDataVector = listOf(10.0, 20.0, 30.0)
        internal val latDataVector = listOf(40.0, 50.0, 60.0)
        internal val plotOpts: MutableMap<String, Any>
        private val mapping: MutableMap<String, Any>
        internal val formattedLonLatData = listOf("10.0, 40.0", "20.0, 50.0", "30.0, 60.0")

        init {
            val data = object : HashMap<String, Any>() {
                init {
                    put(LON_DATA_KEY, lonDataVector)
                    put(LAT_DATA_KEY, latDataVector)
                }
            }

            val lonLatSpecData = mapOf(
                    LONLAT_SPEC_KEY to LONLAT_SPEC_VALUE,
                    LON_KEY to LON_DATA_KEY,
                    LAT_KEY to LAT_DATA_KEY)

            mapping = object : HashMap<String, Any>() {
                init {
                    put(Aes.MAP_ID.name, lonLatSpecData)
                }
            }

            plotOpts = object : HashMap<String, Any>() {
                init {
                    if (!dataInLayer) {
                        put(DATA, data)
                    }
                    put(LAYERS, ArrayList<Any>(listOf(
                            object : HashMap<String, Any>() {
                                init {
                                    put(GEOM, GeomName.LIVE_MAP)
                                    put(MAPPING, mapping)
                                    if (dataInLayer) {
                                        put(DATA, data)
                                    }
                                }
                            }
                    ))
                    )
                }
            }
        }

        companion object {

            const val LON_DATA_KEY = "LON_DATA_KEY"
            const val LAT_DATA_KEY = "LAT_DATA_KEY"

            fun withoutMapping(): PlotWithLonLatData {
                return PlotWithLonLatData()
            }

            fun withDataInPlot(): PlotWithLonLatData {
                return PlotWithLonLatData(false)
            }

            fun withMapping(lonMapping: Boolean, latMapping: Boolean): PlotWithLonLatData {
                val data = PlotWithLonLatData()

                if (lonMapping) {
                    data.mapping[Aes.FILL.name] = LON_DATA_KEY
                }

                if (latMapping) {
                    data.mapping[Aes.COLOR.name] = LAT_DATA_KEY
                }

                return data
            }
        }
    }
}