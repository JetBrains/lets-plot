/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.common.twkb

import org.jetbrains.letsPlot.commons.intern.spatial.SimpleFeature
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import jetbrains.gis.common.twkb.VarInt.decodeZigZag
import kotlin.math.pow

object Twkb {

    fun parse(data: ByteArray, geometryConsumer: SimpleFeature.GeometryConsumer<Untyped>) {
        val parser = Parser(data, geometryConsumer)
        while (parser.next()) {
            continue // parse all untill the end
        }
    }

    fun parser(data: ByteArray, geometryConsumer: SimpleFeature.GeometryConsumer<Untyped>): Parser {
        return Parser(data, geometryConsumer)
    }

    class Parser internal constructor(
        data: ByteArray,
        private val myGeometryConsumer: SimpleFeature.GeometryConsumer<Untyped>
    ) {
        private val myInputBuffer: InputBuffer = InputBuffer(data)
        private var myFeatureParser: SimpleFeatureParser? = null

        private operator fun hasNext(): Boolean {
            return myInputBuffer.hasNext()
        }

        operator fun next(): Boolean {
            if (!hasNext()) {
                return false
            }

            if (myFeatureParser == null || !myFeatureParser!!.parsingObject()) {
                if (!startGeometryParsing()) {
                    return false
                }
            }

            myFeatureParser!!.nextCoordinate()
            return true
        }

        private fun startGeometryParsing(): Boolean   {

            while (hasNext()) {
                val geometryType: GeometryType
                val isEmpty: Boolean
                val precision: Double
                var nGeometries = 0
                var idList = arrayListOf<Int>()

                run {
                    // read geometry header
                    val typeAndPrecision = myInputBuffer.readByte()
                    geometryType = type(typeAndPrecision)
                    precision = 10.0.pow(precision(typeAndPrecision))

                    val meta = myInputBuffer.readByte()
                    isEmpty = isSet(meta, META_EMPTY_GEOMETRY_BIT)
                    if (!isEmpty) {
                        if (isMulti(type(typeAndPrecision))) {
                            nGeometries = myInputBuffer.readVarUInt()

                            if (isSet(meta, META_ID_LIST_BIT)) {
                                idList = ArrayList(nGeometries)
                                for (i in 0 until nGeometries) {
                                    idList.add(myInputBuffer.readVarInt())
                                }
                            }
                        }
                    }
                    assertNoMeta(meta)
                }

                if (isEmpty) {
                    continue
                }

                myFeatureParser = SimpleFeatureParser(precision, myInputBuffer, myGeometryConsumer)
                when (geometryType) {
                    GeometryType.POINT -> {
                        myFeatureParser!!.parsePoint()
                        return true
                    }

                    GeometryType.LINESTRING -> {
                        myFeatureParser!!.parseLineString()
                        return true
                    }

                    GeometryType.POLYGON -> {
                        myFeatureParser!!.parsePolygon()
                        return true
                    }

                    GeometryType.MULTI_POINT -> {
                        myFeatureParser!!.parseMultiPoint(
                            nGeometries,
                            idList
                        )
                        return true
                    }

                    GeometryType.MULTI_LINESTRING -> {
                        myFeatureParser!!.parseMultiLine(
                            nGeometries,
                            idList
                        )
                        return true
                    }

                    GeometryType.MULTI_POLYGON -> {
                        myFeatureParser!!.pushMultiPolygon(
                            nGeometries,
                            idList
                        )
                        return true
                    }

                    GeometryType.GEOMETRY_COLLECTION -> {} // continue
                }
            }
            return false
        }


        private fun isSet(b: Int, bit: Int): Boolean {
            return b and (1 shl bit) != 0
        }

        private fun isMulti(type: GeometryType): Boolean {
            return (type == GeometryType.MULTI_POINT
                    || type == GeometryType.MULTI_LINESTRING
                    || type == GeometryType.MULTI_POLYGON
                    || type == GeometryType.GEOMETRY_COLLECTION)
        }

        private fun precision(typeAndPrec: Int): Int {
            return decodeZigZag(typeAndPrec and 0xF0 shr 4)
        }

        internal fun type(typeAndPrec: Int): GeometryType {
            return GeometryType.fromCode(typeAndPrec and 0x0F)
        }

        private fun assertNoMeta(meta: Int) {
            if (isSet(meta, META_EXTRA_PRECISION_BIT)) {
                throw IllegalStateException("META_EXTRA_PRECISION_BIT is not supported")
            }

            if (isSet(meta, META_SIZE_BIT)) {
                throw IllegalStateException("META_SIZE_BIT is not supported")
            }

            if (isSet(meta, META_BBOX_BIT)) {
                throw IllegalStateException("META_BBOX_BIT is not supported")
            }
        }

        enum class GeometryType {
            POINT,
            LINESTRING,
            POLYGON,
            MULTI_POINT,
            MULTI_LINESTRING,
            MULTI_POLYGON,
            GEOMETRY_COLLECTION;


            companion object {

                internal fun fromCode(code: Int): GeometryType {
                    when (code) {
                        1 -> return POINT

                        2 -> return LINESTRING

                        3 -> return POLYGON

                        4 -> return MULTI_POINT

                        5 -> return MULTI_LINESTRING

                        6 -> return MULTI_POLYGON

                        7 -> return GEOMETRY_COLLECTION

                        else -> throw IllegalArgumentException("Unkown geometry type: $code")
                    }
                }
            }
        }

    }
    private const val META_ID_LIST_BIT: Int = 2
    private const val META_EMPTY_GEOMETRY_BIT: Int = 4
    private const val META_BBOX_BIT: Int = 0
    private const val META_SIZE_BIT: Int = 1
    private const val META_EXTRA_PRECISION_BIT: Int = 3

}
