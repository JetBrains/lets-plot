/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

internal object RequestKeys {
    const val PROTOCOL_VERSION = 2
    const val VERSION = "version"
    const val MODE = "mode"
    const val RESOLUTION = "resolution"
    const val FEATURE_OPTIONS = "feature_options"
    const val IDS = "ids"
    const val REGION_QUERIES = "region_queries"
    const val REGION_QUERY_NAMES = "region_query_names"
    const val REGION_QUERY_PARENT = "region_query_parent"
    const val LEVEL = "level"
    const val MAP_REGION_KIND = "kind"
    const val MAP_REGION_VALUES = "values"
    const val NAMESAKE_EXAMPLE_LIMIT = "namesake_example_limit"

    const val FRAGMENTS = "tiles"

    const val AMBIGUITY_RESOLVER = "ambiguity_resolver"
    const val AMBIGUITY_IGNORING_STRATEGY = "ambiguity_resolver_ignoring_strategy"
    const val AMBIGUITY_CLOSEST_COORD = "ambiguity_resolver_closest_coord"
    const val AMBIGUITY_BOX = "ambiguity_resolver_box"

    const val REVERSE_LEVEL = "level"
    const val REVERSE_COORDINATES = "reverse_coordinates"
    const val REVERSE_PARENT = "reverse_parent"

    const val COORDINATE_LON = 0
    const val COORDINATE_LAT = 1

    const val LON_MIN = "min_lon"
    const val LAT_MIN = "min_lat"
    const val LON_MAX = "max_lon"
    const val LAT_MAX = "max_lat"
}
