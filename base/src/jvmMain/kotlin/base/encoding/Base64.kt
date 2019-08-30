package jetbrains.datalore.base.encoding

actual object Base64 {
    actual fun decode(s: String): ByteArray {
        // Decoder throws exception on '\n' in src, use MimeDecoder
        return java.util.Base64.getMimeDecoder().decode(s)
    }
}