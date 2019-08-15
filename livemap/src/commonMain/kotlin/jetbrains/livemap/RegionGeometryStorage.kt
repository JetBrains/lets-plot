package jetbrains.livemap

import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.livemap.searching.RegionGeometryProvider

class RegionGeometryStorage : RegionGeometryProvider, RegionGeometryConsumer {
    private val myLock = Any()
    private val myGeometryMap = HashMap<String, MultiPolygon>()
    private val mySynchronizedMap = HashMap<String, MultiPolygon>()

    override fun getGeometry(regionId: String): MultiPolygon {
        // return mySynchronizedMap.get(regionId)
        TODO()
    }

    fun synchronize() {
        TODO()
        //synchronized(myLock) {
        //    mySynchronizedMap.putAll(myGeometryMap)
        //    myGeometryMap.clear()
        //}
    }

    override fun updateGeometryMap(geometryMapChanges: Map<String, MultiPolygon>) {
        TODO()
        //synchronized(myLock) {
        //    myGeometryMap.putAll(geometryMapChanges)
        //}
    }
}