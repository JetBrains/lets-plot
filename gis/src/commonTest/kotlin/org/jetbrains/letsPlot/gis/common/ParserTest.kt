/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.common


import org.jetbrains.letsPlot.commons.intern.spatial.SimpleFeature
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.gis.common.testUtils.HexParser.parseHex
import org.jetbrains.letsPlot.gis.common.twkb.Twkb
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {
    private fun p(x: Double, y: Double): Vec<Untyped> {
        return explicitVec(x, y)
    }
    
    private fun <T> optionalListOf (vararg elements: T): List<T>? {
        return if (elements.isNotEmpty()) elements.asList() else emptyList()
    }

    @Test
    fun pointTest() {
        // SELECT encode(ST_AsTWKB('POINT(-71.064544 42.28787)'::geometry, 6), 'hex');
        val data = parseHex("c100bfefe243fc8baa28")

        val c = SimpleGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(p(-71.064544, 42.28787), c.point)
    }

    @Test
    fun lineTest() {
        // SELECT encode(ST_AsTWKB('LINESTRING(1 2, 3 4)', 6), 'hex');

        val data = parseHex("c2000280897a8092f4018092f4018092f401")

        val c = SimpleGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf(
                p(1.0, 2.0),
                p(3.0, 4.0)
            ),
            c.lineString
        )
    }

    @Test
    fun polygonTest() {
        // SELECT encode(ST_AsTWKB('POLYGON(
        //     (-71.1776585052917 42.3902909739571,
        //      -71.1776820268866 42.3903701743239,
        //      -71.1776063012595 42.3903825660754,
        //      -71.1775826583081 42.3903033653531,
        //      -71.1776585052917 42.3902909739571)
        //  )'::geometry, 6), 'hex');
        val data = parseHex("c3000105f5d6f043a6ccb6282d9e0198011a2e9f01970117")

        val c = SimpleGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf( // rings
                optionalListOf( // ring
                    p(-71.177659, 42.390291),
                    p(-71.177682, 42.39037),
                    p(-71.177606, 42.390383),
                    p(-71.177583, 42.390303),
                    p(-71.177659, 42.390291)
                )
            ),
            c.polygon
        )
    }

    @Test
    fun multiPointTest() {
        // SELECT encode(ST_AsTWKB('MULTIPOINT(1 2, 3 4)', 6), 'hex');
        val data = parseHex("c4000280897a8092f4018092f4018092f401")

        val c = SimpleGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf(
                p(1.0, 2.0),
                p(3.0, 4.0)
            ),
            c.multiPoint
        )
    }

    @Test
    fun multiLineStringTest() {
        // SELECT encode(ST_AsTWKB('MULTILINESTRING((1 2, 3 4), (5 6, 7 8), (9 10, 11 12))', 6), 'hex');
        val data =
            parseHex("c500030280897a8092f4018092f4018092f401028092f4018092f4018092f4018092f401028092f4018092f4018092f4018092f401")

        val c = SimpleGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf(
                optionalListOf(
                    p(1.0, 2.0),
                    p(3.0, 4.0)
                ),
                optionalListOf(
                    p(5.0, 6.0),
                    p(7.0, 8.0)
                ),
                optionalListOf(
                    p(9.0, 10.0),
                    p(11.0, 12.0)
                )
            ),
            c.multiLineString
        )
    }

    @Test
    fun multipolygonTest() {
        // SELECT encode(ST_AsTWKB('MULTIPOLYGON(((
        //						-71.103188 42.315277,
        //						-71.103162 42.315296,
        //            -71.102923 42.314915,
        //						-71.102309 42.315196,
        //            -71.101928 42.314738,
        //						-71.102505 42.314472,
        //            -71.102774 42.314165,
        //						-71.103113 42.314273,
        //            -71.103248 42.314024,
        //						-71.103300 42.314039,
        //            -71.103348 42.313949,
        //						-71.103396 42.313863,
        //            -71.104152 42.314115,
        //						-71.104141 42.314154,
        //            -71.104128 42.314211,
        //						-71.104118 42.314269,
        //            -71.104111 42.314327,
        //						-71.104107 42.314385,
        //            -71.104105 42.314443,
        //						-71.104106 42.314500,
        //            -71.104109 42.314558,
        //						-71.104116 42.314616,
        //            -71.104125 42.314674,
        //						-71.104137 42.314731,
        //            -71.104149 42.314771,
        //						-71.104159 42.314808,
        //            -71.104251 42.315128,
        //						-71.104117 42.315073,
        //            -71.104080 42.315134,
        //						-71.104043 42.315119,
        //            -71.104019 42.315183,
        //						-71.103873 42.315114,
        //            -71.103844 42.315100,
        //						-71.103831 42.315094,
        //            -71.103739 42.315054,
        //						-71.103544 42.315260,
        //            -71.103343 42.315164,
        //						-71.103258 42.315226,
        //            -71.103223 42.315251,
        //						-71.103188 42.315277)),
        //						((
        //            -71.104363 42.315113,
        //            -71.104358 42.315121,
        //						-71.104344 42.315067,
        //            -71.104385 42.315079,
        //            -71.104363 42.315113
        //            ))
        //					   )'::geometry, 6), 'hex'
        //			 );
        val data = parseHex(
            "c600020128a7cbe7439ab8ad283426de03f905cc09b204fa059307810993049904e504a505d8018d02f103671e5fb3015fab01e70bf803164e1a721" +
                    "4740e7408740474017205740d74117417721750134ab70180058c026d4a7a4a1d308001a40289013a1b1a0bb8014f86039c039203bf01aa017c463246340105ad12c7020a1" +
                    "01c6b51182c44"
        )
        val c = SimpleGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals<List<List<List<Vec<Untyped>>?>?>?>(
            optionalListOf( // polygons
                optionalListOf( // polygon
                    optionalListOf( // ring
                        p(-71.103188, 42.315277),
                        p(-71.103162, 42.315296),
                        p(-71.102923, 42.314915),
                        p(-71.102309, 42.315196),
                        p(-71.101928, 42.314738),
                        p(-71.102505, 42.314472),
                        p(-71.102774, 42.314165),
                        p(-71.103113, 42.314273),
                        p(-71.103248, 42.314024),
                        p(-71.1033, 42.314039),
                        p(-71.103348, 42.313949),
                        p(-71.103396, 42.313863),
                        p(-71.104152, 42.314115),
                        p(-71.104141, 42.314154),
                        p(-71.104128, 42.314211),
                        p(-71.104118, 42.314269),
                        p(-71.104111, 42.314327),
                        p(-71.104107, 42.314385),
                        p(-71.104105, 42.314443),
                        p(-71.104106, 42.3145),
                        p(-71.104109, 42.314558),
                        p(-71.104116, 42.314616),
                        p(-71.104125, 42.314674),
                        p(-71.104137, 42.314731),
                        p(-71.104149, 42.314771),
                        p(-71.104159, 42.314808),
                        p(-71.104251, 42.315128),
                        p(-71.104117, 42.315073),
                        p(-71.10408, 42.315134),
                        p(-71.104043, 42.315119),
                        p(-71.104019, 42.315183),
                        p(-71.103873, 42.315114),
                        p(-71.103844, 42.3151),
                        p(-71.103831, 42.315094),
                        p(-71.103739, 42.315054),
                        p(-71.103544, 42.31526),
                        p(-71.103343, 42.315164),
                        p(-71.103258, 42.315226),
                        p(-71.103223, 42.315251),
                        p(-71.103188, 42.315277)
                    ) // ring
                ), // polygon
                optionalListOf( // polygon
                    optionalListOf( // ring
                        p(-71.104363, 42.315113),
                        p(-71.104358, 42.315121),
                        p(-71.104344, 42.315067),
                        p(-71.104385, 42.315079),
                        p(-71.104363, 42.315113)
                    ) // ring
                ) // polygon
            ), // polygons
            c.multiPolygon
        )
    }

    @Test
    fun collectionOfOnePointTest() {
        // SELECT encode(ST_AsTWKB(array[
        //        ST_PointFromText('POINT(0.0001 0.0002)')
        // ], array[1], 6), 'hex');

        val data = parseHex("c4040102c8019003")
        val c = CollectedGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf(
                p(0.0001, 0.0002)
            ),
            c.points
        )
    }

    @Test
    fun collectionPointTest() {
        // SELECT encode(ST_AsTWKB(array[
        //         ST_PointFromText('POINT(0.00001 0.00002)'),
        //         ST_PointFromText('POINT(0.00003 0.00004)'),
        //         ST_PointFromText('POINT(0.00005 0.00006)')
        // ], array[1, 2, 3], 6), 'hex');

        val data = parseHex("c40403020406142828282828")
        val c = CollectedGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf(
                p(0.00001, 0.00002),
                p(0.00003, 0.00004),
                p(0.00005, 0.00006)
            ),
            c.points
        )
    }

    @Test
    fun collectionLineStringTest() {
        // SELECT encode(ST_AsTWKB(array[
        //         ST_LineFromText('LINESTRING(0.00001 0.00002, 0.00003 0.00004)'),
        //         ST_LineFromText('LINESTRING(0.00005 0.00006, 0.00007 0.00008)'),
        //         ST_LineFromText('LINESTRING(0.00009 0.00010, 0.00011 0.00012)')
        // ], array[1, 2, 3], 6), 'hex');

        val data = parseHex("c50403020406021428282802282828280228282828")
        val c = CollectedGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf(
                optionalListOf(
                    p(0.00001, 0.00002),
                    p(0.00003, 0.00004)
                ),
                optionalListOf(
                    p(0.00005, 0.00006),
                    p(0.00007, 0.00008)
                ),
                optionalListOf(
                    p(0.00009, 0.00010),
                    p(0.00011, 0.00012)
                )
            ),
            c.lineStrings
        )
    }

    @Test
    fun collectionPolygonTest() {
        // SELECT encode(ST_AsTWKB(array[
        //        ST_PolygonFromText('POLYGON((0 0,4 0,4 4,0 4,0 0))'),
        //        ST_PolygonFromText('POLYGON((0 0,4 0,4 4,0 4,0 0), (1 1,1 3,3 3,3 1,1 1))')
        // ], array[1, 2], 6), 'hex');

        val data =
            parseHex(("c6040202040105000080a4e803000080a4e803ffa3e8030000ffa3e8030205000080a4e803000080a4e803ffa3e8030000ffa3e8030580897a80897" + "a008092f4018092f4010000ff91f401ff91f40100"))
        val c = CollectedGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals<List<List<List<Vec<Untyped>>?>?>?>(
            optionalListOf( // polygons
                optionalListOf( // 1 ring
                    optionalListOf(
                        p(0.0, 0.0),
                        p(4.0, 0.0),
                        p(4.0, 4.0),
                        p(0.0, 4.0),
                        p(0.0, 0.0)
                    )
                ),
                optionalListOf( // 2 rings
                    optionalListOf(
                        p(0.0, 0.0),
                        p(4.0, 0.0),
                        p(4.0, 4.0),
                        p(0.0, 4.0),
                        p(0.0, 0.0)
                    ),
                    optionalListOf(
                        p(1.0, 1.0),
                        p(1.0, 3.0),
                        p(3.0, 3.0),
                        p(3.0, 1.0),
                        p(1.0, 1.0)
                    )
                )
            ),
            c.polygons
        )
    }

    @Test
    fun collectionMultiPointTest() {
        // SELECT encode(ST_AsTWKB(array[
        //     ST_MPointFromText('MULTIPOINT(0 1, 2 3)'),
        //     ST_MPointFromText('MULTIPOINT(4 5, 6 7)')
        // ], array[1, 2], 1), 'hex');

        val data = parseHex("27040202042400020014282824000250642828")
        val c = CollectedGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf(
                optionalListOf( // multipoint
                    p(0.0, 1.0),
                    p(2.0, 3.0)
                ),
                optionalListOf( // multipoint
                    p(4.0, 5.0),
                    p(6.0, 7.0)
                )
            ),
            c.multiPoints
        )
    }

    @Test
    fun collectionMultiLineStringTest() {
        // SELECT encode(ST_AsTWKB(array[
        //        ST_MLineFromText('MULTILINESTRING((1 2, 3 4), (4 5, 6 7))'),
        //        ST_MLineFromText('MULTILINESTRING((8 7, 6 5), (4 3, 2 1))')
        // ], array[1, 2], 6), 'hex');

        val data =
            parseHex(("c704020204c500020280897a8092f4018092f4018092f4010280897a80897a8092f4018092f401c500020280c8d00780bfd606ff91f401ff91f4010" + "2ff91f401ff91f401ff91f401ff91f401"))
        val c = CollectedGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals<List<List<List<Vec<Untyped>>?>?>?>(
            optionalListOf( // multi lines
                optionalListOf( // lines
                    optionalListOf(
                        p(1.0, 2.0),
                        p(3.0, 4.0)
                    ),
                    optionalListOf(
                        p(4.0, 5.0),
                        p(6.0, 7.0)
                    )
                ),
                optionalListOf(
                    optionalListOf(
                        p(8.0, 7.0),
                        p(6.0, 5.0)
                    ),
                    optionalListOf(
                        p(4.0, 3.0),
                        p(2.0, 1.0)
                    )
                )
            ),
            c.multiLineStrings
        )
    }

    @Test
    fun collectionMultiPolygonTest() {
        // SELECT encode(ST_AsTWKB(array[
        //        ST_MPolyFromText('MULTIPOLYGON(((0 0, 1 0, 0 1, 0 0)), ((1 1, 0 1, 1 0, 1 1)))'),
        //        ST_MPolyFromText('MULTIPOLYGON(((2 0, 3 0, 2 1, 2 0)))')
        // ], array[1, 2], 6), 'hex');

        val data =
            parseHex(("c704020204c600020104000080897a00ff887a80897a00ff887a010480897a80897aff887a0080897aff887a0080897ac6000101048092f40100808" + "97a00ff887a80897a00ff887a"))
        val c = CollectedGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals<List<List<List<List<Vec<Untyped>>?>?>?>?>(
            optionalListOf( //
                optionalListOf( // multi1
                    optionalListOf( // poly1
                        optionalListOf( // ring
                            p(0.0, 0.0),
                            p(1.0, 0.0),
                            p(0.0, 1.0),
                            p(0.0, 0.0)
                        )
                    ),
                    optionalListOf( // poly2
                        optionalListOf( // ring
                            p(1.0, 1.0),
                            p(0.0, 1.0),
                            p(1.0, 0.0),
                            p(1.0, 1.0)
                        )
                    )
                ),
                optionalListOf( // multi2
                    optionalListOf( // poly
                        optionalListOf( // ring
                            p(2.0, 0.0),
                            p(3.0, 0.0),
                            p(2.0, 1.0),
                            p(2.0, 0.0)
                        )
                    )
                )
            ),
            c.multiPolygons
        )
    }

    @Test
    fun collectionWithEmptyGeometry() {
        // SELECT encode(ST_AsTWKB(array[
        //        ST_PointFromText('POINT(0.00001 0.00002)'),
        //        ST_GeomFromText('GEOMETRYCOLLECTION EMPTY'),
        //        ST_PointFromText('POINT(0.00005 0.00006)')
        // ], array[1, 2, 3], 6), 'hex');

        val data = parseHex("c70403020406c1001428c710c1006478")

        val c = CollectedGeometryConsumer()
        Twkb.parse(data, c)

        assertEquals(
            optionalListOf(
                p(0.00001, 0.00002),
                p(0.00005, 0.00006)
            ),
            c.points
        )
    }


    internal class CollectedGeometryConsumer : SimpleFeature.GeometryConsumer<Untyped> {
        private val myPoints = ArrayList<Vec<Untyped>>()
        private val myLineStrings = ArrayList<LineString<Untyped>>()
        private val myPolygons = ArrayList<Polygon<Untyped>>()
        private val myMultiPoints = ArrayList<MultiPoint<Untyped>>()
        private val myMultiLineStrings = ArrayList<MultiLineString<Untyped>>()
        private val myMultiPolygons = ArrayList<MultiPolygon<Untyped>>()

        val points: List<Vec<Untyped>>
            get() = myPoints

        val lineStrings: List<LineString<Untyped>>
            get() = myLineStrings

        val polygons: List<Polygon<Untyped>>
            get() = myPolygons

        val multiPoints: List<MultiPoint<Untyped>>
            get() = myMultiPoints

        val multiLineStrings: List<MultiLineString<Untyped>>
            get() = myMultiLineStrings

        val multiPolygons: List<MultiPolygon<Untyped>>
            get() = myMultiPolygons

        override fun onPoint(point: Vec<Untyped>) {
            myPoints.add(point)
        }

        override fun onLineString(lineString: LineString<Untyped>) {
            myLineStrings.add(lineString)
        }

        override fun onPolygon(polygon: Polygon<Untyped>) {
            myPolygons.add(polygon)
        }

        override fun onMultiPoint(multiPoint: MultiPoint<Untyped>) {
            myMultiPoints.add(multiPoint)
        }

        override fun onMultiLineString(multiLineString: MultiLineString<Untyped>) {
            myMultiLineStrings.add(multiLineString)
        }

        override fun onMultiPolygon(multipolygon: MultiPolygon<Untyped>) {
            myMultiPolygons.add(multipolygon)
        }
    }

    internal class SimpleGeometryConsumer : SimpleFeature.GeometryConsumer<Untyped> {
        var point: Vec<Untyped>? = null
            private set
        var lineString: LineString<Untyped>? = null
            private set
        var polygon: Polygon<Untyped>? = null
            private set
        var multiPoint: MultiPoint<Untyped>? = null
            private set
        var multiLineString: MultiLineString<Untyped>? = null
            private set
        var multiPolygon: MultiPolygon<Untyped>? = null
            private set

        override fun onPoint(point: Vec<Untyped>) {
            this.point = point
        }

        override fun onLineString(lineString: LineString<Untyped>) {
            this.lineString = lineString
        }

        override fun onPolygon(polygon: Polygon<Untyped>) {
            this.polygon = polygon
        }

        override fun onMultiPoint(multiPoint: MultiPoint<Untyped>) {
            this.multiPoint = multiPoint
        }

        override fun onMultiLineString(multiLineString: MultiLineString<Untyped>) {
            this.multiLineString = multiLineString
        }

        override fun onMultiPolygon(multipolygon: MultiPolygon<Untyped>) {
            this.multiPolygon = multipolygon
        }
    }
}