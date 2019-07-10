package jetbrains.gis.tileprotocol.binary

internal actual fun byteArrayToUtf8String(bytes: ByteArray): String {
    return String(bytes, Charsets.UTF_8)
}