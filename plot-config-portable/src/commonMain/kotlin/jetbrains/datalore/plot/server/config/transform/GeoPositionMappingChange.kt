/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_GEOMETRY_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_ID_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_OSM_ID_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_REGION_COLUMN
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GEOMETRY
import jetbrains.datalore.plot.config.Option.Meta.GeoReference
import jetbrains.datalore.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.getMap
import jetbrains.datalore.plot.config.has
import jetbrains.datalore.plot.config.read
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector
import jetbrains.datalore.plot.config.write

class GeoPositionMappingChange : SpecChange {

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val mapSpec = spec.getMap(GEO_POSITIONS)!!
        spec.read(MAP_DATA_META, GeoDataFrame.TAG, GEOMETRY)
            ?.let {it as String }
            ?.let { geometry ->
                mapSpec.write(MAP_GEOMETRY_COLUMN) { mapSpec.read(geometry)!! }

                listOfNotNull(
                    spec.read(MAP_JOIN_COLUMN)?.let { mapSpec.read(it as String) },
                    mapSpec.read(GeoReference.REQUEST),
                    mapSpec.read(MAP_JOIN_ID_COLUMN),
                    mapSpec.read(MAP_REGION_COLUMN)
                ).firstOrNull()?.let { mapSpec.write(MAP_JOIN_ID_COLUMN) { it } }
            }

        spec.read(MAP_DATA_META, GeoReference.TAG)?.run {
            mapSpec.write(MAP_JOIN_ID_COLUMN) { mapSpec.read(GeoReference.REQUEST)!! }
            mapSpec.write(MAP_OSM_ID_COLUMN) { mapSpec.read(GeoReference.OSM_ID)!! }
        }
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return spec.has(MAP_DATA_META, GeoDataFrame.TAG) ||
                spec.has(MAP_DATA_META, GeoReference.TAG)
    }

    companion object {
        internal fun specSelector() = SpecSelector.of(Plot.LAYERS)
    }
}
