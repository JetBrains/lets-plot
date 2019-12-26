/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.common.twkb

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.function.Functions
import jetbrains.datalore.base.function.Functions.funcOf
import jetbrains.datalore.base.gcommon.collect.Stack
import jetbrains.datalore.base.spatial.SimpleFeature
import jetbrains.datalore.base.typedGeometry.*

internal class SimpleFeatureParser(
    private val myPrecision: Double,
    private val myInputBuffer: InputBuffer,
    private val myGeometryConsumer: SimpleFeature.GeometryConsumer<Generic>
) {

    private val myParsers = Stack<GeometryParser>()
    private var x = 0
    private var y = 0

    internal fun readPoint(): Vec<Generic> {
        x += myInputBuffer.readVarInt()
        y += myInputBuffer.readVarInt()
        return explicitVec<Generic>(x / myPrecision, y / myPrecision)
    }

    fun parsingObject(): Boolean  = !myParsers.empty()

    fun parsePoint() {
        myParsers.push(PointParser(myGeometryConsumer::onPoint, this))
    }

    fun parseLineString() {
        myParsers.push(
            PointsParser(
                { myGeometryConsumer.onLineString(LineString(it)) },
                this
            )
        )
    }

    fun parsePolygon() {
        myParsers.push(
            PolygonParser(
                { myGeometryConsumer.onPolygon(Polygon(it)) },
                this
            )
        )
    }

    fun parseMultiPoint(n: Int, ids: List<Int>) {
        myParsers.push(
            MultiPointParser(
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
            MultiLineStringParser(
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
            MultiPolygonParser(
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
        private val myCtx: SimpleFeatureParser
    ) {

        internal abstract fun parseNext()

        fun popThisParser() {
            val removed = myCtx.popParser()
            if (removed !== this) {
                throw IllegalStateException()
            }
        }

        fun readPoint() = myCtx.readPoint()

        fun pushParser(parser: GeometryParser) {
            myCtx.myParsers.push(parser)
        }
    }

    private class PointParser(
        private val myParsingResultConsumer: Consumer<Vec<Generic>>,
        ctx: SimpleFeatureParser
    ) : GeometryParser(ctx) {
        private lateinit var myP: Vec<Generic>

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
        ctx: SimpleFeatureParser
    ) : GeometryParser(ctx) {
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
        parsingResultConsumer: Consumer<List<Vec<Generic>>>,
        ctx: SimpleFeatureParser
    ) : GeometryListParser<Vec<Generic>>(ctx.readCount(), parsingResultConsumer, ctx) {

        override fun parseNext() {
            addGeometry(readPoint())

            if (allRead()) {
                done()
            }
        }
    }

    private open class NestedGeometryParser<NestedGeometriesT, GeometryT>(
        count: Int,
        private val myNestedParserFactory: Function<Consumer<NestedGeometriesT>, GeometryParser>,
        private val myNestedToGeometry: Function<NestedGeometriesT, GeometryT>,
        parsingResultConsumer: Consumer<List<GeometryT>>,
        ctx: SimpleFeatureParser
    ) : GeometryListParser<GeometryT>(count, parsingResultConsumer, ctx) {

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
        parsingResultConsumer: Consumer<List<Ring<Generic>>>,
        ctx: SimpleFeatureParser
    ) :
        NestedGeometryParser<List<Vec<Generic>>, Ring<Generic>>(
            ctx.readCount(),
            funcOf { points -> PointsParser(points, ctx) },
            funcOf(::Ring),
            parsingResultConsumer,
            ctx
        )

    private class MultiPointParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<Vec<Generic>>>,
        ctx: SimpleFeatureParser
    ) : NestedGeometryParser<Vec<Generic>, Vec<Generic>>(
        nGeometries,
        funcOf { point -> PointParser(point, ctx) },
        funcOf(Functions.identity()),
        parsingResultConsumer,
        ctx
    )

    private class MultiLineStringParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<LineString<Generic>>>,
        ctx: SimpleFeatureParser
    ) : NestedGeometryParser<List<Vec<Generic>>, LineString<Generic>>(
        nGeometries,
        funcOf { points -> PointsParser(points, ctx) },
        funcOf(::LineString),
        parsingResultConsumer,
        ctx
    )

    private class MultiPolygonParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<Polygon<Generic>>>,
        ctx: SimpleFeatureParser
    ) : NestedGeometryParser<List<Ring<Generic>>, Polygon<Generic>>(
        nGeometries,
        funcOf { ringsConsumer -> PolygonParser(ringsConsumer, ctx) },
        funcOf(::Polygon),
        parsingResultConsumer,
        ctx
    )
}
