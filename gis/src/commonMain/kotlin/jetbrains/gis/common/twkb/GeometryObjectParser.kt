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

internal class GeometryObjectParser(
    precision: Double,
    input: Input,
    private val myGeometryConsumer: SimpleFeature.GeometryConsumer
) {
    private val myGeometryStream = GeometryStream(precision, input)
    private val myParsers = Stack<GeometryParser>()

    fun nextCoordinate() {
        myParsers.peek()?.parseNext()
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

    private fun readCount() = myGeometryStream.readCount()
    private fun readPoint() = myGeometryStream.readPoint()
    private fun popParser() = myParsers.pop() ?: error("No more parsers")

    internal abstract class GeometryParser(
        private val myCtx: GeometryObjectParser
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

    internal class PointParser(
        private val myParsingResultConsumer: Consumer<Vec<Generic>>,
        ctx: GeometryObjectParser
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

    internal abstract class GeometryListParser<GeometryT>(
        private val myCount: Int,
        private val myParsingResultConsumer: Consumer<List<GeometryT>>,
        ctx: GeometryObjectParser
    ) : GeometryParser(ctx) {
        private val myGeometries = ArrayList<GeometryT>(myCount)

        private val geometries: List<GeometryT>
            get() = myGeometries

        fun addGeometry(geometry: GeometryT) {
            myGeometries.add(geometry)
        }

        fun allRead() = myGeometries.size == myCount

        fun done() {
            popThisParser()
            myParsingResultConsumer(geometries)
        }
    }

    internal class PointsParser(
        parsingResultConsumer: Consumer<List<Vec<Generic>>>,
        ctx: GeometryObjectParser
    ) : GeometryListParser<Vec<Generic>>(ctx.readCount(), parsingResultConsumer, ctx) {

        override fun parseNext() {
            addGeometry(readPoint())

            if (allRead()) {
                done()
            }
        }
    }

    internal open class NestedGeometryParser<NestedGeometriesT, GeometryT>(
        count: Int,
        private val myNestedParserFactory: Function<Consumer<NestedGeometriesT>, GeometryParser>,
        private val myNestedToGeometry: Function<NestedGeometriesT, GeometryT>,
        parsingResultConsumer: Consumer<List<GeometryT>>,
        ctx: GeometryObjectParser
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

    internal class PolygonParser(
        parsingResultConsumer: Consumer<List<Ring<Generic>>>,
        ctx: GeometryObjectParser
    ) :
        NestedGeometryParser<List<Vec<Generic>>, Ring<Generic>>(
            ctx.readCount(),
            funcOf { points -> PointsParser(points, ctx) },
            funcOf(::Ring),
            parsingResultConsumer,
            ctx
        )

    internal class MultiPointParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<Vec<Generic>>>,
        ctx: GeometryObjectParser
    ) : NestedGeometryParser<Vec<Generic>, Vec<Generic>>(
        nGeometries,
        funcOf { point -> PointParser(point, ctx) },
        funcOf(Functions.identity()),
        parsingResultConsumer,
        ctx
    )

    internal class MultiLineStringParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<LineString<Generic>>>,
        ctx: GeometryObjectParser
    ) : NestedGeometryParser<List<Vec<Generic>>, LineString<Generic>>(
        nGeometries,
        funcOf { points -> PointsParser(points, ctx) },
        funcOf(::LineString),
        parsingResultConsumer,
        ctx
    )

    internal class MultiPolygonParser(
        nGeometries: Int,
        parsingResultConsumer: Consumer<List<Polygon<Generic>>>,
        ctx: GeometryObjectParser
    ) : NestedGeometryParser<List<Ring<Generic>>, Polygon<Generic>>(
        nGeometries,
        funcOf { ringsConsumer -> PolygonParser(ringsConsumer, ctx) },
        funcOf(::Polygon),
        parsingResultConsumer,
        ctx
    )

    internal class GeometryStream(
        private val myPrecision: Double,
        private val myInput: Input
    ) {
        private var x = 0
        private var y = 0

        fun readPoint(): Vec<Generic> {
            x += myInput.readVarInt()
            y += myInput.readVarInt()
            return explicitVec<Generic>(x / myPrecision, y / myPrecision)
        }

        fun readCount(): Int {
            return myInput.readVarUInt()
        }
    }
}
