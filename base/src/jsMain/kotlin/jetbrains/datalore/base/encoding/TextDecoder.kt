package jetbrains.datalore.base.encoding

actual class TextDecoder actual constructor() {
    actual fun decode(bytes: ByteArray): String {
        return js("new TextDecoder()").decode(bytes)
    }
}