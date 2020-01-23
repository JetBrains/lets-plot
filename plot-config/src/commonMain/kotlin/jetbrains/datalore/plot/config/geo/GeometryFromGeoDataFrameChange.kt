/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.geo

import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.plot.builder.map.GeoPositionField
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GeoDataKind
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_GEOMETRY_COLUMN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_ID_COLUMN
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.transform.SpecSelector


internal class GeometryFromGeoDataFrameChange : GeometryFromGeoPositionsChange() {
    override val geoPositionsKeys: Set<String>
        get() = GEO_DATA_FRAME_KEYS

    override fun changeGeoPositions(
        geoPositionsSpec: MutableMap<String, Any>,
        geoDataKind: GeoDataKind
    ) {
        val geometryObjects = (geoPositionsSpec[MAP_GEOMETRY_COLUMN] as List<*>).map { parse(geoDataKind, it as String) }
        val geometryTable = geoPositionsSpec[MAP_JOIN_ID_COLUMN]
            ?.let { col -> col as List<String> }
            ?.let { ids ->
                ids.zip(geometryObjects).fold(mutableMapOf<String, MutableList<Any>>(), { dataFrame, (id, geometryTable) ->
                    dataFrame
                        .concat(geometryTable)
                        .concat(MAP_JOIN_ID_COLUMN, MutableList(geometryTable.rowCount) { id })
                })
            }
            ?: geometryObjects.fold(mutableMapOf<String, MutableList<Any>>(), { dataFrame, geometryTable -> dataFrame.concat(geometryTable) })

        geoPositionsSpec.clear()
        geoPositionsSpec.putAll(geometryTable)
    }

    private val <K, V : List<Any>> Map<K, V>.rowCount get() = values.first().size
    private fun <T> MutableMap<String, MutableList<T>>.concat(other: Map<String, List<T>>) = apply {
        other.forEach { (key, value) -> getOrPut(key, { ArrayList() }).addAll(value) }
    }
    private fun <T> MutableMap<String, MutableList<T>>.concat(key: String, values: List<T>) = apply {
        concat(mutableMapOf(key to values))
    }

    private fun parse(geoDataKind: GeoDataKind, geoJson: String): MutableMap<String, MutableList<Double>> =
        when (geoDataKind) {
            GeoDataKind.CENTROID -> PointCollector(false)
            GeoDataKind.BOUNDARY -> PointCollector(true)
            GeoDataKind.LIMIT -> LimitCollector()
        }.parse(geoJson)

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

    private abstract class GeometryCollector(columns: List<String>) {
        constructor(vararg columns: String) : this(listOf(*columns))
        protected abstract val geometryConsumer: SimpleFeature.Consumer<LonLat>
        protected val df: MutableMap<String, MutableList<Double>> = columns.associateBy({ it }, { ArrayList<Double>() }).toMutableMap()

        fun parse(geoJson: String): MutableMap<String, MutableList<Double>> {
            GeoJson.parse(geoJson, geometryConsumer)
            return df
        }
    }

    private class LimitCollector : GeometryCollector(
        GeoPositionField.RECT_XMIN,
        GeoPositionField.RECT_YMIN,
        GeoPositionField.RECT_XMAX,
        GeoPositionField.RECT_YMAX
    ) {

        protected override val geometryConsumer: SimpleFeature.Consumer<LonLat> = SimpleFeature.Consumer(
            onPoint = {},
            onMultiPoint = { insert(listOf(it.boundingBox())) },
            onLineString = { insert(listOf(it.boundingBox())) },
            onMultiLineString = { insert(listOf(it.flatten().boundingBox())) },
            onPolygon = { insert(listOf(it.limit())) },
            onMultiPolygon = { insert(it.limit()) }
        )

        private fun insert(rectangles: List<Rect<LonLat>>) {
            convertToGeoRectangle(BBOX_CALCULATOR.rectsBBox(rectangles))
                .splitByAntiMeridian()
                .forEach { rect ->
                    df[GeoPositionField.RECT_XMIN]!!.add(rect.left)
                    df[GeoPositionField.RECT_XMAX]!!.add(rect.right)
                    df[GeoPositionField.RECT_YMIN]!!.add(rect.top)
                    df[GeoPositionField.RECT_YMAX]!!.add(rect.bottom)
                }
        }
    }

    private class PointCollector(
        private val closePath: Boolean
    ) : GeometryCollector(GeoPositionField.POINT_X, GeoPositionField.POINT_Y) {
        protected override val geometryConsumer: SimpleFeature.Consumer<LonLat> = SimpleFeature.Consumer<LonLat>(
            onPoint = if (closePath) ::nop else ::insertPoint, // Do not add points to polygon
            onMultiPoint = { insertPoints(it) },
            onLineString = { insertPoints(it) },
            onMultiLineString = { insertPoints(it.flatten()) },
            onPolygon = { insertPoints(it.flatten()) },
            onMultiPolygon = { insertPoints(it.flatten().flatten()) }
        )

        private fun insertPoints(points: List<Vec<LonLat>>) {
            points.forEach(this::insertPoint)

            if (closePath && points.isNotEmpty() && points.first() != points.last()) {
                insertPoint(points.first())
            }
        }

        private fun insertPoint(point: Vec<LonLat>) {
            df[GeoPositionField.POINT_X]!!.add(point.x)
            df[GeoPositionField.POINT_Y]!!.add(point.y)
        }

        private fun nop(p: Vec<LonLat>) = Unit
    }
}
