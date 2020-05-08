/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_OSM_ID_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_REGION_COLUMN
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GEOMETRY_COLUMN_NAME
import jetbrains.datalore.plot.config.Option.Meta.GeoDict
import jetbrains.datalore.plot.config.Option.Meta.GeoReference
import jetbrains.datalore.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.plot.config.Option.Meta.MapJoin
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector

class GeoPositionMappingChange : SpecChange {

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val mapSpec = spec.getMap(GEO_POSITIONS)!!
        val mapJoinIds = spec.read(MapJoin.MAP_JOIN_COLUMN)?.let { mapSpec.read(it as String) }

        if (spec.has(MAP_DATA_META, GeoDataFrame.TAG)) {
            // Select column applicable for join
            listOfNotNull(
                mapJoinIds, // user defined column via parameter `map_join`
                mapSpec.read(GeoReference.REQUEST), // Regions object from our geocoding
                mapSpec.read(MAP_REGION_COLUMN) // ???
            ).firstOrNull()?.let { mapSpec.write(MapJoin.ID) { it } }

            spec.read(MAP_DATA_META, GeoDataFrame.TAG, GEOMETRY_COLUMN_NAME)
                ?.let { geometryColumnName -> mapSpec.read(geometryColumnName as String)!! }
                ?.let { geometries -> mapSpec.write(GeoDataFrame.GEOMETRIES) { geometries } }
        }

        if (spec.has(MAP_DATA_META, GeoReference.TAG)) {
            mapSpec.write(MapJoin.ID) { mapSpec.read(GeoReference.REQUEST)!! }
            mapSpec.write(MAP_OSM_ID_COLUMN) { mapSpec.read(GeoReference.OSM_ID)!! }
        }

        if (spec.has(MAP_DATA_META, GeoDict.TAG)) {
            mapJoinIds?.let { mapSpec.write(MapJoin.ID) { it } }
        }
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return spec.has(MAP_DATA_META, GeoDataFrame.TAG) ||
                spec.has(MAP_DATA_META, GeoReference.TAG) ||
                spec.has(MAP_DATA_META, GeoDict.TAG)
    }

    companion object {
        internal fun specSelector() = SpecSelector.of(Plot.LAYERS)
    }
}
