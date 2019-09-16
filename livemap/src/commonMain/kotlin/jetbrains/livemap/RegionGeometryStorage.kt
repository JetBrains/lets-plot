package jetbrains.livemap

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.livemap.searching.RegionGeometryProvider

class RegionGeometryStorage : RegionGeometryProvider, RegionGeometryConsumer {
    private val myLock = Any()
    private val myGeometryMap = HashMap<String, MultiPolygon<Generic>>()
    private val mySynchronizedMap = HashMap<String, MultiPolygon<Generic>>()

    override fun getGeometry(regionId: String): MultiPolygon<Generic> {
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

    override fun updateGeometryMap(geometryMapChanges: Map<String, MultiPolygon<Generic>>) {
        TODO()
        //synchronized(myLock) {
        //    myGeometryMap.putAll(geometryMapChanges)
        //}
    }
}