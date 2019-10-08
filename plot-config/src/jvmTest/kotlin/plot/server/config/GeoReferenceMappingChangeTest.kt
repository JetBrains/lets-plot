package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_COLUMN_REQUEST
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.OBJECT_OSM_ID
import jetbrains.datalore.plot.config.Option.Meta.GeoReference
import jetbrains.datalore.plot.server.config.ServerSideTestUtil.createLayerConfigsByLayerSpec
import jetbrains.datalore.plot.server.config.ServerSideTestUtil.geoPositionsDict
import jetbrains.datalore.plot.server.config.SingleLayerAssert.Companion.assertThat
import kotlin.test.Test

class GeoReferenceMappingChangeTest {

    @Test
    fun whenMapContainsGeoReference_shouldKeepIdAndGeocodeColumns() {
        val cfg = createLayerConfigsByLayerSpec(geoPositionsDict(GEO_REFERENCE, GEO_REFERENCE_META))

        assertThat(cfg)
                .haveMapIds(GEO_REFERENCE[MAP_COLUMN_REQUEST] as List<*>)
                .haveMapGeocode(GEO_REFERENCE[OBJECT_OSM_ID] as List<*>)
    }

    companion object {
        private val GEO_REFERENCE = mapOf(
                MAP_COLUMN_REQUEST to listOf("foo", "bar", "xyz"),
                OBJECT_OSM_ID to listOf("123", "42", "27"),
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