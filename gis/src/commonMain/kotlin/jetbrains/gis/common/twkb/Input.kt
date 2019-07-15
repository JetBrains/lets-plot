package jetbrains.gis.common.twkb

internal class Input(private val myData: ByteArray) {
    private var myPointer = 0

    operator fun hasNext(): Boolean {
        return myPointer < myData.size
    }

    fun readByte(): Int {
        return myData[myPointer++].toInt()
    }

    fun readVarInt(): Int {
        return VarInt.readVarInt(this::readByte)
    }

    fun readVarUInt(): Int {
        return VarInt.readVarUInt(this::readByte)
    }
}
