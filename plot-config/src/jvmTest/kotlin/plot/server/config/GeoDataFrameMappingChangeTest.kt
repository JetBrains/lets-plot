/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_REGION_COLUMN
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Layer.POS
import jetbrains.datalore.plot.config.Option.Layer.STAT
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame
import jetbrains.datalore.plot.config.Option.Meta.GeoReference
import jetbrains.datalore.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.plot.config.Option.Meta.MapJoin
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.server.config.ServerSideTestUtil.createLayerConfigsByLayerSpec
import jetbrains.datalore.plot.server.config.ServerSideTestUtil.geomPolygonSpec
import jetbrains.datalore.plot.server.config.SingleLayerAssert.Companion.assertThat
import kotlin.test.Test

class GeoDataFrameMappingChangeTest {

    @Test
    fun whenDataContainsGeoDataFrame_shouldMoveGeometryColumnToMap() {
        val cfg = createLayerConfigsByLayerSpec(mapOf(
                GEOM to GeomName.POLYGON,
                DATA to MAP_DATA_WITH_IDS,
                DATA_META to GEO_DATA_FRAME_META,
                GEO_POSITIONS to null,
                MAP_DATA_META to null,
                MAPPING to mapOf(Aes.FILL.name to NAME_COLUMN),
                STAT to null,
                POS to null)
        )

        val expectedIdList = listOf("0", "1", "2", "3", "4")

        assertThat(cfg)
                .haveBinding(Aes.MAP_ID, MapJoin.ID)
                .haveDataVectors(mapOf(
                    MapJoin.ID to expectedIdList,
                        NAME_COLUMN to MAP_DATA_WITH_IDS[NAME_COLUMN] as List<*>))
                .haveMapIds(expectedIdList)
                .haveMapGeometries(MAP_DATA_WITH_IDS[GEOMETRY_COLUMN] as List<*>)
    }

    @Test
    fun whenMapContainsGeoDataFrameWithID_shouldKeepIdAndGeometryColumns() {
        val cfg = createLayerConfigsByLayerSpec(
            geomPolygonSpec(MAP_DATA_WITH_IDS, GEO_DATA_FRAME_META)
        )

        assertThat(cfg)
                .haveMapIds(MAP_DATA_REQUESTS)
                .haveMapGeometries(GEOMETRIES)
    }

    @Test
    fun whenMapContainsGeoDataFrameWithoutID_shouldKeepOnlyGeometryColumns() {
        val cfg = createLayerConfigsByLayerSpec(
            geomPolygonSpec(GEO_DATA_FRAME_WITHOUT_IDS, GEO_DATA_FRAME_META)
        )

        assertThat(cfg)
                .haveMapGeometries(GEOMETRIES)
    }

    companion object {
        private const val NAME_COLUMN = "name"
        private const val VALUE_COLUMN = "value"
        private const val GEOMETRY_COLUMN = "coord"
        private val NAMES = listOf("foo", "bar", "qwe", "tmp", "xyz")
        private val VALUES = listOf("254", "313", "142", "89", "3")

        private val DATA_FRAME = mapOf(
            NAME_COLUMN to NAMES,
            VALUE_COLUMN to VALUES
        )

        private val MAP_DATA_REQUESTS = listOf("rq_foo", "rq_bar", "rq_qwe", "rq_tmp", "rq_xyz")
        private val MAP_DATA_REGIONS = listOf("rg_foo", "rg_bar", "rg_qwe", "rg_tmp", "rg_xyz")
        private val MAP_DATA_JOIN_KEYS = listOf("id_foo", "id_bar", "id_qwe", "id_tmp", "id_xyz")
        private val GEOMETRIES = listOf(
            "{\"type: \"Point\", \"coordinates\":[-58, -34]}",
            "{\"type: \"Point\", \"coordinates\":[-47, -15]}",
            "{\"type: \"Point\", \"coordinates\":[-70, -33]}",
            "{\"type: \"Point\", \"coordinates\":[-74, -11]}",
            "{\"type: \"Point\", \"coordinates\":[-66, -17]}"
        )

        private val MAP_DATA_WITH_IDS =
            DATA_FRAME + mapOf(
                GeoReference.REQUEST to MAP_DATA_REQUESTS,
                MAP_REGION_COLUMN to MAP_DATA_REGIONS,
                MapJoin.ID to MAP_DATA_JOIN_KEYS,
                GEOMETRY_COLUMN to GEOMETRIES
        )

        private val GEO_DATA_FRAME_WITHOUT_IDS =
            DATA_FRAME + mapOf(
                GEOMETRY_COLUMN to GEOMETRIES
            )

        private val GEO_DATA_FRAME_META = mapOf(
                GeoDataFrame.TAG to mapOf(
                        GeoDataFrame.GEOMETRY_COLUMN_NAME to GEOMETRY_COLUMN
                )
        )
    }
}