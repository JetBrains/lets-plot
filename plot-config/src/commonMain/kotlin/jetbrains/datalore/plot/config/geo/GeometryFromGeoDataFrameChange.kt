/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.geo

import jetbrains.datalore.base.spatial.GeoJson
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GeoDataKind
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_GEOMETRY_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_KEY_COLUMN
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.transform.SpecSelector


internal class GeometryFromGeoDataFrameChange : GeometryFromGeoPositionsChange() {
    override val geoPositionsKeys: Set<String>
        get() = GEO_DATA_FRAME_KEYS

    override fun changeGeoPositions(
        geoPositionsSpec: MutableMap<String, Any>,
        geoDataKind: GeoDataKind
    ) {
        val geometryDataFrameBuilder = GeometryDataFrameBuilder(geoDataKind)
        val geometryCombiner = GeometryCombiner(geometryDataFrameBuilder)
        val geometries = geoPositionsSpec[MAP_GEOMETRY_COLUMN] as MutableList<*>
        val idExists = geoPositionsSpec.containsKey(MAP_JOIN_KEY_COLUMN)
        for (i in geometries.indices) {
            val id =
                if (idExists) (geoPositionsSpec[MAP_JOIN_KEY_COLUMN] as MutableList<*>)[i] as String else ""
            geometryCombiner.combine(id, geometries[i] as String)
        }
        geoPositionsSpec.clear()
        geoPositionsSpec.putAll(if (idExists) geometryDataFrameBuilder.data else geometryDataFrameBuilder.geometry)
    }

    private class GeometryCombiner internal constructor(
        private val geometryDataBuilder: GeometryDataFrameBuilder
    ) {
        fun combine(id: String, geometry: String) {
            GeoJson.parse<LonLat>(geometry){
                onPoint = { geometryDataBuilder.addPoint(id, it) }
                onLineString = { geometryDataBuilder.addBoundary(id, it) }
                onPolygon = { geometryDataBuilder.addBoundary(id, it) }
                onMultiPoint = { geometryDataBuilder.addBoundary(id, it) }
                onMultiLineString = { geometryDataBuilder.addBoundary(id, it) }
                onMultiPolygon = { geometryDataBuilder.addBoundary(id, it) }
            }
        }
    }


    companion object {

        fun specSelector(isGGBunch: Boolean) = SpecSelector.from(
            if (isGGBunch) {
                listOf(Option.GGBunch.ITEMS, Option.GGBunch.Item.FEATURE_SPEC, Option.Plot.LAYERS);
            } else {
                listOf(Option.Plot.LAYERS);
            }
        )


        private val GEO_DATA_FRAME_KEYS: Set<String> = setOf(
            MAP_GEOMETRY_COLUMN
        )
    }
}
