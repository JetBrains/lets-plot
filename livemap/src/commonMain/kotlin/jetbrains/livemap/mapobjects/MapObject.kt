package jetbrains.livemap.mapobjects

open class MapObject(
    val index: Int,
    var mapId: String?,
    regionId: String?
) {
    var regionId = regionId
        set(value) {
            require(value?.let {it.toIntOrNull() != null} ?: true) { "regionId should be a number" }
            field = value
        }
}