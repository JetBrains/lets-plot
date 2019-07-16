package jetbrains.gis.tileprotocol.binary

import jetbrains.gis.common.twkb.VarInt
import jetbrains.gis.tileprotocol.GeometryCollection
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileLayerBuilder

expect fun byteArrayToUtf8String(bytes: ByteArray): String

class ResponseTileDecoder(private val myBytes: ByteArrayStream) {

    private val myTileLayerBuilder: TileLayerBuilder
    private val myKey: String
    private val myTileLayers: List<TileLayer>
    private var mySize: Int = 0

    init {
        mySize = myBytes.available()
        myTileLayerBuilder = TileLayerBuilder()
        myKey = readString()
        myTileLayers = readLayers()
    }

    fun getKey(): String {
        return myKey
    }

    fun getTileLayers(): List<TileLayer> {
        return myTileLayers
    }

    private fun readLayers(): List<TileLayer> {
        val layers = ArrayList<TileLayer>()

        while (myBytes.available() > 0) {
            layers.add(
                readName()
                    .readTwkb()
                    .readKinds()
                    .readSubs()
                    .readLabels()
                    .readShorts()
                    .setSize()
                    .build()
            )
        }

        return layers
    }

    private fun setSize(): ResponseTileDecoder {
        myTileLayerBuilder.setSize(mySize - myBytes.available())
        mySize = myBytes.available()
        return this
    }

    private fun readName(): ResponseTileDecoder {
        myTileLayerBuilder.setName(readString())
        return this
    }

    private fun build(): TileLayer {
        return myTileLayerBuilder.build()
    }

    private fun readShorts(): ResponseTileDecoder {
        myTileLayerBuilder.setShorts(readStrings())
        return this
    }

    private fun readLabels(): ResponseTileDecoder {
        myTileLayerBuilder.setLabels(readStrings())
        return this
    }

    private fun readSubs(): ResponseTileDecoder {
        myTileLayerBuilder.setSubs(readInts())
        return this
    }

    private fun readKinds(): ResponseTileDecoder {
        myTileLayerBuilder.setKinds(readInts())
        return this
    }

    private fun readInts(): List<Int> {
        val len = VarInt.readVarUInt(this::readByte)
        if (len == 0) {
            return emptyList()
        }

        val list = ArrayList<Int>()

        for (i in 0 until len) {
            list.add(VarInt.readVarUInt(this::readByte))
        }

        return list
    }

    private fun readStrings(): List<String> {
        val len = VarInt.readVarUInt(this::readByte)
        if (len == 0) {
            return emptyList()
        }

        val list = ArrayList<String>()

        for (i in 0 until len) {
            list.add(readString())
        }

        return list
    }

    private fun readTwkb(): ResponseTileDecoder {
        val len = VarInt.readVarUInt(this::readByte)

        myTileLayerBuilder.setGeometryCollection(GeometryCollection(myBytes.read(len)))
        return this
    }

    private fun readString(): String {
        val len = VarInt.readVarUInt(this::readByte)
        return if (len > 0) byteArrayToUtf8String(myBytes.read(len)) else ""

    }

    private fun readByte(): Int {
        return myBytes.read().toInt()
    }
}
