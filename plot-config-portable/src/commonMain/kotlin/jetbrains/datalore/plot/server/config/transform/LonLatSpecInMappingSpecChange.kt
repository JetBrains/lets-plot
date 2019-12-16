/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.spatial.MercatorUtils.checkLat
import jetbrains.datalore.base.spatial.MercatorUtils.checkLon
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer.DATA
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Layer.MAPPING
import jetbrains.datalore.plot.config.Option.Mapping
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecFinder
import jetbrains.datalore.plot.config.transform.SpecSelector

class LonLatSpecInMappingSpecChange : SpecChange {

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return GeomName.LIVE_MAP == spec[GEOM] &&
                spec.containsKey(MAPPING) &&
                spec[MAPPING] is Map<*, *>
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        //System.out.println("Finding mapping.....");
        // find: aes(... , map_id = lon_lat(lon = 'x', lat = 'y') )
        val finder = SpecFinder(MAPPING, Mapping.MAP_ID)
        val specs = finder.findSpecs(spec)
        if (specs.isEmpty()) {
            //System.out.println("Not found");
            return
        }

        val mapIdMapping = specs[0]
        //System.out.println("Found " + mapIdMapping);

        // check if the value is: lon_lat(lon = 'x', lat = 'y')
        if (mapIdMapping.containsKey(LONLAT_SPEC_KEY)
                && LONLAT_SPEC_VALUE == mapIdMapping[LONLAT_SPEC_KEY]
                && mapIdMapping.containsKey(LON_KEY)
                && mapIdMapping.containsKey(LAT_KEY)) {

            val lonDataKey = mapIdMapping[LON_KEY].toString()
            val latDataKey = mapIdMapping[LAT_KEY].toString()
            val keys = listOf(lonDataKey, latDataKey)

            // Find data spec containing both: lon and lat data keys
            var dataSpec: MutableMap<String, Any>? = null
            if (spec[DATA] is Map<*, *>) {
                val layerDataSpec = spec[DATA] as MutableMap<String, Any>
                if (layerDataSpec.keys.containsAll(keys)) {
                    dataSpec = layerDataSpec
                } else {
                }
            }

            if (dataSpec == null) {
                val list = ctx.getSpecsAbsolute(Plot.DATA)
                if (list.isNotEmpty() && list[0].keys.containsAll(keys)) {
                    dataSpec = list[0] as MutableMap<String, Any>
                }
            }

            //System.out.println("data found: " + dataSpec);
            if (dataSpec == null) {
                throw IllegalArgumentException("Can not find data containing keys: $lonDataKey and $latDataKey")
            }

            val coords =
                concatColumns(
                    dataSpec[lonDataKey] as List<*>,
                    dataSpec[latDataKey] as List<*>
                )

            // add 'lon/lat' data vector
            dataSpec[GENERATED_LONLAT_COLUMN_NAME] = coords
            // update map_id mapping
            val aesSpec = spec[MAPPING] as MutableMap<String, Any>
            aesSpec[Mapping.MAP_ID] =
                GENERATED_LONLAT_COLUMN_NAME
        }
    }

    companion object {
        const val LON_KEY = "lon"
        const val LAT_KEY = "lat"
        const val LONLAT_SPEC_KEY = "name"
        const val LONLAT_SPEC_VALUE = "lon_lat"
        const val GENERATED_LONLAT_COLUMN_NAME = "generated_lonlat"

        fun specSelector(): SpecSelector {
            return SpecSelector.of(Plot.LAYERS)
        }

        private fun concatColumns(lon: List<*>, lat: List<*>): List<String> {
            checkArgument(lon.isNotEmpty(), "Empty longitude data.")
            checkArgument(lat.isNotEmpty(), "Empty latitude data.")
            checkArgument(lon.size == lat.size, "Longitude and latitude have different size")

            val coordinates = ArrayList<String>()
            var i = 0
            val n = lon.size
            while (i < n) {
                coordinates.add(
                    concatCoordinates(
                        lon[i],
                        lat[i]
                    )
                )
                ++i
            }

            return coordinates
        }

        private fun concatCoordinates(lonObj: Any?, latObj: Any?): String {
            val lon =
                toDouble(lonObj)
            val lat =
                toDouble(latObj)

            checkArgument(checkLon(lon), "Invalid longitude: $lon")
            checkArgument(checkLat(lat), "Invalid latitude: $lat")

            return "$lon, $lat"
        }

        private fun toDouble(v: Any?): Double {
            val errorMessage = "Invalid coordinate data format"
            checkArgument(v is String || v is Number, errorMessage)

            if (v is String) {
                try {
                    return v.toDouble()
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException(errorMessage, e)
                }

            }

            return (v as Number).toDouble()
        }
    }
}
