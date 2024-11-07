/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.model

import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.api.GeoObject

object Cities {
    val SPB = GeoObject(
        id="27490597",
        centroid = Vec(30.1751, 59.5439),
        bbox = GeoRectangle(0.0 ,0.0, 0.0, 0.0),
        position = GeoRectangle(0.0 ,0.0, 0.0, 0.0)
    )

    val MOSCOW = GeoObject(
        id = "1686293227",
        centroid = Vec(37.3659, 55.4507),
        bbox = GeoRectangle(0.0 ,0.0, 0.0, 0.0),
        position = GeoRectangle(
            startLongitude = 37.326228171587,
            minLatitude = 55.4913076758385,
            endLongitude = 37.9674276709557,
            maxLatitude = 55.9577719867229
        )
    )

    val BOSTON = GeoObject(
        id = "158809705",
        centroid = Vec(-71.0335, 42.2130),
        bbox = GeoRectangle(0.0 ,0.0, 0.0, 0.0),
        position = GeoRectangle(0.0 ,0.0, 0.0, 0.0)
    )

    val NEW_YORK = GeoObject(
        id = "61785451",
        centroid = Vec(-73.5939, 40.4342),
        bbox = GeoRectangle(0.0 ,0.0, 0.0, 0.0),
        position = GeoRectangle(0.0 ,0.0, 0.0, 0.0)
    )

    val FRISCO = GeoObject(
        id="112175",
        centroid = Vec(-106.10563, 39.58297),
        bbox = GeoRectangle(0.0 ,0.0, 0.0, 0.0),
        position = GeoRectangle(0.0 ,0.0, 0.0, 0.0)
    )

    val USA = GeoObject(
        id = "148838",
        centroid = Vec(-99.74261, 37.25026),
        bbox = GeoRectangle(
            startLongitude = 144.618412256241,
            endLongitude = -64.56484794616701,
            minLatitude = -14.3740922212601,
            maxLatitude = 71.38780832290649
        ),
        position = GeoRectangle(
            startLongitude = -124.733375608921,
            minLatitude = -66.9498561322689,
            endLongitude = 25.1162923872471,
            maxLatitude = 49.3844716250896
        )
    )

    val GERMANY = GeoObject(
        id = "51477",
        centroid = Vec(10.5792501010919, 51.1642031371593),
        bbox = GeoRectangle(
            startLongitude = 5.86631566286087,
            endLongitude = 15.04180803895,
            minLatitude = 47.2701117396355,
            maxLatitude = 55.0585734844208,
        ),
        position = GeoRectangle(0.0 ,0.0, 0.0, 0.0)
    )

    val POLAND = GeoObject(
        id = "49715",
        centroid = Vec(19.156819, 51.919052),
        bbox = GeoRectangle(
            startLongitude = 14.1228845,
            endLongitude = 24.1457833,
            minLatitude = 49.0020462,
            maxLatitude = 54.8361815
        ),
        position = GeoRectangle(0.0 ,0.0, 0.0, 0.0)
    )
}
