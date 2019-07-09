package jetbrains.gis.common.twkb

import jetbrains.gis.common.twkb.Twkb.GeometryConsumer
import jetbrains.gis.common.twkb.Twkb.GeometryType
import jetbrains.gis.common.twkb.Twkb.META_EMPTY_GEOMETRY_BIT
import jetbrains.gis.common.twkb.Twkb.META_ID_LIST_BIT
import jetbrains.gis.common.twkb.Twkb.assertNoMeta
import jetbrains.gis.common.twkb.Twkb.isMulti
import jetbrains.gis.common.twkb.Twkb.isSet
import jetbrains.gis.common.twkb.Twkb.precision
import jetbrains.gis.common.twkb.Twkb.type
import kotlin.math.pow

class Parser internal constructor(data: ByteArray, private val myGeometryConsumer: GeometryConsumer) {
    private val myInput: Input = Input(data)
    private var myGeometryObjectParser: GeometryObjectParser? = null

    private operator fun hasNext(): Boolean {
        return myInput.hasNext()
    }

    operator fun next(): Boolean {
        if (!hasNext()) {
            return false
        }

        if (myGeometryObjectParser == null || !myGeometryObjectParser!!.parsingObject()) {
            if (!startGeometryParsing()) {
                return false
            }
        }

        myGeometryObjectParser!!.parsePoint()
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
                val typeAndPrecision = myInput.readByte()
                geometryType = type(typeAndPrecision)
                precision = 10.0.pow(precision(typeAndPrecision))

                val meta = myInput.readByte()
                isEmpty = isSet(meta, META_EMPTY_GEOMETRY_BIT)
                if (!isEmpty) {
                    if (isMulti(type(typeAndPrecision))) {
                        nGeometries = myInput.readVarUInt()

                        if (isSet(meta, META_ID_LIST_BIT)) {
                            idList = ArrayList(nGeometries)
                            for (i in 0 until nGeometries) {
                                idList.add(myInput.readVarInt())
                            }
                        }
                    }
                }
                assertNoMeta(meta)
            }

            if (isEmpty) {
                continue
            }

            myGeometryObjectParser = GeometryObjectParser(precision, myInput)

            when (geometryType) {
                GeometryType.POINT -> {
                    myGeometryObjectParser!!.parsePoint(myGeometryConsumer::onPoint)
                    return true
                }

                GeometryType.LINESTRING -> {
                    myGeometryObjectParser!!.parseLineString(myGeometryConsumer::onLineString)
                    return true
                }

                GeometryType.POLYGON -> {
                    myGeometryObjectParser!!.parsePolygon(myGeometryConsumer::onPolygon)
                    return true
                }

                GeometryType.MULTI_POINT -> {
                    myGeometryObjectParser!!.parseMultiPoint(
                        nGeometries,
                        idList, myGeometryConsumer::onMultiPoint)
                    return true
                }

                GeometryType.MULTI_LINESTRING -> {
                    myGeometryObjectParser!!.parseMultiLine(
                        nGeometries,
                        idList, myGeometryConsumer::onMultiLineString)
                    return true
                }

                GeometryType.MULTI_POLYGON -> {
                    myGeometryObjectParser!!.pushMultiPolygon(
                        nGeometries,
                        idList, myGeometryConsumer::onMultiPolygon)
                    return true
                }

                GeometryType.GEOMETRY_COLLECTION -> {} // continue
            }
        }
        return false
    }
}
