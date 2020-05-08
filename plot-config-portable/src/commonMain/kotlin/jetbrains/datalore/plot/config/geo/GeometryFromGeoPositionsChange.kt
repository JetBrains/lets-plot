/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.geo

import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.config.GeoPositionsDataUtil
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.getGeoDataKind
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.isGeomSupported
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.asMutable
import jetbrains.datalore.plot.config.getMap
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext


abstract class GeometryFromGeoPositionsChange : SpecChange {
    override fun isApplicable(spec: Map<String, Any>): Boolean {
        if (spec[GEO_POSITIONS] !is MutableMap<*, *>) {
            return false
        }
        if (!(spec[GEO_POSITIONS] as MutableMap<*, *>?)!!.keys.containsAll(geoPositionsKeys)) {
            return false
        }
        return isGeomSupported(getGeomKind(spec))
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        changeGeoPositions(
            mapSpec = spec.getMap(GEO_POSITIONS)!!.asMutable(),
            geoDataKind = getGeoDataKind(getGeomKind(spec))
        )
    }

    abstract val geoPositionsKeys: Set<String>
    abstract fun changeGeoPositions(mapSpec: MutableMap<String, Any>, geoDataKind: GeoPositionsDataUtil.GeoDataKind)

    companion object {
        private fun getGeomKind(layerSpec: Map<String, Any>): GeomKind {
            val name = layerSpec[GEOM] as String
            return Option.GeomName.toGeomKind(name)
        }
    }
}