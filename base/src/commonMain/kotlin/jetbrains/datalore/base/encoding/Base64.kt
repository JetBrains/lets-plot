package jetbrains.datalore.base.encoding

expect object Base64 {
    fun decode(s: String): ByteArray
}