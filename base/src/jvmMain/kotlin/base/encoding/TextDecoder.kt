package jetbrains.datalore.base.encoding

actual class TextDecoder actual constructor() {
    actual fun decode(bytes: ByteArray): String {
        return String(bytes, Charsets.UTF_8)
    }
}