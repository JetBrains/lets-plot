/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.livemap.api.ChartSource
import jetbrains.livemap.api.transformValues2Angles
import jetbrains.livemap.api.transformValues2Percents
import jetbrains.livemap.projections.Client
import kotlin.math.PI
import kotlin.math.abs

object Utils {


    fun splitMapPieChart(source: ChartSource): List<MapPieSector> {
        val result = ArrayList<MapPieSector>()
        val angles = transformValues2Angles(source.values)
        var startAngle = 0.0

        for (i in angles.indices) {
            val endAngle = startAngle + angles[i]
            result.add(
                MapPieSector(
                    source.indices[i],
                    "",
                    "",

                    explicitVec(source.lon, source.lat),
                    source.radius,
                    startAngle,
                    endAngle,

                    source.colors[i],
                    source.strokeColor,
                    source.strokeWidth
                )
            )
            startAngle = endAngle
        }

        return result
    }

    fun splitMapBarChart(source: ChartSource, maxAbsValue: Double): List<MapBar> {
        val result = ArrayList<MapBar>()
        val percents = transformValues2Percents(source.values, maxAbsValue)

        val radius = source.radius
        val barCount = percents.size
        val spacing = 0.1 * radius
        val barWidth = (2 * radius - (barCount - 1) * spacing) / barCount

        for (i in percents.indices) {
            val barDimension =  explicitVec<Client>(barWidth, radius * abs(percents[i]))
            val barOffset = explicitVec<Client>(
                (barWidth + spacing) * i - radius,
                if (percents[i] > 0) -barDimension.y else 0.0
            )
            result.add(
                MapBar(
                    source.indices[i],
                    "",
                    "",
                    explicitVec(source.lon, source.lat),
                    source.colors[i],
                    source.strokeColor,
                    source.strokeWidth,
                    barDimension,
                    barOffset
                )
            )
        }
        return result
    }

    fun calculateBBoxes(v: MapObject): List<Rect<LonLat>> {
        return when (v) {
            is MapGeometry -> calculateGeometryBBoxes(v)
            is MapPointGeometry -> calculatePointBBoxes(v)
            else -> throw IllegalStateException("Unsupported MapObject type: ${v::class}")
        }
    }

    private fun calculateGeometryBBoxes(v: MapGeometry): List<Rect<LonLat>> {
        return v.geometry
            ?.run { asMultipolygon().limit() }
            ?: emptyList()

    }

    private fun calculatePointBBoxes(v: MapPointGeometry): List<Rect<LonLat>> {
        return listOf(Rect(v.point, Vec(0,0)))
    }
}