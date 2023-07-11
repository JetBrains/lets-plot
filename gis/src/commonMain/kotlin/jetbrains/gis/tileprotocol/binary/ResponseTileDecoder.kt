/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol.binary

import jetbrains.gis.common.twkb.VarInt.readVarUInt
import jetbrains.gis.tileprotocol.GeometryCollection
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileLayerBuilder

class ResponseTileDecoder(data: ByteArray) {
    private val byteArrayStream = ByteArrayStream(data)
    private val key = readString()
    private val tileLayers = readLayers()

    private fun readLayers(): List<TileLayer> {
        val layers = ArrayList<TileLayer>()

        do {
            val layerStartPosition = byteArrayStream.available()

            TileLayerBuilder()
                .apply {
                    name = readString()
                    geometryCollection = readVarUInt(::readByte)    // take length
                        .run(byteArrayStream::read)                 // take geometry byte array
                        .run(::GeometryCollection)
                    kinds = readInts()
                    subs = readInts()
                    labels = readStrings()
                    shorts = readStrings()
                    layerSize = layerStartPosition - byteArrayStream.available()
                }
                .build()
                .run(layers::add)

        } while (byteArrayStream.available() > 0)

        return layers
    }

    private fun readInts(): List<Int> {
        val len: Int = readVarUInt(::readByte)

        return when {
            len > 0 -> (0 until len).map { readVarUInt(::readByte) }
            len == 0 -> emptyList()
            else -> throw IllegalStateException()
        }
    }

    private fun readStrings(): List<String> {
        val len: Int = readVarUInt(::readByte)

        return when {
            len > 0 -> (0 until len).map { readString() }
            len == 0 -> emptyList()
            else -> throw IllegalStateException()
        }
    }

    private fun readString(): String {
        val len: Int = readVarUInt(::readByte)

        return when {
            len > 0 -> byteArrayStream.read(len).decodeToString()
            len == 0 -> ""
            else -> throw IllegalStateException()
        }
    }

    private fun readByte(): Int = byteArrayStream.read().toInt()

    operator fun component1(): String = key

    operator fun component2(): List<TileLayer> = tileLayers
}
