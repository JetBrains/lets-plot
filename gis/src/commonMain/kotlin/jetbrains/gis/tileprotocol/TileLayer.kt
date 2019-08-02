package jetbrains.gis.tileprotocol

data class TileLayer internal constructor(
    val name: String,
    val geometryCollection: GeometryCollection,
    val kinds: List<Int>,
    val subs: List<Int>,
    val labels: List<String?>,
    val shorts: List<String>,
    val size: Int
)
