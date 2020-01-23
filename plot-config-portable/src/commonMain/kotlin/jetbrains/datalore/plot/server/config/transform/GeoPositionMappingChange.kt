/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GEOCODING_OSM_ID_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GEOCODING_REQUEST_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GEO_POSITIONS_KEYS
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_GEOMETRY_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_ID_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_OSM_ID_COLUMN
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame
import jetbrains.datalore.plot.config.Option.Meta.GeoReference
import jetbrains.datalore.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.select
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector

class GeoPositionMappingChange : SpecChange {

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        spec[GEO_POSITIONS] = HashMap(SUPPORTED_TAGS[getFirstSupportedTagInMapMeta(spec)]?.invoke(spec)!!)
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return getFirstSupportedTagInMapMeta(spec) != null
    }

    companion object {
        private val SUPPORTED_TAGS = mapOf<String, (Map<String, Any?>) -> Map<String, Any?>>(
                GeoReference.TAG to ::transformGeoReference,
                GeoDataFrame.TAG to ::transformGeoDataFrame
        )

        internal fun specSelector(): SpecSelector {
            return SpecSelector.of(Plot.LAYERS)
        }

        private fun getFirstSupportedTagInMapMeta(geomSpec: Map<String, Any>): String? {
            return geomSpec[MAP_DATA_META]?.let {
                SUPPORTED_TAGS.keys.firstOrNull((it as Map<*, *>).keys::contains) }
        }


        private fun transformGeoReference(geomSpec: Map<String, Any?>): Map<String, Any?> {
            return mapOf(
                    MAP_JOIN_ID_COLUMN to geomSpec.select(GEO_POSITIONS, GEOCODING_REQUEST_COLUMN),
                    MAP_OSM_ID_COLUMN to geomSpec.select(GEO_POSITIONS, GEOCODING_OSM_ID_COLUMN)
            )
        }

        private fun transformGeoDataFrame(geomSpec: Map<String, Any?>): Map<String, Any?> {
            val mapOptions = geomSpec[GEO_POSITIONS] as Map<*, *>

            val geometryColumnName = geomSpec.select(MAP_DATA_META, GeoDataFrame.TAG, GeoDataFrame.GEOMETRY) as String
            val transformedGeoPositions = mutableMapOf(
                MAP_GEOMETRY_COLUMN to mapOptions[geometryColumnName]
            )

            listOf(GEOCODING_REQUEST_COLUMN, *GEO_POSITIONS_KEYS.toTypedArray())
                .firstOrNull { mapOptions.containsKey(it)}
                ?.let { transformedGeoPositions[MAP_JOIN_ID_COLUMN] = mapOptions[it] }

            return transformedGeoPositions
        }
    }
}
