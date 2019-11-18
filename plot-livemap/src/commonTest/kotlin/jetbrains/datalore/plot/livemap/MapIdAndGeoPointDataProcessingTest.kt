/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap


import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode.PIE
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode.POINT
import jetbrains.datalore.plot.config.LiveMapOptionsParser.Companion.parseFromLayerOptions
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.DISPLAY_MODE
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.livemap.ConverterDataHelper.createDefaultMatcher
import jetbrains.datalore.plot.livemap.MapObjectMatcher.Companion.eq
import kotlin.test.Test


class MapIdAndGeoPointDataProcessingTest {

    @Test
    fun whenSingleGeoName() {
        Expectations(
            DataPointKind.SINGLE,
            MapIdDataKind.NAME,
            null
        ).doAssert()
    }

    @Test
    fun whenSingleLonLat() {
        Expectations(
            DataPointKind.SINGLE,
            MapIdDataKind.LONLAT,
            LONLAT_MERCATOR_GEO_COORD
        ).doAssert()
    }

    @Test
    fun whenSingleOsmId() {
        Expectations(
            DataPointKind.SINGLE,
            MapIdDataKind.OSM_ID,
            null
        ).doAssert()
    }

    @Test
    fun multiDataGeoName() {
        Expectations(
            DataPointKind.MULTI,
            MapIdDataKind.NAME,
            null
        ).doAssert()
    }

    @Test
    fun multiDataLonLat() {
        Expectations(
            DataPointKind.MULTI,
            MapIdDataKind.LONLAT,
            LONLAT_MERCATOR_GEO_COORD
        ).doAssert()
    }

    @Test
    fun multiDataOsmId() {
        Expectations(
            DataPointKind.MULTI,
            MapIdDataKind.OSM_ID,
            null
        ).doAssert()
    }

    internal enum class MapIdDataKind {
        NAME,
        LONLAT,
        OSM_ID
    }

    internal enum class DataPointKind {
        SINGLE,
        MULTI
    }

    private class Expectations constructor(
        private val myDataPointKind: DataPointKind,
        private val myMapIdDataKind: MapIdDataKind,
        private val myExpectedPoint: Vec<LonLat>?
    ) {
        private val myMapObjectMatcher = createDefaultMatcher()
            .point(eq(myExpectedPoint))

        internal fun doAssert() {
            createMapObject(
                myDataPointKind,
                myMapIdDataKind
            )?.let {
                myMapObjectMatcher.match(it)
            } ?: if (myExpectedPoint != null) throw AssertionError("Expect:<$myExpectedPoint> but mapObject not created")
        }
    }

    internal class LiveMapDataPointAestheticsProcessorBuilder(
        mapIdDataKind: MapIdDataKind,
        dataPointKind: DataPointKind
    ) {
        private val myOptions = HashMap<String, Any>()
        private val myAes: Aesthetics

        init {
            myAes = initAes(mapIdDataKind)
            processOptions(dataPointKind)
        }

        private fun initAes(mapIdDataKind: MapIdDataKind): Aesthetics {

            val mapId: Any = when (mapIdDataKind) {
                MapIdDataKind.NAME -> TEXAS_GEO_STRING
                MapIdDataKind.LONLAT -> LONLAT_GEO_STRING
                MapIdDataKind.OSM_ID -> OSM_ID_STRING
            }

            return AestheticsBuilder(1)
                .x(constant(1.0))
                .y(constant(1.0))
                .mapId(constant(mapId))
                .build()
        }

        private fun processOptions(dataPointKind: DataPointKind) {
            myOptions[DISPLAY_MODE] = displayModeToDataPointKind(dataPointKind)
        }

        private fun displayModeToDataPointKind(dataPointKind: DataPointKind): DisplayMode {
            return when (dataPointKind) {
                DataPointKind.SINGLE -> POINT
                DataPointKind.MULTI -> PIE
            }
        }

        fun build(): LiveMapDataPointAestheticsProcessor {
            return LiveMapDataPointAestheticsProcessor(
                myAes,
                parseFromLayerOptions(OptionsAccessor(myOptions))
            )
        }
    }

    companion object {
        private const val TEXAS_GEO_STRING = "TEXAS"
        private const val LONLAT_GEO_STRING = "0,0"
        private const val OSM_ID_STRING = "123456"
        private val LONLAT_MERCATOR_GEO_COORD = explicitVec<LonLat>(0.0, 0.0)

        private fun createMapObject(dataPointKind: DataPointKind, mapIdDataKind: MapIdDataKind): MapEntityBuilder? {
            val mapObjects = createProcessorBuilder(
                dataPointKind,
                mapIdDataKind
            )
                .build()
                .mapEntityBuilders

            return if (mapObjects.isNotEmpty()) mapObjects[0] else null
        }

        private fun createProcessorBuilder(
            dataPointKind: DataPointKind, mapIdDataKind: MapIdDataKind
        ): LiveMapDataPointAestheticsProcessorBuilder {
            return LiveMapDataPointAestheticsProcessorBuilder(
                mapIdDataKind,
                dataPointKind
            )
        }
    }
}