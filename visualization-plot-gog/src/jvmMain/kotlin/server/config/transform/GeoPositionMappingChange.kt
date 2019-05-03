package jetbrains.datalore.visualization.plot.gog.server.config.transform

import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.GEO_POSITIONS_KEYS
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.MAP_COLUMN_GEOJSON
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.MAP_COLUMN_JOIN_KEY
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.MAP_COLUMN_OSM_ID
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.MAP_COLUMN_REQUEST
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.OBJECT_OSM_ID
import jetbrains.datalore.visualization.plot.gog.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.visualization.plot.gog.config.Option.Meta.GeoDataFrame
import jetbrains.datalore.visualization.plot.gog.config.Option.Meta.GeoReference
import jetbrains.datalore.visualization.plot.gog.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChange
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChangeContext
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecSelector

class GeoPositionMappingChange : SpecChange {

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        spec[GEO_POSITIONS] = HashMap(SUPPORTED_TAGS[getFirstSupportedTagInMapMeta(spec)]?.invoke(spec))
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return getFirstSupportedTagInMapMeta(spec) != null
    }

    companion object {
        private val SUPPORTED_TAGS = mapOf<String, (Map<String, Any?>) -> Map<String, Any?>>(
                GeoReference.TAG to { it -> transformGeoReference(it) },
                GeoDataFrame.TAG to { it -> transformGeoDataFrame(it) }
        )

        internal fun specSelector(): SpecSelector {
            return SpecSelector.of(Plot.LAYERS)
        }

        private fun getFirstSupportedTagInMapMeta(geomSpec: Map<String, Any>): String? {
            return if (geomSpec[MAP_DATA_META] is Map<*, *>) {
                getFirstSupportedValue((geomSpec[MAP_DATA_META] as Map<*, *>).keys, SUPPORTED_TAGS.keys)
            } else {
                null
            }
        }

        private fun getFirstSupportedValue(values: Set<*>, supportedValues: Iterable<String>): String? {
            for (supportedValue in supportedValues) {
                if (values.contains(supportedValue)) {
                    return supportedValue
                }
            }
            return null
        }

        private fun transformGeoReference(geomSpec: Map<String, Any?>): Map<String, Any?> {
            val map = geomSpec[GEO_POSITIONS] as Map<*, *>

            return mapOf(
                    MAP_COLUMN_JOIN_KEY to map[MAP_COLUMN_REQUEST],
                    MAP_COLUMN_OSM_ID to map[OBJECT_OSM_ID]
            )
        }

        private fun transformGeoDataFrame(geomSpec: Map<String, Any?>): Map<String, Any?> {
            val map = geomSpec[GEO_POSITIONS] as Map<*, *>

            val supportedGeoPositionsKeys = ArrayList<String>()
            supportedGeoPositionsKeys.add(MAP_COLUMN_REQUEST)
            supportedGeoPositionsKeys.addAll(GEO_POSITIONS_KEYS)

            val geoPositionsKey = getFirstSupportedValue(map.keys, supportedGeoPositionsKeys)
            val geometryColumn = ((geomSpec[MAP_DATA_META] as Map<*, *>)[GeoDataFrame.TAG] as Map<*, *>)[GeoDataFrame.GEOMETRY] as String

            val transformedGeoPositions = HashMap<String, Any?>()
            transformedGeoPositions[MAP_COLUMN_GEOJSON] = map[geometryColumn]

            if (geoPositionsKey != null) {
                transformedGeoPositions[MAP_COLUMN_JOIN_KEY] = map[geoPositionsKey]
            }

            return transformedGeoPositions
        }
    }
}
