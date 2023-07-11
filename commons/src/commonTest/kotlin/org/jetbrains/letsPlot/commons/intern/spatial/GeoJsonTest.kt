/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import kotlin.test.Test
import kotlin.test.assertTrue

typealias GeomTag = Untyped

class GeoJsonTest {

    private fun parse(geoJson: String, handler: SimpleFeature.Consumer<GeomTag>.() -> Unit) {
        GeoJson.parse<GeomTag>(geoJson, handler)
    }
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

        parse(data) {
            onPoint = { expected.removeOrThrow(it) }
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

        parse(data) {
            onLineString = { expected.removeOrThrow(it) }
        }
        assertTrue { expected.isEmpty() }
    }

    @Test
    fun simplePolygon() {
        val expected = mutableListOf(
            polygon(
                ring(p(30, 10), p(40, 40), p(20, 40), p(10, 20), p(30, 10))
            )
        )
        val data = """
            |{ "type": "Polygon", 
            |    "coordinates": [
            |        [[30, 10], [40, 40], [20, 40], [10, 20], [30, 10]]
            |    ]
            |}
            """.trimMargin()

        parse(data) {
            onPolygon = { expected.removeOrThrow(it) }
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

        parse(data) {
            onPolygon = { expected.removeOrThrow(it) }
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

        parse(data) {
            onMultiPoint = { expected.removeOrThrow(it) }
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

        parse(data) {
            onMultiLineString = { expected.removeOrThrow(it) }
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

        parse(data) {
            onMultiPolygon = { expected.removeOrThrow(it) }
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

        parse(data) {
            onMultiPolygon = { expected.removeOrThrow(it) }
        }

        assertTrue(expected.isEmpty())
    }

    @Test
    fun simpleGeometryCollection() {
        val expectedPoint = mutableListOf(
            p(102.0, 0.5)
        )
        val expectedLineString = mutableListOf(
            lineString(
                p(102.0, 0.0),
                p(103.0, 1.0),
                p(104.0, 0.0),
                p(105.0, 1.0)
            )
        )
        val expectedPolygon = mutableListOf(
            polygon(
                ring(p(100.0, 0.0), p(101.0, 0.0), p(101.0, 1.0), p(100.0, 1.0), p(100.0, 0.0))
            )
        )
        val data = """
            |{ "type": "GeometryCollection",
            |    "geometries": [
            |        {"type": "Point", "coordinates": [102.0, 0.5]},
            |        { "type": "LineString", "coordinates": [[102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]]},
            |        {"type": "Polygon", "coordinates": [[ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]]}
            |   ]
            |}
            """.trimMargin()

        parse(data) {
            onPoint = { expectedPoint.removeOrThrow(it) }
            onLineString = { expectedLineString.removeOrThrow(it) }
            onPolygon = { expectedPolygon.removeOrThrow(it) }
        }

        assertTrue(expectedPoint.isEmpty())
        assertTrue(expectedLineString.isEmpty())
        assertTrue(expectedPolygon.isEmpty())
    }

    @Test
    fun simpleFeatureCollection() {
        val expectedPoint = mutableListOf(
            p(102.0, 0.5)
        )
        val expectedLineString = mutableListOf(
            lineString(p(102.0, 0.0), p(103.0, 1.0), p(104.0, 0.0), p(105.0, 1.0))
        )
        val expectedPolygon = mutableListOf(
            polygon(
                ring(p(100.0, 0.0), p(101.0, 0.0), p(101.0, 1.0), p(100.0, 1.0), p(100.0, 0.0))
            )
        )
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

        parse(data) {
            onPoint = { expectedPoint.removeOrThrow(it) }
            onLineString = { expectedLineString.removeOrThrow(it) }
            onPolygon = { expectedPolygon.removeOrThrow(it) }
        }

        assertTrue(expectedPoint.isEmpty())
        assertTrue(expectedLineString.isEmpty())
        assertTrue(expectedPolygon.isEmpty())
    }

    companion object {
        private fun <T> MutableList<T>.removeOrThrow(v: T) = if (contains(v)) { remove(v) } else { error("Object $v not found") }
        private fun p(x: Int, y: Int) = p(x.toDouble(), y.toDouble())
        private fun p(x: Double, y: Double) = explicitVec<GeomTag>(x, y)
        private fun lineString(vararg points: Vec<GeomTag>) = LineString<GeomTag>(points.toList())
        private fun ring(vararg points: Vec<GeomTag>) = Ring<GeomTag>(points.toList())
        private fun polygon(vararg rings: Ring<GeomTag>) = Polygon<GeomTag>(rings.toList())
        private fun multiPoint(vararg points: Vec<GeomTag>) = MultiPoint<GeomTag>(points.toList())
        private fun multiLineString(vararg lineStrings: LineString<GeomTag>) = MultiLineString<GeomTag>(lineStrings.toList())
        private fun multiPolygon(vararg polygons: Polygon<GeomTag>) = MultiPolygon<GeomTag>(polygons.toList())
    }
}
