/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol.json

import org.jetbrains.letsPlot.commons.intern.json.*
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.gis.geoprotocol.GeoRequest
import org.jetbrains.letsPlot.gis.geoprotocol.GeoRequest.*
import org.jetbrains.letsPlot.gis.geoprotocol.GeocodingMode
import org.jetbrains.letsPlot.gis.geoprotocol.GeocodingMode.*
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestKeys.FEATURE_OPTIONS
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestKeys.FRAGMENTS
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestKeys.IDS
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestKeys.MODE
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestKeys.PROTOCOL_VERSION
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestKeys.RESOLUTION
import org.jetbrains.letsPlot.gis.geoprotocol.json.RequestKeys.VERSION
import org.jetbrains.letsPlot.commons.intern.json.FluentObject
import org.jetbrains.letsPlot.commons.intern.json.Obj
import org.jetbrains.letsPlot.commons.intern.json.formatEnum
import org.jetbrains.letsPlot.commons.intern.json.put

object RequestJsonFormatter {
    private const val PARENT_KIND_ID = true
    private const val PARENT_KIND_NAME = false

    fun format(request: GeoRequest): Obj {
        return when (request) {
            is ExplicitSearchRequest -> explicit(request)
            else -> throw IllegalStateException("Unknown request: " + request::class.toString())
        }
    }

    private fun explicit(request: ExplicitSearchRequest): Obj {
        return common(request, BY_ID)
            .put(IDS, request.ids)
            .get()
    }

    private fun common(request: GeoRequest, mode: GeocodingMode): FluentObject =
        FluentObject()
            .put(VERSION, PROTOCOL_VERSION)
            .put(MODE, mode)
            .put(RESOLUTION, request.levelOfDetails?.toResolution())
            .put(FEATURE_OPTIONS, request.features.map { formatEnum(it) })
            .putRemovable(FRAGMENTS, request.fragments?.let {
                val obj = FluentObject()
                it.map { (region, quads) ->
                    obj.put(region, quads.map(QuadKey<LonLat>::key))
                }
                obj
            }
            )
}
