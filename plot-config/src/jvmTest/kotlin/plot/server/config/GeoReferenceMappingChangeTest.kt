/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GEOCODING_OSM_ID_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GEOCODING_REQUEST_COLUMN
import jetbrains.datalore.plot.config.Option.Meta.GeoReference
import jetbrains.datalore.plot.server.config.ServerSideTestUtil.createLayerConfigsByLayerSpec
import jetbrains.datalore.plot.server.config.ServerSideTestUtil.geomPolygonSpec
import jetbrains.datalore.plot.server.config.SingleLayerAssert.Companion.assertThat
import kotlin.test.Test

class GeoReferenceMappingChangeTest {

    @Test
    fun whenMapContainsGeoReference_shouldKeepIdAndGeocodeColumns() {
        val cfg = createLayerConfigsByLayerSpec(geomPolygonSpec(GEO_REFERENCE, GEO_REFERENCE_META))

        assertThat(cfg)
                .haveMapIds(GEO_REFERENCE[GEOCODING_REQUEST_COLUMN] as List<*>)
                .haveMapGeocode(GEO_REFERENCE[GEOCODING_OSM_ID_COLUMN] as List<*>)
    }

    companion object {
        private val GEO_REFERENCE = mapOf(
                GEOCODING_REQUEST_COLUMN to listOf("foo", "bar", "xyz"),
                GEOCODING_OSM_ID_COLUMN to listOf("123", "42", "27"),
                "found name" to listOf("Foo", "Bar", "xyz"),
                "highlights" to listOf(
                        listOf("foo baz"),
                        listOf("baz bar"),
                        listOf("baz xyz baz"))
        )

        private val GEO_REFERENCE_META = mapOf(
                GeoReference.TAG to mapOf<String, Any>()
        )
    }
}