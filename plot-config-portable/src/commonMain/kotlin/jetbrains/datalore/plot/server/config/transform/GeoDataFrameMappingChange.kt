/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Mapping.MAP_ID
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GEOMETRY_COLUMN_NAME
import jetbrains.datalore.plot.config.Option.Meta.MapJoin
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector

class GeoDataFrameMappingChange : SpecChange {

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val geometryColumnName = spec.read(DATA_META, GeoDataFrame.TAG, GEOMETRY_COLUMN_NAME) as String
        val geometries = spec.getList(DATA, geometryColumnName)!!
        val ids = geometries.indices.map(Int::toString)

        spec.remove(DATA, geometryColumnName)
        spec.write(DATA, MapJoin.ID) { ids }
        spec.write(GEO_POSITIONS, MapJoin.ID) { ids }
        spec.write(GEO_POSITIONS, GeoDataFrame.GEOMETRIES) { geometries}
        spec.write(MAPPING, MAP_ID) { MapJoin.ID }
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return (!spec.has(GEO_POSITIONS)
                && spec.has(DATA_META, GeoDataFrame.TAG))
    }

    companion object {
        internal fun specSelector() = SpecSelector.of(Plot.LAYERS)
    }
}
