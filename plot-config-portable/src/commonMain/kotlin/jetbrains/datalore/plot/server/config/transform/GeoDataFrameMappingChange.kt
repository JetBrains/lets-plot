/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.base.Aes.Companion.MAP_ID
import jetbrains.datalore.plot.builder.map.GeoPositionField
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_GEOMETRY_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_KEY_COLUMN
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Layer.DATA
import jetbrains.datalore.plot.config.Option.Layer.MAPPING
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.select
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector

class GeoDataFrameMappingChange : SpecChange {

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val geometryColumnName = spec.select(DATA_META, GeoDataFrame.TAG, GeoDataFrame.GEOMETRY) as String
        val geometries = spec.select(DATA, geometryColumnName) as List<*>

        val keys = geometries.indices.map(Int::toString)

        spec[GEO_POSITIONS] = mutableMapOf(
            MAP_JOIN_KEY_COLUMN to keys,
            MAP_GEOMETRY_COLUMN to geometries
        )

        with(spec[DATA] as MutableMap<String, Any>) {
            remove(geometryColumnName)
            put(GeoPositionField.DATA_JOIN_KEY_COLUMN, keys)
        }

        val mapping = spec.getOrPut(MAPPING) { HashMap<Any, Any>() } as MutableMap<String, Any>
        mapping[MAP_ID.name] = GeoPositionField.DATA_JOIN_KEY_COLUMN
    }

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return (spec[GEO_POSITIONS] == null
                && spec.select(DATA_META, GeoDataFrame.TAG) != null)
    }

    companion object {
        internal fun specSelector(): SpecSelector {
            return SpecSelector.of(Plot.LAYERS)
        }

    }
}
