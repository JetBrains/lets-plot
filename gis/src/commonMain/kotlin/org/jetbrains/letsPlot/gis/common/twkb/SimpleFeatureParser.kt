/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.common.twkb

import org.jetbrains.letsPlot.commons.intern.function.Consumer
import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.intern.function.Functions
import org.jetbrains.letsPlot.commons.intern.function.Functions.funcOf
import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Stack
import org.jetbrains.letsPlot.commons.intern.spatial.SimpleFeature
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*

internal class SimpleFeatureParser(
    private val myPrecision: Double,
    private val myInputBuffer: org.jetbrains.letsPlot.gis.common.twkb.InputBuffer,
    private val myGeometryConsumer: SimpleFeature.GeometryConsumer<Untyped>
) {

    private val myParsers = Stack<org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.GeometryParser>()
    private var x = 0
    private var y = 0

    internal fun readPoint(): Vec<Untyped> {
        x += myInputBuffer.readVarInt()
        y += myInputBuffer.readVarInt()
        return explicitVec<Untyped>(x / myPrecision, y / myPrecision)
    }

    fun parsingObject(): Boolean  = !myParsers.empty()

    fun parsePoint() {
        myParsers.push(
            org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.PointParser(
                myGeometryConsumer::onPoint,
                this
            )
        )
    }

    fun parseLineString() {
        myParsers.push(
            org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.PointsParser(
                { myGeometryConsumer.onLineString(LineString(it)) },
                this
            )
        )
    }

    fun parsePolygon() {
        myParsers.push(
            org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.PolygonParser(
                { myGeometryConsumer.onPolygon(Polygon(it)) },
                this
            )
        )
    }

    fun parseMultiPoint(n: Int, ids: List<Int>) {
        myParsers.push(
            org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.MultiPointParser(
                n,
                { multiPoint ->
                    if (ids.isEmpty()) {
                        myGeometryConsumer.onMultiPoint(MultiPoint(multiPoint))
                    } else {
                        multiPoint.forEach(myGeometryConsumer::onPoint)
                    }
                },
                this
            )
        )
    }

    fun parseMultiLine(n: Int, ids: List<Int>) {
        myParsers.push(
            org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.MultiLineStringParser(
                n,
                {
                    if (ids.isEmpty()) {
                        myGeometryConsumer.onMultiLineString(MultiLineString(it))
                    } else {
                        it.forEach(myGeometryConsumer::onLineString)
                    }
                },
                this
            )
        )
    }

    fun pushMultiPolygon(n: Int, ids: List<Int>) {
        myParsers.push(
            org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.MultiPolygonParser(
                n,
                {
                    if (ids.isEmpty()) {
                        myGeometryConsumer.onMultiPolygon(MultiPolygon(it))
                    } else {
                        it.forEach(myGeometryConsumer::onPolygon)
                    }
                },
                this
            )
        )
    }

    private fun readCount() = myInputBuffer.readVarUInt()
    internal fun nextCoordinate() { myParsers.peek()?.parseNext() }
    private fun popParser() = myParsers.pop() ?: error("No more parsers")

    private abstract class GeometryParser(
        private val myCtx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) {

        internal abstract fun parseNext()

        fun popThisParser() {
            val removed = myCtx.popParser()
            if (removed !== this) {
                throw IllegalStateException()
            }
        }

        fun readPoint() = myCtx.readPoint()

        fun pushParser(parser: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.GeometryParser) {
            myCtx.myParsers.push(parser)
        }
    }

    private class PointParser(
        private val myParsingResultConsumer: Consumer<Vec<Untyped>>,
        ctx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) : org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.GeometryParser(ctx) {
        private lateinit var myP: Vec<Untyped>

        override fun parseNext() {
            myP = readPoint()
            done()
        }

        private fun done() {
            popThisParser()
            myParsingResultConsumer(myP)
        }
    }

    private abstract class GeometryListParser<GeometryT>(
        private val myCount: Int,
        private val myParsingResultConsumer: Consumer<List<GeometryT>>,
        ctx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) : org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.GeometryParser(ctx) {
        private val myGeometries = ArrayList<GeometryT>(myCount)

        fun addGeometry(geometry: GeometryT) {
            myGeometries.add(geometry)
        }

        fun allRead() = myGeometries.size == myCount

        fun done() {
            popThisParser()
            myParsingResultConsumer(myGeometries)
        }
    }

    private class PointsParser(
        parsingResultConsumer: Consumer<List<Vec<Untyped>>>,
        ctx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) : org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.GeometryListParser<Vec<Untyped>>(ctx.readCount(), parsingResultConsumer, ctx) {

        override fun parseNext() {
            addGeometry(readPoint())

            if (allRead()) {
                done()
            }
        }
    }

    private open class NestedGeometryParser<NestedGeometriesT, GeometryT>(
        count: Int,
        private val myNestedParserFactory: Function<Consumer<NestedGeometriesT>, org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.GeometryParser>,
        private val myNestedToGeometry: Function<NestedGeometriesT, GeometryT>,
        parsingResultConsumer: Consumer<List<GeometryT>>,
        ctx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) : org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.GeometryListParser<GeometryT>(count, parsingResultConsumer, ctx) {

        override fun parseNext() {
            pushParser(myNestedParserFactory.apply(this::onNestedParsed))
        }

        private fun onNestedParsed(nestedGeometries: NestedGeometriesT) {
            addGeometry(myNestedToGeometry.apply(nestedGeometries))

            if (allRead()) {
                done()
            }
        }
    }

    private class PolygonParser(
        parsingResultConsumer: Consumer<List<Ring<Untyped>>>,
        ctx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) :
        org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.NestedGeometryParser<List<Vec<Untyped>>, Ring<Untyped>>(
            ctx.readCount(),
            funcOf { points -> org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.PointsParser(points, ctx) },
            funcOf(::Ring),
            parsingResultConsumer,
            ctx
        )

    private class MultiPointParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<Vec<Untyped>>>,
        ctx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) : org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.NestedGeometryParser<Vec<Untyped>, Vec<Untyped>>(
        nGeometries,
        funcOf { point -> org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.PointParser(point, ctx) },
        funcOf(Functions.identity()),
        parsingResultConsumer,
        ctx
    )

    private class MultiLineStringParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<LineString<Untyped>>>,
        ctx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) : org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.NestedGeometryParser<List<Vec<Untyped>>, LineString<Untyped>>(
        nGeometries,
        funcOf { points -> org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.PointsParser(points, ctx) },
        funcOf(::LineString),
        parsingResultConsumer,
        ctx
    )

    private class MultiPolygonParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<Polygon<Untyped>>>,
        ctx: org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser
    ) : org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.NestedGeometryParser<List<Ring<Untyped>>, Polygon<Untyped>>(
        nGeometries,
        funcOf { ringsConsumer ->
            org.jetbrains.letsPlot.gis.common.twkb.SimpleFeatureParser.PolygonParser(
                ringsConsumer,
                ctx
            )
        },
        funcOf(::Polygon),
        parsingResultConsumer,
        ctx
    )
}
