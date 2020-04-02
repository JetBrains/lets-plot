/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.builder.map.GeoPositionField.DATA_JOIN_KEY_COLUMN
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_GEOMETRY_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_ID_COLUMN
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Mapping.MAP_ID
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GEOMETRY
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector

class GeoDataFrameMappingChange : SpecChange {

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val geometryColumnName = spec.read(DATA_META, GeoDataFrame.TAG, GEOMETRY) as String
        val geometries = spec.list(DATA, geometryColumnName)!!
        val keys = geometries.indices.map(Int::toString)

        spec.remove(DATA, geometryColumnName)
        spec.write(DATA, DATA_JOIN_KEY_COLUMN) { keys }
        spec.write(GEO_POSITIONS, MAP_JOIN_ID_COLUMN) { keys }
        spec.write(GEO_POSITIONS, MAP_GEOMETRY_COLUMN) { geometries}
        spec.write(MAPPING, MAP_ID) { DATA_JOIN_KEY_COLUMN }
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return (!spec.has(GEO_POSITIONS)
                && spec.has(DATA_META, GeoDataFrame.TAG))
    }

    companion object {
        internal fun specSelector() = SpecSelector.of(Plot.LAYERS)
    }
}
