/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.geo

import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_X
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_Y
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMIN
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMIN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GeoDataKind
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_GEOMETRY_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_ID_COLUMN
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.transform.SpecSelector


internal class GeometryFromGeoDataFrameChange : GeometryFromGeoPositionsChange() {
    override val geoPositionsKeys: Set<String>
        get() = GEO_DATA_FRAME_KEYS

    override fun changeGeoPositions(
        mapSpec: MutableMap<String, Any>,
        geoDataKind: GeoDataKind
    ) {
        val geometryTables = (mapSpec[MAP_GEOMETRY_COLUMN] as List<*>).map { parseGeometry(it as String, geoDataKind) }

        if (geometryTables.sumBy { it.rowCount } == 0) {
            error("Geometries are empty or no matching types. Expected: " +
                    when(geoDataKind) {
                        GeoDataKind.POINT -> "Point, MultiPoint"
                        GeoDataKind.PATH -> "LineString, MultiLineString"
                        GeoDataKind.BOUNDARY -> "Polygon, MultiPolygon"
                        GeoDataKind.BBOX -> "MultiPoint, LineString, MultiLineString, Polygon, MultiPolygon"
                    }
            )
        }

        val dataTable = (mapSpec[MAP_JOIN_ID_COLUMN] as? List<*>)
            ?.zip(geometryTables)
            ?.fold(mutableMapOf<String, MutableList<Any>>(), { dataFrame, (id, geometryTable) ->
                dataFrame
                    .concat(geometryTable)
                    .concat(MAP_JOIN_ID_COLUMN, MutableList(geometryTable.rowCount) { id as String })
            })
            ?: geometryTables.fold(mutableMapOf<String, MutableList<Any>>(), { dataFrame, geometryTable ->
                dataFrame.concat(geometryTable)
            })

        mapSpec.clear()
        mapSpec.putAll(dataTable)
    }

    private fun parseGeometry(
        geoJson: String,
        geoDataKind: GeoDataKind
    ): MutableMap<String, MutableList<Double>> {
        val geometryTable = mutableMapOf<String, MutableList<Double>>()

        when (geoDataKind) {
            GeoDataKind.POINT -> defaultConsumer {
                onPoint = geometryTable::append
                onMultiPoint = { it.forEach(geometryTable::append) }
            }
            GeoDataKind.PATH -> defaultConsumer {
                onLineString = { it.forEach(geometryTable::append) }
                onMultiLineString = { it.flatten().forEach(geometryTable::append) }
            }
            GeoDataKind.BOUNDARY -> defaultConsumer {
                onPolygon = { it.flatten().forEach(geometryTable::append) }
                onMultiPolygon = { it.flatten().flatten().forEach(geometryTable::append) }
            }

            GeoDataKind.BBOX -> {
                fun insert(bboxes: List<Rect<LonLat>>) =
                    bboxes
                        .run(BBOX_CALCULATOR::union)
                        .run(::convertToGeoRectangle)
                        .run(GeoRectangle::splitByAntiMeridian)
                        .forEach(geometryTable::append)

                fun insert(bbox: Rect<LonLat>) = insert(listOf(bbox))

                defaultConsumer {
                    onMultiPoint = { insert(it.boundingBox()) }
                    onLineString = { insert(it.boundingBox()) }
                    onMultiLineString = { insert(it.flatten().boundingBox()) }
                    onPolygon = { insert(it.limit()) }
                    onMultiPolygon = { insert(it.limit()) }
                }

            }
        }.let { GeoJson.parse(geoJson, it) }

        return geometryTable
    }

    companion object {
        fun specSelector(isGGBunch: Boolean) = SpecSelector.from(
            if (isGGBunch) {
                listOf(Option.GGBunch.ITEMS, Option.GGBunch.Item.FEATURE_SPEC, Option.Plot.LAYERS);
            } else {
                listOf(Option.Plot.LAYERS);
            }
        )

        private val GEO_DATA_FRAME_KEYS: Set<String> = setOf(MAP_GEOMETRY_COLUMN)

        internal fun defaultConsumer(config: SimpleFeature.Consumer<LonLat>.() -> Unit) =
            SimpleFeature.Consumer<LonLat>(
                onPoint = {},
                onMultiPoint = {},
                onLineString = {},
                onMultiLineString = {},
                onPolygon = {},
                onMultiPolygon = {}
            ).apply(config)
    }
}

private val <K, V : List<Any>> Map<K, V>.rowCount get() = values.firstOrNull()?.size ?: 0

private fun <T> MutableMap<String, MutableList<T>>.concat(other: Map<String, List<T>>) = apply {
    other.forEach { (key, value) -> getOrPut(key, { ArrayList() }).addAll(value) }
}

private fun <T> MutableMap<String, MutableList<T>>.concat(key: String, values: List<T>) = apply {
    concat(mutableMapOf(key to values))
}

fun MutableMap<String, MutableList<Double>>.append(key: String, value: Double) {
    getOrPut(key, { mutableListOf() }).add(value)
}

fun MutableMap<String, MutableList<Double>>.append(p: Vec<LonLat>) {
    append(POINT_X, p.x)
    append(POINT_Y, p.y)
}

fun MutableMap<String, MutableList<Double>>.append(rect: Rect<LonLat>) {
    append(RECT_XMIN, rect.left)
    append(RECT_XMAX, rect.right)
    append(RECT_YMIN, rect.top)
    append(RECT_YMAX, rect.bottom)
}
