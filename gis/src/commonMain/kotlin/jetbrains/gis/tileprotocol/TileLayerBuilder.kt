package jetbrains.gis.tileprotocol

class TileLayerBuilder {
    private var myName = "NoName"
    private var myGeometryCollection = GeometryCollection.createEmpty()

    private var myKinds = emptyList<Int>()
    private var mySubs = emptyList<Int>()
    private var myLabels = emptyList<String>()
    private var myShorts = emptyList<String>()
    private var myLayerSize = 0

    fun build(): TileLayer {
        return MyTileLayer(this)
    }

    fun setName(name: String): TileLayerBuilder {
        myName = name
        return this
    }

    fun setGeometryCollection(v: GeometryCollection): TileLayerBuilder {
        myGeometryCollection = v
        return this
    }

    fun setKinds(v: List<Int>): TileLayerBuilder {
        myKinds = v
        return this
    }

    fun setSubs(v: List<Int>): TileLayerBuilder {
        mySubs = v
        return this
    }

    fun setLabels(v: List<String>): TileLayerBuilder {
        myLabels = v
        return this
    }

    fun setShorts(v: List<String>): TileLayerBuilder {
        myShorts = v
        return this
    }

    fun setSize(layerSize: Int): TileLayerBuilder {
        myLayerSize = layerSize
        return this
    }

    private class MyTileLayer internal constructor(builder: TileLayerBuilder) : TileLayer {
        override val name: String = builder.myName
        override val geometryCollection: GeometryCollection = builder.myGeometryCollection
        override val kinds: List<Int> = builder.myKinds
        override val subs: List<Int> = builder.mySubs
        override val labels: List<String> = builder.myLabels
        override val shorts: List<String> = builder.myShorts
        override val size: Int = builder.myLayerSize

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as MyTileLayer

            if (name != other.name) return false
            if (geometryCollection != other.geometryCollection) return false
            if (kinds != other.kinds) return false
            if (subs != other.subs) return false
            if (labels != other.labels) return false
            if (shorts != other.shorts) return false
            if (size != other.size) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + geometryCollection.hashCode()
            result = 31 * result + kinds.hashCode()
            result = 31 * result + subs.hashCode()
            result = 31 * result + labels.hashCode()
            result = 31 * result + shorts.hashCode()
            result = 31 * result + size
            return result
        }

        override fun toString(): String {
            return "MyTileLayer(name='$name', geometryCollection=$geometryCollection, kinds=$kinds, subs=$subs, labels=$labels, shorts=$shorts, size=$size)"
        }
    }
}
