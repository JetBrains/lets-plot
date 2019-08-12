package jetbrains.datalore.base.encoding

actual object Base64 {
    actual fun decode(s: String) = java.util.Base64.getMimeDecoder().decode(s)
}