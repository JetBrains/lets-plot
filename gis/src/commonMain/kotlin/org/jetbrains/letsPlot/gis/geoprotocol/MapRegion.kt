/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol

class MapRegion private constructor(private val myKind: MapRegionKind, valueList: List<String>) {
    private val myValueList: List<String>

    val idList: List<String>
        get() {
            require(containsId()) { "Can't get ids from MapRegion with name" }

            return myValueList
        }

    val name: String
        get() {
            require(containsName()) { "Can't get name from MapRegion with ids" }
            require(myValueList.size == 1) { "MapRegion should contain one name" }

            return myValueList[0]
        }

    init {
        myValueList = ArrayList(valueList)
    }

    fun containsId(): Boolean {
        return myKind == MapRegionKind.MAP_REGION_KIND_ID
    }

    fun containsName(): Boolean {
        return myKind == MapRegionKind.MAP_REGION_KIND_NAME
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MapRegion

        if (myKind != other.myKind) return false
        if (myValueList != other.myValueList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = myKind.hashCode()
        result = 31 * result + myValueList.hashCode()
        return result
    }


    private enum class MapRegionKind {
        MAP_REGION_KIND_ID,
        MAP_REGION_KIND_NAME
    }

    companion object {
        private const val US_48_NAME = "us-48"
        private val US_48 = MapRegion(MapRegionKind.MAP_REGION_KIND_NAME, listOf(US_48_NAME))
        private const val US_48_PARENT_NAME = "United States of America"
        val US_48_PARENT = MapRegion(MapRegionKind.MAP_REGION_KIND_NAME, listOf(US_48_PARENT_NAME))

        fun withIdList(regionIdList: List<String>): MapRegion {
            return MapRegion(MapRegionKind.MAP_REGION_KIND_ID, regionIdList)
        }

        fun withId(regionId: String): MapRegion {
            return MapRegion(MapRegionKind.MAP_REGION_KIND_ID, listOf(regionId))
        }

        fun withName(regionName: String): MapRegion {
            // Single instance of a region with name US_48_NAME
            return if (US_48_NAME.equals(regionName, ignoreCase = true)) {
                US_48
            } else MapRegion(
                MapRegionKind.MAP_REGION_KIND_NAME,
                listOf(regionName)
            )

        }
    }
}
