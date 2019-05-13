package jetbrains.datalore.visualization.plot.gog.common.base64

expect object BinaryUtil {
    fun encodeList(l: List<Double?>): String
    fun decodeList(s: String): List<Double>
}
