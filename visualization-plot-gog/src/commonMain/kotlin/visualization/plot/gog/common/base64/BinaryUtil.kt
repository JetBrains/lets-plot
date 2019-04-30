package jetbrains.datalore.visualization.plot.gog.common.base64

expect object BinaryUtil {
    fun decodeList(s: String): List<Double>
}
