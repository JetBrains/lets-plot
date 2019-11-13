/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.common.twkb

import jetbrains.datalore.base.function.BiConsumer
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.function.Functions
import jetbrains.datalore.base.function.Functions.funcOf
import jetbrains.datalore.base.gcommon.collect.Stack
import jetbrains.datalore.base.projectionGeometry.*

internal class GeometryObjectParser(precision: Double, input: Input) {
    private val myGeometryStream: GeometryStream
    private val myParsers = Stack<GeometryParser>()

    init {
        myGeometryStream = GeometryStream(precision, input)
    }

    fun parsePoint() {
        myParsers.peek()?.parsePoint()
    }

    fun parsingObject(): Boolean {
        return !myParsers.empty()
    }

    fun parsePoint(onParse: Consumer<Vec<Generic>>) {
        pushParser(PointParser(onParse, this))
    }

    fun parseLineString(onParse: Consumer<LineString<Generic>>) {
        pushParser(
            PointsParser(
                { points -> onParse(LineString<Generic>(points)) },
                this
            )
        )
    }

    fun parsePolygon(onParse: Consumer<Polygon<Generic>>) {
        pushParser(PolygonParser({ rings -> onParse(Polygon<Generic>(rings)) }, this))
    }

    fun parseMultiPoint(n: Int, ids: List<Int>, onParse: BiConsumer<MultiPoint<Generic>, List<Int>>) {
        pushParser(
            MultiPointParser(
                n,
                { points -> onParse(MultiPoint<Generic>(points), ids) },
                this
            )
        )
    }

    fun parseMultiLine(n: Int, ids: List<Int>, onParse: BiConsumer<MultiLineString<Generic>, List<Int>>) {
        pushParser(
            MultiLineStringParser(
                n,
                { points -> onParse(MultiLineString<Generic>(points), ids) },
                this
            )
        )
    }

    fun pushMultiPolygon(n: Int, ids: List<Int>, onParse: BiConsumer<MultiPolygon<Generic>, List<Int>>) {
        pushParser(
            MultiPolygonParser(
                n,
                { points -> onParse(MultiPolygon<Generic>(points), ids) },
                this
            )
        )
    }

    private fun readCount(): Int {
        return myGeometryStream.readCount()
    }

    private fun readPoint(): Vec<Generic> {
        return myGeometryStream.readPoint()
    }

    private fun pushParser(parser: GeometryParser) {
        myParsers.push(parser)
    }

    private fun popParser(): GeometryParser {
        val a = myParsers.pop()
        if (a !== null) {
            return a
        } else {
            throw IllegalStateException()
        }
    }

    internal abstract class GeometryParser(private val myCtx: GeometryObjectParser) {

        internal abstract fun parsePoint()

        fun popThisParser() {
            val removed = myCtx.popParser()
            if (removed !== this) {
                throw IllegalStateException()
            }
        }

        fun readPoint(): Vec<Generic> {
            return myCtx.readPoint()
        }

        fun pushParser(parser: GeometryParser) {
            myCtx.pushParser(parser)
        }
    }

    internal class PointParser(private val myParsingResultConsumer: Consumer<Vec<Generic>>, ctx: GeometryObjectParser) :
        GeometryParser(ctx) {
        private lateinit var myP: Vec<Generic>

        override fun parsePoint() {
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
        private val myGeometries: MutableList<GeometryT>

        private val geometries: List<GeometryT>
            get() = myGeometries

        init {
            myGeometries = ArrayList(myCount)
        }

        fun addGeometry(geometry: GeometryT) {
            myGeometries.add(geometry)
        }

        fun allRead(): Boolean {
            return myGeometries.size == myCount
        }

        fun done() {
            popThisParser()
            myParsingResultConsumer(geometries)
        }
    }

    internal class PointsParser(parsingResultConsumer: Consumer<List<Vec<Generic>>>, ctx: GeometryObjectParser) :
        GeometryListParser<Vec<Generic>>(ctx.readCount(), parsingResultConsumer, ctx) {

        override fun parsePoint() {
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

        override fun parsePoint() {
            pushParser(myNestedParserFactory.apply(this::onNestedParsed))
        }

        private fun onNestedParsed(nestedGeometries: NestedGeometriesT) {
            addGeometry(myNestedToGeometry.apply(nestedGeometries))

            if (allRead()) {
                done()
            }
        }
    }

    internal class PolygonParser(parsingResultConsumer: Consumer<List<Ring<Generic>>>, ctx: GeometryObjectParser) :
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

    internal class GeometryStream(private val myPrecision: Double, private val myInput: Input) {
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
