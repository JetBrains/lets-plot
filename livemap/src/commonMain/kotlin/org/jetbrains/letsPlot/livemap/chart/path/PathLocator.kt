/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.math.isOnSegment
import org.jetbrains.letsPlot.commons.intern.math.projection
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiLineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.toDoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.toVec
import org.jetbrains.letsPlot.commons.intern.util.ClosestPointChecker
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.HoverObject
import org.jetbrains.letsPlot.livemap.chart.HoverObjectKind
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.Locator
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper

object PathLocator : Locator {
    override fun search(coord: Vec<Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        if (!target.contains(WorldGeometryComponent::class)) {
            return null
        }
        val cursorCoord = renderHelper.posToWorld(coord)
        val pointChecker = ClosestPointChecker(cursorCoord.x, cursorCoord.y)
        val candidate = tooltipPosition(
            cursorCoord,
            target.get<WorldGeometryComponent>().geometry.multiLineString,
            pointChecker
        )
        if (candidate != null) {
            return HoverObject(
                kind = HoverObjectKind.PATH,
                layerIndex = target.get<IndexComponent>().layerIndex,
                index = target.get<IndexComponent>().index,
                distance = renderHelper.dimToClient(pointChecker.distance).value,
                this,
                targetPosition = renderHelper.worldToPos(candidate).toDoubleVector(),
            )
        }
        return null
    }

    // Special logic is not yet determined.
    override fun reduce(hoverObjects: Collection<HoverObject>): HoverObject? = hoverObjects.minByOrNull(HoverObject::distance)

    private fun tooltipPosition(
        coord: Vec<World>,
        multiLineString: MultiLineString<World>,
        pointChecker: ClosestPointChecker
    ): Vec<World>? {
        var candidate: Vec<World>? = null
        for (lineString in multiLineString) {
            lineString.toList().asSequence().windowed(2).forEach() {
                val p1 = it[0]
                val p2 = it[1]

                if (isOnSegment(coord, p1, p2)) {
                    val targetPointCoord = projection(coord.x, coord.y, p1.x, p1.y, p2.x, p2.y)
                    if (pointChecker.check(targetPointCoord)) {
                        candidate = targetPointCoord.toVec()
                    }
                } else if (pointChecker.check(p1.toDoubleVector())) {
                    candidate = p1
                }
            }
        }

        return candidate
    }

    private fun <TypeT> isOnSegment(p: Vec<TypeT>, l1: Vec<TypeT>, l2: Vec<TypeT>): Boolean {
        return isOnSegment(p.x, p.y, l1.x, l1.y, l2.x, l2.y)
    }
}