/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol

enum class LevelOfDetails {
    CITY_HIGH,
    CITY_MEDIUM,
    CITY_LOW,
    COUNTY_HIGH,
    COUNTY_MEDIUM,
    COUNTY_LOW,
    STATE_HIGH,
    STATE_MEDIUM,
    STATE_LOW,
    COUNTRY_HIGH,
    COUNTRY_MEDIUM,
    COUNTRY_LOW,
    WORLD_HIGH,
    WORLD_MEDIUM,
    WORLD_LOW;

    private class Lod internal constructor(internal val resolution: Int, internal val level: LevelOfDetails)

    fun toResolution(): Int {
        return 15 - this.ordinal
    }

    companion object {

        private val LOD_RANGES = values().map { level -> Lod(level.toResolution(), level) }

        fun fromResolution(resolution: Int): LevelOfDetails {
            for (lod in LOD_RANGES) {
                if (resolution >= lod.resolution) {
                    return lod.level
                }
            }

            return WORLD_LOW
        }
    }
}
