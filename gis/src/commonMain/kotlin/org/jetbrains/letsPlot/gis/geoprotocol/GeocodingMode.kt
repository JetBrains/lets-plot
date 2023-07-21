/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol

enum class GeocodingMode private constructor(private val myValue: String) {
    BY_ID("by_id"),
    BY_NAME("by_geocoding"),
    REVERSE("reverse");

    override fun toString(): String {
        return myValue
    }
}
