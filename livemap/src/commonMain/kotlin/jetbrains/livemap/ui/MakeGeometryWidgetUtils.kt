/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.spatial.LonLat

fun createFormattedGeometryString(points: List<Vec<LonLat>>): String {
    var counter = 0
    var lonString = ""
    var latString = ""
    points.forEach {
        if (counter == 5) {
            counter = 0
            lonString += "\n            "
            latString += "\n            "
        }

        counter++

        lonString += "${it.x.trim()}, "
        latString += "${it.y.trim()}, "
    }

    lonString.dropLast(2)
    latString.dropLast(2)

    return "geometry = {\n    'lon': [${lonString}], \n    'lat': [${latString}]\n}"
}

private fun Double.trim(): String {
    return toString()
        .split(".")
        .run {
            "${this[0]}.${if (this[1].length > 6) this[1].substring(0, 6) else this[1]}"
        }
}