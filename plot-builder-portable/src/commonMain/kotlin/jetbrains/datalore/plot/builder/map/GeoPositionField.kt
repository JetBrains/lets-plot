/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.map


// column names in data-frames provided by geocoding
object GeoPositionField {

    // fixed columns in 'boundaries' of 'centroids' data frames
    const val POINT_X = "lon"
    const val POINT_X1 = "longitude"
    const val POINT_X2 = "long"
    const val POINT_Y = "lat"
    const val POINT_Y1 = "latitude"

    // fixed columns in 'limits'
    const val RECT_XMIN = "lonmin"
    const val RECT_XMAX = "lonmax"
    const val RECT_YMIN = "latmin"
    const val RECT_YMAX = "latmax"
}
