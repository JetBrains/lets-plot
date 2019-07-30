package jetbrains.datalore.base.encoding

expect class TextDecoder() {
    fun decode(bytes: ByteArray): String
}