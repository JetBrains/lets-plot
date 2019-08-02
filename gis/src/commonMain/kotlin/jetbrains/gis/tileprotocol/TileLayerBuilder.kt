package jetbrains.gis.tileprotocol

class TileLayerBuilder {
    var name = "NoName"
    var geometryCollection = GeometryCollection.createEmpty()

    var kinds = emptyList<Int>()
    var subs = emptyList<Int>()
    var labels = emptyList<String?>()
    var shorts = emptyList<String>()
    var layerSize = 0

    fun build(): TileLayer {
        return TileLayer(
            name,
            geometryCollection,
            kinds,
            subs,
            labels,
            shorts,
            layerSize
        )
    }
}
