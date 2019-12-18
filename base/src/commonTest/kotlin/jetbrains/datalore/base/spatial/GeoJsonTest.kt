/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.typedGeometry.*
import kotlin.test.Test
import kotlin.test.assertTrue

class GeoJsonTest {

    @Test
    fun simplePoint() {
        val expected = mutableListOf(
            p(30, 10)
        )
        val data = """
            |{ "type": "Point", 
            |    "coordinates": [30, 10]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            point = { expected.removeOrThrow(it) }
        }
        assertTrue { expected.isEmpty() }
    }

    @Test
    fun simpleLineString() {
        val expected = mutableListOf(
            lineString(p(30, 10), p(10, 30), p(40, 40))
        )
        val data = """
            |{ "type": "LineString", 
            |    "coordinates": [
            |        [30, 10], [10, 30], [40, 40]
            |    ]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            lineString = { expected.removeOrThrow(it) }
        }
        assertTrue { expected.isEmpty() }
    }

    @Test
    fun simplePolygon() {
        val expected = mutableListOf(
            polygon(ring(p(30, 10), p(40, 40), p(20, 40), p(10, 20), p(30, 10)))
        )
        val data = """
            |{ "type": "Polygon", 
            |    "coordinates": [
            |        [[30, 10], [40, 40], [20, 40], [10, 20], [30, 10]]
            |    ]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            polygon = { expected.removeOrThrow(it) }
        }
        assertTrue { expected.isEmpty() }
    }

    @Test
    fun polygonWithHole() {
        val expected = mutableListOf(
            polygon(
                ring(p(35, 10), p(45, 45), p(15, 40), p(10, 20), p(35, 10)),
                ring(p(20, 30), p(35, 35), p(30, 20), p(20, 30))
            )
        )
        val data = """
            |{ "type": "Polygon", 
            |    "coordinates": [
            |        [[35, 10], [45, 45], [15, 40], [10, 20], [35, 10]], 
            |        [[20, 30], [35, 35], [30, 20], [20, 30]]
            |    ]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            polygon = { expected.removeOrThrow(it) }
        }
        assertTrue { expected.isEmpty() }
    }

    @Test
    fun simpleMultiPoint() {
        val expected = mutableListOf(
            multiPoint(p(10, 40), p(40, 30), p(20, 20), p(30, 10))
        )
        val data = """
            |{ "type": "MultiPoint", 
            |    "coordinates": [
            |        [10, 40], [40, 30], [20, 20], [30, 10]
            |    ]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            multiPoint = { points, _ -> expected.removeOrThrow(points) }
        }
        assertTrue { expected.isEmpty() }
    }
    
    @Test
    fun simpleMultiLineString() {
        val expected = mutableListOf(
            multiLineString(
                lineString(p(10, 10), p(20, 20), p(10, 40)),
                lineString(p(40, 40), p(30, 30), p(40, 20), p(30, 10))
            )
        )
        val data = """
            |{ "type": "MultiLineString", 
            |    "coordinates": [
            |        [[10, 10], [20, 20], [10, 40]], 
            |        [[40, 40], [30, 30], [40, 20], [30, 10]]
            |    ]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            multiLineString = { lineStrings, _ -> expected.removeOrThrow(lineStrings) }
        }

        assertTrue { expected.isEmpty() }
    }

    @Test
    fun simpleMultiPolygon() {
        val expected = mutableListOf(
            multiPolygon(
                polygon(
                    ring(p(30, 20), p(45, 40), p(10, 40), p(30, 20))
                ),
                polygon(
                    ring(p(15, 5), p(40, 10), p(10, 20), p(5, 10), p(15, 5))
                )
            )
        )
        val data = """
            |{ "type": "MultiPolygon", 
            |    "coordinates": [
            |        [
            |            [[30, 20], [45, 40], [10, 40], [30, 20]]
            |        ], 
            |        [
            |            [[15, 5], [40, 10], [10, 20], [5, 10], [15, 5]]
            |        ]
            |    ]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            multiPolygon = { multiPolygon, _ -> expected.removeOrThrow(multiPolygon) }
        }

        assertTrue { expected.isEmpty() }
    }

    @Test
    fun multiPolygonWithHole() {
        val expected = mutableListOf(
            multiPolygon(
                polygon(
                    ring(p(40, 40), p(20, 45), p(45, 30), p(40, 40))
                ),
                polygon(
                    ring(p(20, 35), p(10, 30), p(10, 10), p(30, 5), p(45, 20), p(20, 35)),
                    ring(p(30, 20), p(20, 15), p(20, 25), p(30, 20))
                )
            )
        )
        val data = """
            |{ "type": "MultiPolygon",
            |   "coordinates": [
            |       [
            |           [[40, 40], [20, 45], [45, 30], [40, 40]]
            |       ],
            |       [
            |           [[20, 35], [10, 30], [10, 10], [30, 5], [45, 20], [20, 35]],
            |           [[30, 20], [20, 15], [20, 25], [30, 20]]
            |       ]
            |   ]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            multiPolygon = { multiPolygon, _ -> expected.removeOrThrow(multiPolygon) }
        }

        assertTrue(expected.isEmpty())
    }

    @Test
    fun simpleCollection() {
        val expectedPoint = mutableListOf(p(102.0, 0.5))
        val expectedLineString = mutableListOf(lineString(p(102.0, 0.0), p(103.0, 1.0), p(104.0, 0.0), p(105.0, 1.0)))
        val expectedPolygon = mutableListOf(polygon(ring(p(100.0, 0.0), p(101.0, 0.0), p(101.0, 1.0), p(100.0, 1.0), p(100.0, 0.0))))
        val data = """
            |{ "type": "FeatureCollection",
            |    "features": [
            |    { "type": "Feature",
            |        "geometry": {"type": "Point", "coordinates": [102.0, 0.5]},
            |        "properties": {"prop0": "value0"}
            |    },
            |    { "type": "Feature",
            |        "geometry": {
            |            "type": "LineString",
            |            "coordinates": [
            |                [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]
            |            ]
            |        },
            |        "properties": {
            |            "prop0": "value0",
            |            "prop1": 0.0
            |        }
            |    },
            |    { "type": "Feature",
            |        "geometry": {
            |            "type": "Polygon",
            |            "coordinates": [
            |                [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0],
            |                    [100.0, 1.0], [100.0, 0.0] ]
            |            ]
            |        },
            |        "properties": {
            |            "prop0": "value0",
            |            "prop1": {"this": "that"}
            |        }
            |    }
            |   ]
            |}
            """.trimMargin()

        GeoJson.parse(data) {
            point = { expectedPoint.removeOrThrow(it) }
            lineString = { expectedLineString.removeOrThrow(it) }
            polygon = { expectedPolygon.removeOrThrow(it) }
        }

        assertTrue(expectedPoint.isEmpty())
        assertTrue(expectedLineString.isEmpty())
        assertTrue(expectedPolygon.isEmpty())
    }
    
    companion object {
        private fun <T> MutableList<T>.removeOrThrow(v: T) = if (contains(v)) { remove(v) } else { error("Object $v not found") }
        private fun p(x: Int, y: Int) = p(x.toDouble(), y.toDouble())
        private fun p(x: Double, y: Double) = explicitVec<Generic>(x, y)
        private fun lineString(vararg points: Vec<Generic>) = LineString<Generic>(points.toList())
        private fun ring(vararg points: Vec<Generic>) = Ring<Generic>(points.toList())
        private fun polygon(vararg rings: Ring<Generic>) = Polygon<Generic>(rings.toList())
        private fun multiPoint(vararg points: Vec<Generic>) = MultiPoint<Generic>(points.toList())
        private fun multiLineString(vararg lineStrings: LineString<Generic>) = MultiLineString<Generic>(lineStrings.toList())
        private fun multiPolygon(vararg polygons: Polygon<Generic>) = MultiPolygon<Generic>(polygons.toList())
    }
}
