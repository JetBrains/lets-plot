/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.math.distance2
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.HoverObject
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.Locator
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import kotlin.math.pow

object PathLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        if (!target.contains(WorldGeometryComponent::class)) {
            return null
        }

        if (isCoordinateInPath(
                renderHelper.posToWorld(coord),
                renderHelper.dimToWorld(target.get<ChartElementComponent>().strokeWidth / 2),
                target.get<WorldGeometryComponent>().geometry.multiLineString
            )
        ) {
            return HoverObject(
                layerIndex = target.get<IndexComponent>().layerIndex,
                index = target.get<IndexComponent>().index,
                distance = 0.0,
                this
            )
        }
        return null
    }

    // Special logic is not yet determined.
    override fun reduce(hoverObjects: Collection<HoverObject>): HoverObject? = hoverObjects.firstOrNull()

    private fun isCoordinateInPath(
        coord: Vec<World>,
        strokeRadius: Scalar<World>,
        multiLineString: MultiLineString<World>
    ): Boolean {
        for (lineString in multiLineString) {
            val bbox = lineString.bbox ?: continue

            if (!bbox.inflate(strokeRadius).contains(coord)) {
                continue
            }
            if (pathContainsCoordinate(coord, lineString, strokeRadius.value)) {
                return true
            }
        }
        return false
    }

    private fun <TypeT> pathContainsCoordinate(
        coord: Vec<TypeT>,
        path: List<Vec<TypeT>>,
        strokeWidth: Double
    ): Boolean {
        for (i in 0 until path.size - 1) {
            if (calculateSquareDistanceToPathSegment(coord, path, i) <= strokeWidth.pow(2.0)) {
                return true
            }
        }
        return false
    }

    private fun <TypeT> calculateSquareDistanceToPathSegment(
        coord: Vec<TypeT>,
        path: List<Vec<TypeT>>,
        segmentNum: Int
    ): Double {
        val next = segmentNum + 1
        val segmentEnd = path[next]
        val segmentStart = path[segmentNum]
        val dx: Double = segmentEnd.x - segmentStart.x
        val dy: Double = segmentEnd.y - segmentStart.y

        val scalar: Double = dx * (coord.x - segmentStart.x) + dy * (coord.y - segmentStart.y)
        if (scalar <= 0) {
            return distance2(coord.x, coord.y, segmentStart.x, segmentStart.y)
        }
        val segmentSquareLength = dx * dx + dy * dy
        val baseSquareLength = scalar * scalar / segmentSquareLength
        return if (baseSquareLength >= segmentSquareLength) {
            distance2(coord.x, coord.y, segmentEnd.x, segmentEnd.y)
        } else distance2(coord.x, coord.y, segmentStart.x, segmentStart.y) - baseSquareLength
    }
}