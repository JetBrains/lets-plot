package jetbrains.gis.tileprotocol.binary

actual fun byteArrayToUtf8String(bytes: ByteArray): String {
    return bytes.toString() // todo
}