/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.makegeometrywidget

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec

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

    return "geometry = {\n    'lon': [${lonString.dropLast(2)}], \n    'lat': [${latString.dropLast(2)}]\n}"
}

private fun Double.trim(): String {
    return toString()
        .split(".")
        .run {
            "${this[0]}.${if (this[1].length > 6) this[1].substring(0, 6) else this[1]}"
        }
}