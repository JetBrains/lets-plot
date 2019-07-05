package jetbrains.gis.tileprotocol

import java.util.Collections
import java.util.Objects

class TileLayerBuilder {
    private var myName: String? = null
    private var myGeometryCollection: GeometryCollection? = null

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
        val name: String?
        val geometryCollection: GeometryCollection?
        val kinds: List<Int>
        val subs: List<Int>
        val labels: List<String>
        val shorts: List<String>
        private val mySize: Int

        val size: Int?
            get() = mySize

        init {
            name = builder.myName
            geometryCollection = builder.myGeometryCollection
            kinds = builder.myKinds
            subs = builder.mySubs
            labels = builder.myLabels
            shorts = builder.myShorts
            mySize = builder.myLayerSize
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val that = o as MyTileLayer?
            return geometryCollection == that!!.geometryCollection &&
                    kinds == that.kinds &&
                    subs == that.subs &&
                    labels == that.labels &&
                    shorts == that.shorts
        }

        override fun hashCode(): Int {
            return Objects.hash(geometryCollection, kinds, subs, labels, shorts)
        }

        override fun toString(): String {
            return "MyTileLayer{" +
                    "myGeometryCollection=$geometryCollection, " +
                    "myKinds=$kinds, " +
                    "mySubs=$subs, " +
                    "myLabels=$labels, " +
                    "myShorts=$shorts}"
        }
    }
}
